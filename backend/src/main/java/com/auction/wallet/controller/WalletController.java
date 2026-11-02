package com.auction.wallet.controller;

import com.auction.common.dto.ApiResponse;
import com.auction.user.security.CustomUserDetails;
import com.auction.wallet.entity.Transaction;
import com.auction.wallet.service.WalletService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/wallet")
    public String viewWallet(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getId();
        long balance = walletService.getAvailableBalance(userId);
        List<Transaction> transactions = walletService.getTransactions(userId);

        model.addAttribute("balance", balance);
        model.addAttribute("transactions", transactions);
        return "wallet/index";
    }

    @PostMapping("/wallet/topup")
    public String topUp(@AuthenticationPrincipal CustomUserDetails userDetails,
                        @RequestParam("amount") long amount,
                        RedirectAttributes redirectAttributes) {
        try {
            String idempotencyKey = "TOPUP_" + UUID.randomUUID();
            walletService.topUp(userDetails.getId(), amount, idempotencyKey);
            redirectAttributes.addFlashAttribute("successMessage", "Nạp tiền thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/wallet";
    }

    @PostMapping("/wallet/withdraw")
    public String withdraw(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam("amount") long amount,
                           @RequestParam("bankAccount") String bankAccount,
                           RedirectAttributes redirectAttributes) {
        try {
            String idempotencyKey = "WITHDRAW_" + UUID.randomUUID();
            walletService.withdraw(userDetails.getId(), amount, bankAccount, idempotencyKey);
            redirectAttributes.addFlashAttribute("successMessage", "Yêu cầu rút tiền thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/wallet";
    }

    @GetMapping("/api/wallet/balance")
    @ResponseBody
    public ApiResponse<Long> getBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.ok(walletService.getAvailableBalance(userDetails.getId()));
    }
}

