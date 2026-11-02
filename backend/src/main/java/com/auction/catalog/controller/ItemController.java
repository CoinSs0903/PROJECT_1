package com.auction.catalog.controller;

import com.auction.catalog.dto.CreateItemRequest;
import com.auction.catalog.dto.ItemResponseDto;
import com.auction.catalog.entity.Category;
import com.auction.catalog.service.CatalogService;
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
@RequestMapping("/items")
public class ItemController {

    private final CatalogService catalogService;

    public ItemController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public String listMyItems(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            List<ItemResponseDto> items = catalogService.getItemsBySeller(userDetails.getId());
            model.addAttribute("items", items);
        }
        return "catalog/my-items";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("itemRequest")) {
            model.addAttribute("itemRequest", new CreateItemRequest());
        }
        model.addAttribute("categories", catalogService.getAllCategories());
        return "catalog/item-form";
    }

    @PostMapping
    public String createItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute("itemRequest") CreateItemRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", catalogService.getAllCategories());
            return "catalog/item-form";
        }
        try {
            catalogService.createItem(userDetails.getId(), request);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng món hàng thành công!");
            return "redirect:/items";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/items/new";
        }
    }

    @GetMapping("/{id}")
    public String viewItemDetail(@PathVariable("id") Long id, Model model) {
        ItemResponseDto item = catalogService.getItemDetail(id);
        model.addAttribute("item", item);
        return "catalog/item-detail";
    }
}

