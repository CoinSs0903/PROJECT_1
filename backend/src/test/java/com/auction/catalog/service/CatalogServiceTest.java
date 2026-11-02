package com.auction.catalog.service;

import com.auction.catalog.dto.CreateItemRequest;
import com.auction.catalog.dto.ItemResponseDto;
import com.auction.catalog.entity.Category;
import com.auction.catalog.entity.Item;
import com.auction.catalog.entity.ItemCondition;
import com.auction.catalog.entity.ItemImage;
import com.auction.catalog.repository.CategoryRepository;
import com.auction.catalog.repository.ItemImageRepository;
import com.auction.catalog.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CatalogServiceTest {

    private InMemoryCategoryRepository categoryRepository;
    private InMemoryItemRepository itemRepository;
    private InMemoryItemImageRepository itemImageRepository;
    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        categoryRepository = new InMemoryCategoryRepository();
        itemRepository = new InMemoryItemRepository();
        itemImageRepository = new InMemoryItemImageRepository();
        catalogService = new CatalogService(categoryRepository, itemRepository, itemImageRepository);

        Category cat = new Category();
        cat.setName("Đồng Hồ Cơ");
        cat.setSlug("dong-ho-co");
        categoryRepository.save(cat);
    }

    @Test
    @DisplayName("Tạo món hàng mới thành công với danh mục và album ảnh")
    void testCreateItemSuccess() {
        Category cat = categoryRepository.findAll().get(0);

        CreateItemRequest request = new CreateItemRequest();
        request.setCategoryId(cat.getId());
        request.setTitle("Rolex Submariner Date");
        request.setDescription("Đồng hồ mới 100% nguyên hộp sổ thẻ");
        request.setItemCondition(ItemCondition.NEW);
        request.setMainImageUrl("https://example.com/rolex.jpg");
        request.setAdditionalImageUrls(List.of("https://example.com/img1.jpg", "https://example.com/img2.jpg"));

        ItemResponseDto response = catalogService.createItem(10L, request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Rolex Submariner Date", response.getTitle());
        assertEquals("Đồng Hồ Cơ", response.getCategoryName());
        assertEquals(ItemCondition.NEW, response.getItemCondition());
        assertEquals(2, response.getImageUrls().size());
    }

    @Test
    @DisplayName("Lấy danh sách món hàng theo người bán")
    void testGetItemsBySeller() {
        Category cat = categoryRepository.findAll().get(0);

        CreateItemRequest r1 = new CreateItemRequest();
        r1.setCategoryId(cat.getId());
        r1.setTitle("Món 1");
        catalogService.createItem(10L, r1);

        CreateItemRequest r2 = new CreateItemRequest();
        r2.setCategoryId(cat.getId());
        r2.setTitle("Món 2");
        catalogService.createItem(10L, r2);

        CreateItemRequest r3 = new CreateItemRequest();
        r3.setCategoryId(cat.getId());
        r3.setTitle("Món của người khác");
        catalogService.createItem(20L, r3);

        List<ItemResponseDto> sellerItems = catalogService.getItemsBySeller(10L);
        assertEquals(2, sellerItems.size());
    }

    // --- In-memory test repositories ---
    private static class InMemoryCategoryRepository implements CategoryRepository {
        private final Map<Long, Category> db = new ConcurrentHashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override public List<Category> findByParentIdIsNullOrderByDisplayOrderAsc() { return findAll(); }
        @Override public List<Category> findByParentIdOrderByDisplayOrderAsc(Long parentId) { return Collections.emptyList(); }
        @Override public Optional<Category> findBySlug(String slug) {
            return db.values().stream().filter(c -> slug.equals(c.getSlug())).findFirst();
        }
        @Override public <S extends Category> S save(S entity) {
            if (entity.getId() == null) entity.setId(idGen.getAndIncrement());
            db.put(entity.getId(), entity);
            return entity;
        }
        @Override public Optional<Category> findById(Long aLong) { return Optional.ofNullable(db.get(aLong)); }
        @Override public boolean existsById(Long aLong) { return db.containsKey(aLong); }
        @Override public List<Category> findAll() { return new ArrayList<>(db.values()); }
        @Override public List<Category> findAllById(Iterable<Long> longs) { return null; }
        @Override public long count() { return db.size(); }
        @Override public void deleteById(Long aLong) { db.remove(aLong); }
        @Override public void delete(Category entity) { db.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> longs) {}
        @Override public void deleteAll(Iterable<? extends Category> entities) {}
        @Override public void deleteAll() { db.clear(); }
        @Override public void flush() {}
        @Override public <S extends Category> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Category> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<Category> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public Category getOne(Long aLong) { return db.get(aLong); }
        @Override public Category getById(Long aLong) { return db.get(aLong); }
        @Override public Category getReferenceById(Long aLong) { return db.get(aLong); }
        @Override public <S extends Category> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Category> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends Category> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends Category> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Category> long count(Example<S> example) { return 0; }
        @Override public <S extends Category> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Category, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public <S extends Category> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public List<Category> findAll(Sort sort) { return null; }
        @Override public Page<Category> findAll(Pageable pageable) { return null; }
    }

    private static class InMemoryItemRepository implements ItemRepository {
        private final Map<Long, Item> db = new ConcurrentHashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public List<Item> findBySellerIdOrderByCreatedAtDesc(Long sellerId) {
            return db.values().stream().filter(i -> Objects.equals(i.getSellerId(), sellerId)).collect(Collectors.toList());
        }
        @Override
        public List<Item> findByCategoryIdOrderByCreatedAtDesc(Long categoryId) {
            return db.values().stream().filter(i -> Objects.equals(i.getCategoryId(), categoryId)).collect(Collectors.toList());
        }
        @Override public <S extends Item> S save(S entity) {
            if (entity.getId() == null) entity.setId(idGen.getAndIncrement());
            db.put(entity.getId(), entity);
            return entity;
        }
        @Override public Optional<Item> findById(Long aLong) { return Optional.ofNullable(db.get(aLong)); }
        @Override public boolean existsById(Long aLong) { return db.containsKey(aLong); }
        @Override public List<Item> findAll() { return new ArrayList<>(db.values()); }
        @Override public List<Item> findAllById(Iterable<Long> longs) { return null; }
        @Override public long count() { return db.size(); }
        @Override public void deleteById(Long aLong) { db.remove(aLong); }
        @Override public void delete(Item entity) { db.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> longs) {}
        @Override public void deleteAll(Iterable<? extends Item> entities) {}
        @Override public void deleteAll() { db.clear(); }
        @Override public void flush() {}
        @Override public <S extends Item> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Item> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<Item> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public Item getOne(Long aLong) { return db.get(aLong); }
        @Override public Item getById(Long aLong) { return db.get(aLong); }
        @Override public Item getReferenceById(Long aLong) { return db.get(aLong); }
        @Override public <S extends Item> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Item> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends Item> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends Item> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Item> long count(Example<S> example) { return 0; }
        @Override public <S extends Item> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Item, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public <S extends Item> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public List<Item> findAll(Sort sort) { return null; }
        @Override public Page<Item> findAll(Pageable pageable) { return null; }
    }

    private static class InMemoryItemImageRepository implements ItemImageRepository {
        private final Map<Long, ItemImage> db = new ConcurrentHashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public List<ItemImage> findByItemIdOrderByDisplayOrderAsc(Long itemId) {
            return db.values().stream().filter(img -> Objects.equals(img.getItemId(), itemId)).collect(Collectors.toList());
        }
        @Override
        public void deleteByItemId(Long itemId) {
            db.values().removeIf(img -> Objects.equals(img.getItemId(), itemId));
        }
        @Override public <S extends ItemImage> S save(S entity) {
            if (entity.getId() == null) entity.setId(idGen.getAndIncrement());
            db.put(entity.getId(), entity);
            return entity;
        }
        @Override public Optional<ItemImage> findById(Long aLong) { return Optional.ofNullable(db.get(aLong)); }
        @Override public boolean existsById(Long aLong) { return db.containsKey(aLong); }
        @Override public List<ItemImage> findAll() { return new ArrayList<>(db.values()); }
        @Override public List<ItemImage> findAllById(Iterable<Long> longs) { return null; }
        @Override public long count() { return db.size(); }
        @Override public void deleteById(Long aLong) { db.remove(aLong); }
        @Override public void delete(ItemImage entity) { db.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> longs) {}
        @Override public void deleteAll(Iterable<? extends ItemImage> entities) {}
        @Override public void deleteAll() { db.clear(); }
        @Override public void flush() {}
        @Override public <S extends ItemImage> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends ItemImage> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<ItemImage> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public ItemImage getOne(Long aLong) { return db.get(aLong); }
        @Override public ItemImage getById(Long aLong) { return db.get(aLong); }
        @Override public ItemImage getReferenceById(Long aLong) { return db.get(aLong); }
        @Override public <S extends ItemImage> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends ItemImage> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends ItemImage> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends ItemImage> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends ItemImage> long count(Example<S> example) { return 0; }
        @Override public <S extends ItemImage> boolean exists(Example<S> example) { return false; }
        @Override public <S extends ItemImage, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public <S extends ItemImage> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public List<ItemImage> findAll(Sort sort) { return null; }
        @Override public Page<ItemImage> findAll(Pageable pageable) { return null; }
    }
}
