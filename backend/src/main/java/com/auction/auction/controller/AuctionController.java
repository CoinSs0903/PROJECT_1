package com.auction.auction.controller;

import com.auction.auction.dto.AuctionDetailDto;
import com.auction.auction.dto.AuctionResponseDto;
import com.auction.auction.dto.CreateAuctionRequest;
import com.auction.auction.entity.Auction;
import com.auction.auction.service.AuctionService;
import com.auction.catalog.dto.ItemResponseDto;
import com.auction.catalog.service.CatalogService;
import com.auction.order.entity.Order;
import com.auction.user.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuctionController {

    private final AuctionService auctionService;
    private final CatalogService catalogService;

    public AuctionController(AuctionService auctionService, CatalogService catalogService) {
        this.auctionService = auctionService;
        this.catalogService = catalogService;
    }

    @GetMapping("/")
    public String home(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "categoryId", required = false) Long categoryId,
                       @RequestParam(value = "minPrice", required = false) Long minPrice,
                       @RequestParam(value = "maxPrice", required = false) Long maxPrice,
                       @RequestParam(value = "sortBy", required = false, defaultValue = "newest") String sortBy,
                       Model model) {
        List<AuctionResponseDto> auctions = auctionService.searchAuctions(keyword, categoryId, minPrice, maxPrice, sortBy);
        model.addAttribute("auctions", auctions);
        model.addAttribute("categories", catalogService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortBy", sortBy);
        return "index";
    }

    @GetMapping("/auctions")
    public String listAuctions(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryId", required = false) Long categoryId,
                               @RequestParam(value = "minPrice", required = false) Long minPrice,
                               @RequestParam(value = "maxPrice", required = false) Long maxPrice,
                               @RequestParam(value = "sortBy", required = false, defaultValue = "newest") String sortBy,
                               Model model) {
        List<AuctionResponseDto> auctions = auctionService.searchAuctions(keyword, categoryId, minPrice, maxPrice, sortBy);
        model.addAttribute("auctions", auctions);
        model.addAttribute("categories", catalogService.getAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sortBy", sortBy);
        return "auction/list";
    }

    @GetMapping("/auctions/{id}")
    public String showAuctionDetail(@PathVariable("id") Long id,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    Model model) {
        Long currentUserId = userDetails != null ? userDetails.getId() : null;
        AuctionDetailDto auction = auctionService.getAuctionDetail(id, currentUserId);
        model.addAttribute("auction", auction);
        model.addAttribute("currentUserId", currentUserId);
        return "auction/detail";
    }

    @GetMapping("/auctions/create")
    public String showCreateAuctionForm(@RequestParam("itemId") Long itemId,
                                        @AuthenticationPrincipal CustomUserDetails userDetails,
                                        Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        if (!model.containsAttribute("auctionRequest")) {
            CreateAuctionRequest req = new CreateAuctionRequest();
            req.setItemId(itemId);
            req.setStepPrice(10000L);
            req.setDurationDays(3);
            model.addAttribute("auctionRequest", req);
        }
        ItemResponseDto item = catalogService.getItemDetail(itemId);
        if (!item.getSellerId().equals(userDetails.getId())) {
            return "redirect:/items?error=unauthorized";
        }
        model.addAttribute("item", item);
        return "auction/create";
    }

    @PostMapping("/auctions/create")
    public String createAuction(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @Valid @ModelAttribute("auctionRequest") CreateAuctionRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            ItemResponseDto item = catalogService.getItemDetail(request.getItemId());
            model.addAttribute("item", item);
            return "auction/create";
        }
        try {
            Auction created = auctionService.createAuction(userDetails.getId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Phiên đấu giá đã được tạo thành công!");
            return "redirect:/auctions/" + created.getId();
        } catch (Exception e) {
            ItemResponseDto item = catalogService.getItemDetail(request.getItemId());
            model.addAttribute("item", item);
            model.addAttribute("errorMessage", e.getMessage());
            return "auction/create";
        }
    }

    @PostMapping("/auctions/{id}/buy-it-now")
    public String buyItNow(@PathVariable("id") Long id,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            Order order = auctionService.executeBuyItNow(id, userDetails.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Mua ngay thành công! Vui lòng hoàn tất đơn hàng #" + order.getId());
            return "redirect:/auctions/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auctions/" + id;
        }
    }

    @PostMapping("/auctions/{id}/cancel")
    public String cancelAuction(@PathVariable("id") Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        try {
            auctionService.cancelAuction(id, userDetails.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Đã huỷ phiên đấu giá thành công.");
            return "redirect:/auctions/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/auctions/" + id;
        }
    }
}
