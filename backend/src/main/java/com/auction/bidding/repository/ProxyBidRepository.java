package com.auction.bidding.repository;

import com.auction.bidding.entity.ProxyBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProxyBidRepository extends JpaRepository<ProxyBid, Long> {
    Optional<ProxyBid> findByAuctionIdAndBidderId(Long auctionId, Long bidderId);
    Optional<ProxyBid> findTopByAuctionIdOrderByMaxAmountDescCreatedAtAsc(Long auctionId);
    List<ProxyBid> findByAuctionIdOrderByMaxAmountDesc(Long auctionId);
}
