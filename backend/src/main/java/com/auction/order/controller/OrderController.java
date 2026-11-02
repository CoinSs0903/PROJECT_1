package com.auction.order.controller;

import com.auction.auction.entity.Auction;
import com.auction.common.dto.ApiResponse;
import com.auction.common.exception.ErrorCode;
import com.auction.order.dto.OrderResponseDto;
import com.auction.order.entity.Order;
import com.auction.order.service.OrderService;
import com.auction.user.security.CustomUserDetails;
import com.auction.wallet.facade.WalletFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final WalletFacade walletFacade;

    public OrderController(OrderService orderService, WalletFacade walletFacade) {
        this.orderService = orderService;
        this.walletFacade = walletFacade;
    }

    @GetMapping("/orders")
    public String listMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @RequestParam(value = "tab", required = false, defaultValue = "purchases") String tab,
                               Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        List<OrderResponseDto> purchases = orderService.getMyPurchases(userDetails.getId());
        List<OrderResponseDto> sales = orderService.getMySales(userDetails.getId());
        model.addAttribute("purchases", purchases);
        model.addAttribute("sales", sales);
        model.addAttribute("activeTab", tab);
        return "order/index";
    }

    @GetMapping("/orders/{id}")
    public String showOrderDetail(@PathVariable("id") Long id,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        OrderResponseDto order = orderService.getOrderDetail(id, userDetails.getId());
        long walletBalance = walletFacade.getAvailableBalance(userDetails.getId());
        model.addAttribute("order", order);
        model.addAttribute("walletBalance", walletBalance);
        return "order/detail";
    }

    @PostMapping("/orders/{id}/pay")
    public String payOrder(@PathVariable("id") Long id,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam("shippingAddress") String shippingAddress,
                           RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            orderService.checkoutAndPay(id, userDetails.getId(), shippingAddress);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán đơn hàng thành công qua Ví bảo chứng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping("/orders/{id}/ship")
    public String shipOrder(@PathVariable("id") Long id,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam("trackingNumber") String trackingNumber,
                            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            orderService.shipOrder(id, userDetails.getId(), trackingNumber);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật mã vận đơn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping("/orders/{id}/confirm")
    public String confirmReceived(@PathVariable("id") Long id,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            orderService.confirmReceived(id, userDetails.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Xác nhận nhận hàng thành công! Đơn hàng hoàn tất và tiền đã được giải ngân cho người bán.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders/" + id;
    }

    @PostMapping("/auctions/{id}/relist")
    public String relist(@PathVariable("id") Long auctionId,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            Auction newAuction = orderService.relistAuction(auctionId, userDetails.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Đã mở bán lại (Relist) phiên đấu giá thành công!");
            return "redirect:/auctions/" + newAuction.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/items";
        }
    }
}
