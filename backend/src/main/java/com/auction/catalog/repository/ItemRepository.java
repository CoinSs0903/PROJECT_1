package com.auction.catalog.repository;

import com.auction.catalog.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    List<Item> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);
}

