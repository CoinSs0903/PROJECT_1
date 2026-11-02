package com.auction.catalog.controller;

import com.auction.catalog.dto.CreateItemRequest;
import com.auction.catalog.dto.ItemResponseDto;
import com.auction.catalog.entity.Category;
import com.auction.catalog.service.CatalogService;
import com.auction.common.dto.ApiResponse;
import com.auction.user.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CatalogApiController {

    private final CatalogService catalogService;

    public CatalogApiController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<Category>> getCategories() {
        return ApiResponse.ok(catalogService.getAllCategories());
    }

    @GetMapping("/items")
    public ApiResponse<List<ItemResponseDto>> getMyItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.ok(catalogService.getItemsBySeller(userDetails.getId()));
    }

    @PostMapping("/items")
    public ApiResponse<ItemResponseDto> createItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @Valid @RequestBody CreateItemRequest request) {
        return ApiResponse.ok("Tạo món hàng thành công", catalogService.createItem(userDetails.getId(), request));
    }

    @GetMapping("/items/{id}")
    public ApiResponse<ItemResponseDto> getItemDetail(@PathVariable("id") Long id) {
        return ApiResponse.ok(catalogService.getItemDetail(id));
    }
}

