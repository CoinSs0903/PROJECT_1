package com.auction.system.controller;

import com.auction.auction.repository.AuctionRepository;
import com.auction.audit.entity.AuditLog;
import com.auction.audit.repository.AuditLogRepository;
import com.auction.complaint.entity.Complaint;
import com.auction.complaint.service.ComplaintService;
import com.auction.order.repository.OrderRepository;
import com.auction.user.entity.User;
import com.auction.user.entity.UserStatus;
import com.auction.user.repository.UserRepository;
import com.auction.user.security.CustomUserDetails;
import com.auction.wallet.entity.Wallet;
import com.auction.wallet.entity.WalletType;
import com.auction.wallet.repository.WalletRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final OrderRepository orderRepository;
    private final WalletRepository walletRepository;
    private final AuditLogRepository auditLogRepository;
    private final ComplaintService complaintService;

    public AdminController(
            UserRepository userRepository,
            AuctionRepository auctionRepository,
            OrderRepository orderRepository,
            WalletRepository walletRepository,
            AuditLogRepository auditLogRepository,
            ComplaintService complaintService) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.orderRepository = orderRepository;
        this.walletRepository = walletRepository;
        this.auditLogRepository = auditLogRepository;
        this.complaintService = complaintService;
    }

    @GetMapping
    public String dashboard(Model model) {
        long totalUsers = userRepository.count();
        long totalAuctions = auctionRepository.count();
        long totalOrders = orderRepository.count();
        long systemRevenue = walletRepository.findByWalletType(WalletType.SYSTEM_REVENUE)
                .map(Wallet::getAvailableBalance).orElse(0L);

        List<User> users = userRepository.findAll();
        List<Complaint> pendingComplaints = complaintService.getPendingComplaints();
        List<AuditLog> auditLogs = auditLogRepository.findTop50ByOrderByCreatedAtDesc();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalAuctions", totalAuctions);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("systemRevenue", systemRevenue);
        model.addAttribute("users", users);
        model.addAttribute("complaints", pendingComplaints);
        model.addAttribute("auditLogs", auditLogs);

        return "admin/dashboard";
    }

    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable("id") Long userId,
                          RedirectAttributes redirectAttributes) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setStatus(UserStatus.BANNED);
            userRepository.save(u);
        });
        redirectAttributes.addFlashAttribute("successMessage", "Đã khóa tài khoản #" + userId);
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable("id") Long userId,
                            RedirectAttributes redirectAttributes) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setStatus(UserStatus.ACTIVE);
            userRepository.save(u);
        });
        redirectAttributes.addFlashAttribute("successMessage", "Đã kích hoạt lại tài khoản #" + userId);
        return "redirect:/admin";
    }

    @PostMapping("/complaints/{id}/resolve")
    public String resolveComplaint(@PathVariable("id") Long complaintId,
                                   @AuthenticationPrincipal CustomUserDetails adminDetails,
                                   @RequestParam("resolutionNote") String note,
                                   @RequestParam(value = "refundBuyer", defaultValue = "false") boolean refundBuyer,
                                   RedirectAttributes redirectAttributes) {
        Long adminId = adminDetails != null ? adminDetails.getId() : 0L;
        complaintService.resolveComplaint(complaintId, adminId, note, refundBuyer);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xử lý khiếu nại #" + complaintId);
        return "redirect:/admin";
    }
}

