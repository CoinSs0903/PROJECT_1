package com.auction.catalog.service;

import com.auction.catalog.dto.CreateItemRequest;
import com.auction.catalog.dto.ItemResponseDto;
import com.auction.catalog.entity.Category;
import com.auction.catalog.entity.Item;
import com.auction.catalog.entity.ItemImage;
import com.auction.catalog.repository.CategoryRepository;
import com.auction.catalog.repository.ItemImageRepository;
import com.auction.catalog.repository.ItemRepository;
import com.auction.common.exception.AppException;
import com.auction.common.exception.ErrorCode;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;

    public CatalogService(CategoryRepository categoryRepository,
                          ItemRepository itemRepository,
                          ItemImageRepository itemImageRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.itemImageRepository = itemImageRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initSampleCategoriesIfEmpty() {
        if (categoryRepository.count() == 0) {
            saveCategory("Điện Thoại & Máy Tính Bảng", "dien-thoai-may-tinh-bang", 1);
            saveCategory("Laptop & Máy Tính", "laptop-may-tinh", 2);
            saveCategory("Đồng Hồ Cơ & Phụ Kiện", "dong-ho-co-phu-kien", 3);
            saveCategory("Máy Ảnh & Thiết Bị Quang Học", "may-anh-thiet-bi-quang-hoc", 4);
            saveCategory("Đồ Cổ & Đồ Sưu Tầm", "do-co-do-suu-tam", 5);
            saveCategory("Thời Trang & Túi Hiệu", "thoi-trang-tui-hieu", 6);
        }
    }

    private void saveCategory(String name, String slug, int order) {
        Category c = new Category();
        c.setName(name);
        c.setSlug(slug);
        c.setDisplayOrder(order);
        categoryRepository.save(c);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional
    public ItemResponseDto createItem(Long sellerId, CreateItemRequest request) {
        Category category = getCategoryById(request.getCategoryId());

        Item item = new Item();
        item.setSellerId(sellerId);
        item.setCategoryId(category.getId());
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setItemCondition(request.getItemCondition());
        item.setMainImageUrl(request.getMainImageUrl() != null && !request.getMainImageUrl().isBlank()
                ? request.getMainImageUrl()
                : "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=60");

        Item savedItem = itemRepository.save(item);

        if (request.getAdditionalImageUrls() != null) {
            int order = 1;
            for (String url : request.getAdditionalImageUrls()) {
                if (url != null && !url.isBlank()) {
                    ItemImage img = new ItemImage();
                    img.setItemId(savedItem.getId());
                    img.setImageUrl(url);
                    img.setDisplayOrder(order++);
                    itemImageRepository.save(img);
                }
            }
        }

        List<String> images = itemImageRepository.findByItemIdOrderByDisplayOrderAsc(savedItem.getId())
                .stream().map(ItemImage::getImageUrl).collect(Collectors.toList());

        return ItemResponseDto.fromEntity(savedItem, category.getName(), images);
    }

    @Transactional
    public ItemResponseDto updateItem(Long sellerId, Long itemId, CreateItemRequest request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        if (!item.getSellerId().equals(sellerId)) {
            throw new AppException(ErrorCode.ITEM_NOT_OWNED);
        }

        Category category = getCategoryById(request.getCategoryId());
        item.setCategoryId(category.getId());
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setItemCondition(request.getItemCondition());
        if (request.getMainImageUrl() != null && !request.getMainImageUrl().isBlank()) {
            item.setMainImageUrl(request.getMainImageUrl());
        }

        Item updatedItem = itemRepository.save(item);

        List<String> images = itemImageRepository.findByItemIdOrderByDisplayOrderAsc(updatedItem.getId())
                .stream().map(ItemImage::getImageUrl).collect(Collectors.toList());

        return ItemResponseDto.fromEntity(updatedItem, category.getName(), images);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> getItemsBySeller(Long sellerId) {
        return itemRepository.findBySellerIdOrderByCreatedAtDesc(sellerId).stream()
                .map(item -> {
                    String catName = categoryRepository.findById(item.getCategoryId())
                            .map(Category::getName).orElse("Khác");
                    List<String> images = itemImageRepository.findByItemIdOrderByDisplayOrderAsc(item.getId())
                            .stream().map(ItemImage::getImageUrl).collect(Collectors.toList());
                    return ItemResponseDto.fromEntity(item, catName, images);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemResponseDto getItemDetail(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));
        String catName = categoryRepository.findById(item.getCategoryId())
                .map(Category::getName).orElse("Khác");
        List<String> images = itemImageRepository.findByItemIdOrderByDisplayOrderAsc(item.getId())
                .stream().map(ItemImage::getImageUrl).collect(Collectors.toList());
        return ItemResponseDto.fromEntity(item, catName, images);
    }
}

