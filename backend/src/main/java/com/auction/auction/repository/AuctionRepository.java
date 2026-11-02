package com.auction.auction.repository;

import com.auction.auction.entity.Auction;
import com.auction.auction.entity.AuctionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    Optional<Auction> findByIdForUpdate(@Param("id") Long id);

    List<Auction> findByStatusOrderByCreatedAtDesc(AuctionStatus status);

    List<Auction> findByItemIdOrderByCreatedAtDesc(Long itemId);

    @Query("SELECT a FROM Auction a WHERE a.status = 'ACTIVE' AND a.endTime <= :now")
    List<Auction> findExpiredActiveAuctions(@Param("now") LocalDateTime now);

    @Query("SELECT a FROM Auction a WHERE a.status = 'SCHEDULED' AND a.startTime <= :now")
    List<Auction> findDueScheduledAuctions(@Param("now") LocalDateTime now);
}
