package com.auction.auction.controller;

import com.auction.auction.dto.AuctionDetailDto;
import com.auction.auction.dto.AuctionResponseDto;
import com.auction.auction.dto.CreateAuctionRequest;
import com.auction.auction.entity.Auction;
import com.auction.auction.service.AuctionService;
import com.auction.common.dto.ApiResponse;
import com.auction.common.exception.ErrorCode;
import com.auction.order.entity.Order;
import com.auction.user.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionApiController {

    private final AuctionService auctionService;

    public AuctionApiController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuctionResponseDto>>> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "minPrice", required = false) Long minPrice,
            @RequestParam(value = "maxPrice", required = false) Long maxPrice,
            @RequestParam(value = "sortBy", required = false, defaultValue = "newest") String sortBy) {
        List<AuctionResponseDto> list = auctionService.searchAuctions(keyword, categoryId, minPrice, maxPrice, sortBy);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuctionDetailDto>> getDetail(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails != null ? userDetails.getId() : null;
        AuctionDetailDto detail = auctionService.getAuctionDetail(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.ok(detail));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Auction>> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateAuctionRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "Bạn cần đăng nhập để tạo phiên đấu giá"));
        }
        Auction created = auctionService.createAuction(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Tạo phiên đấu giá thành công", created));
    }

    @PostMapping("/{id}/buy-it-now")
    public ResponseEntity<ApiResponse<Order>> buyItNow(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "Bạn cần đăng nhập"));
        }
        Order order = auctionService.executeBuyItNow(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Mua ngay thành công", order));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.error(ErrorCode.UNAUTHORIZED, "Bạn cần đăng nhập"));
        }
        auctionService.cancelAuction(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.ok("Đã huỷ phiên đấu giá", null));
    }
}

