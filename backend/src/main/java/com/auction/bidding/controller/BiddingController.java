package com.auction.bidding.controller;

import com.auction.bidding.dto.BidResultDto;
import com.auction.bidding.dto.PlaceBidRequest;
import com.auction.bidding.service.ProxyBiddingService;
import com.auction.common.dto.ApiResponse;
import com.auction.common.exception.ErrorCode;
import com.auction.user.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BiddingController {

    private final ProxyBiddingService proxyBiddingService;

    public BiddingController(ProxyBiddingService proxyBiddingService) {
        this.proxyBiddingService = proxyBiddingService;
    }

    @PostMapping("/api/auctions/{id}/bids")
    @ResponseBody
    public ResponseEntity<ApiResponse<BidResultDto>> placeBidApi(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PlaceBidRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "Bạn cần đăng nhập để đặt giá."));
        }
        try {
            BidResultDto result = proxyBiddingService.processBid(id, userDetails.getId(), request.getMaxAmount());
            return ResponseEntity.ok(ApiResponse.ok(result.getMessage(), result));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.BAD_REQUEST, e.getMessage()));
        }
    }

    @PostMapping("/auctions/{id}/bid")
    public String placeBidWeb(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("maxAmount") Long maxAmount,
            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            BidResultDto result = proxyBiddingService.processBid(id, userDetails.getId(), maxAmount);
            if (result.isLeading()) {
                redirectAttributes.addFlashAttribute("successMessage", result.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("warningMessage", result.getMessage());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/auctions/" + id;
    }

    @PostMapping("/auctions/{id}/retract")
    public String retractBidWeb(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "reason", required = false, defaultValue = "Nhầm lẫn số tiền") String reason,
            RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            proxyBiddingService.retractBid(id, userDetails.getId(), reason);
            redirectAttributes.addFlashAttribute("successMessage", "Bạn đã rút lại giá thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/auctions/" + id;
    }

    @PostMapping("/api/auctions/{id}/retract")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> retractBidApi(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "reason", required = false, defaultValue = "Nhầm lẫn số tiền") String reason) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "Bạn cần đăng nhập"));
        }
        try {
            proxyBiddingService.retractBid(id, userDetails.getId(), reason);
            return ResponseEntity.ok(ApiResponse.ok("Rút giá thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ErrorCode.BAD_REQUEST, e.getMessage()));
        }
    }
}

