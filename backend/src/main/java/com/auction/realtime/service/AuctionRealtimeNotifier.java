package com.auction.realtime.service;

import com.auction.realtime.dto.BidUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuctionRealtimeNotifier {

    private static final Logger log = LoggerFactory.getLogger(AuctionRealtimeNotifier.class);
    private final SimpMessagingTemplate messagingTemplate;

    public AuctionRealtimeNotifier(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastBidUpdate(Long auctionId, long currentPrice, int bidCount, String highestBidderMasked, boolean isReserveMet) {
        try {
            BidUpdateEvent event = new BidUpdateEvent(auctionId, currentPrice, bidCount, highestBidderMasked, isReserveMet);
            messagingTemplate.convertAndSend("/topic/auctions/" + auctionId, event);
            log.debug("Đã gửi cập nhật realtime cho phiên đấu giá #{}: giá={}", auctionId, currentPrice);
        } catch (Exception e) {
            log.warn("Không thể gửi thông báo realtime cho phiên #{}: {}", auctionId, e.getMessage());
        }
    }
}
