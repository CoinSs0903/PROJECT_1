package com.auction.bidding.service;

import com.auction.auction.entity.Auction;
import com.auction.auction.entity.AuctionStatus;
import com.auction.auction.repository.AuctionRepository;
import com.auction.audit.facade.AuditLogFacade;
import com.auction.bidding.dto.BidResultDto;
import com.auction.bidding.entity.Bid;
import com.auction.bidding.entity.ProxyBid;
import com.auction.bidding.repository.BidRepository;
import com.auction.bidding.repository.ProxyBidRepository;
import com.auction.catalog.entity.Item;
import com.auction.catalog.repository.ItemRepository;
import com.auction.common.util.MoneyUtil;
import com.auction.common.util.StringUtil;
import com.auction.notification.facade.NotificationFacade;
import com.auction.user.entity.User;
import com.auction.user.entity.UserStatus;
import com.auction.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProxyBiddingService {

    private final AuctionRepository auctionRepository;
    private final ProxyBidRepository proxyBidRepository;
    private final BidRepository bidRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final NotificationFacade notificationFacade;
    private final AuditLogFacade auditLogFacade;

    public ProxyBiddingService(
            AuctionRepository auctionRepository,
            ProxyBidRepository proxyBidRepository,
            BidRepository bidRepository,
            ItemRepository itemRepository,
            UserRepository userRepository,
            NotificationFacade notificationFacade,
            AuditLogFacade auditLogFacade) {
        this.auctionRepository = auctionRepository;
        this.proxyBidRepository = proxyBidRepository;
        this.bidRepository = bidRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.notificationFacade = notificationFacade;
        this.auditLogFacade = auditLogFacade;
    }

    @Transactional
    public BidResultDto processBid(Long auctionId, Long bidderId, long maxAmount) {
        // 1. Pessimistic write lock on auction
        Auction auction = auctionRepository.findByIdForUpdate(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Phiên đấu giá không tồn tại: " + auctionId));

        // 2. Validate auction state
        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new IllegalStateException("Phiên đấu giá không ở trạng thái hoạt động (Trạng thái: " + auction.getStatus() + ").");
        }
        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Phiên đấu giá đã kết thúc.");
        }

        Item item = itemRepository.findById(auction.getItemId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy thông tin sản phẩm"));

        if (item.getSellerId().equals(bidderId)) {
            throw new IllegalArgumentException("Người bán không thể tự đặt giá cho sản phẩm của mình.");
        }

        User bidder = userRepository.findById(bidderId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại: " + bidderId));
        if (bidder.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("Tài khoản của bạn đang bị khoá hoặc chưa kích hoạt.");
        }

        // Validate minimum required bid
        long minBid = (auction.getBidCount() == 0)
                ? auction.getStartingPrice()
                : (auction.getCurrentPrice() + auction.getStepPrice());

        if (maxAmount < minBid) {
            throw new IllegalArgumentException("Mức giá tối đa bạn đặt (" + MoneyUtil.formatVnd(maxAmount)
                    + ") phải lớn hơn hoặc bằng mức giá tối thiểu (" + MoneyUtil.formatVnd(minBid) + ").");
        }

        // 3. Find current top proxy bid
        Optional<ProxyBid> topProxyOpt = proxyBidRepository.findTopByAuctionIdOrderByMaxAmountDescCreatedAtAsc(auctionId);
        Optional<ProxyBid> bidderProxyOpt = proxyBidRepository.findByAuctionIdAndBidderId(auctionId, bidderId);

        // Case A: First bid on the auction
        if (topProxyOpt.isEmpty()) {
            ProxyBid newProxy = bidderProxyOpt.orElseGet(ProxyBid::new);
            newProxy.setAuctionId(auctionId);
            newProxy.setBidderId(bidderId);
            newProxy.setMaxAmount(maxAmount);
            proxyBidRepository.save(newProxy);

            long newPrice = auction.getStartingPrice();
            if (auction.getReservePrice() != null && maxAmount >= auction.getReservePrice()) {
                auction.setReserveMet(true);
                if (newPrice < auction.getReservePrice()) {
                    newPrice = auction.getReservePrice();
                }
            }

            auction.setCurrentPrice(newPrice);
            auction.setHighestBidderId(bidderId);
            auction.setBidCount(auction.getBidCount() + 1);
            auctionRepository.save(auction);

            Bid bid = new Bid();
            bid.setAuctionId(auctionId);
            bid.setBidderId(bidderId);
            bid.setBidAmount(newPrice);
            bid.setAutoBid(false);
            bidRepository.save(bid);

            auditLogFacade.log("BID_PLACED", bidderId, "Auction", auctionId,
                    "Đặt giá đầu tiên: " + newPrice + ", giá tối đa: " + maxAmount);

            return BidResultDto.success("Đặt giá thành công! Bạn đang dẫn đầu phiên đấu giá.",
                    newPrice, auction.getBidCount(), StringUtil.maskUsername(bidder.getUsername()),
                    auction.isReserveMet(), true);
        }

        ProxyBid topProxy = topProxyOpt.get();

        // Case B: Bidder is currently already the leader
        if (topProxy.getBidderId().equals(bidderId)) {
            if (maxAmount <= topProxy.getMaxAmount()) {
                throw new IllegalArgumentException("Mức giá tối đa mới (" + MoneyUtil.formatVnd(maxAmount)
                        + ") phải cao hơn mức giá tối đa hiện tại của bạn (" + MoneyUtil.formatVnd(topProxy.getMaxAmount()) + ").");
            }

            topProxy.setMaxAmount(maxAmount);
            proxyBidRepository.save(topProxy);

            if (auction.getReservePrice() != null && !auction.isReserveMet() && maxAmount >= auction.getReservePrice()) {
                auction.setReserveMet(true);
                if (auction.getCurrentPrice() < auction.getReservePrice()) {
                    auction.setCurrentPrice(auction.getReservePrice());
                    Bid autoBid = new Bid();
                    autoBid.setAuctionId(auctionId);
                    autoBid.setBidderId(bidderId);
                    autoBid.setBidAmount(auction.getCurrentPrice());
                    autoBid.setAutoBid(true);
                    bidRepository.save(autoBid);
                    auctionRepository.save(auction);
                }
            }

            auditLogFacade.log("BID_UPDATED", bidderId, "Auction", auctionId,
                    "Nâng mức giá tối đa lên " + maxAmount);

            return BidResultDto.success("Cập nhật mức giá tối đa thành công! Bạn tiếp tục dẫn đầu.",
                    auction.getCurrentPrice(), auction.getBidCount(), StringUtil.maskUsername(bidder.getUsername()),
                    auction.isReserveMet(), true);
        }

        // Case C: New bidder vs Existing leader
        long maxL = topProxy.getMaxAmount();
        Long leaderId = topProxy.getBidderId();

        if (maxAmount > maxL) {
            // New bidder beats the existing leader!
            long newPrice = Math.min(maxAmount, maxL + auction.getStepPrice());
            if (auction.getReservePrice() != null && maxAmount >= auction.getReservePrice()) {
                auction.setReserveMet(true);
                if (newPrice < auction.getReservePrice()) {
                    newPrice = Math.min(maxAmount, auction.getReservePrice());
                }
            }

            ProxyBid newProxy = bidderProxyOpt.orElseGet(ProxyBid::new);
            newProxy.setAuctionId(auctionId);
            newProxy.setBidderId(bidderId);
            newProxy.setMaxAmount(maxAmount);
            proxyBidRepository.save(newProxy);

            auction.setCurrentPrice(newPrice);
            auction.setHighestBidderId(bidderId);
            auction.setBidCount(auction.getBidCount() + 1);
            auctionRepository.save(auction);

            Bid bid = new Bid();
            bid.setAuctionId(auctionId);
            bid.setBidderId(bidderId);
            bid.setBidAmount(newPrice);
            bid.setAutoBid(false);
            bidRepository.save(bid);

            // Notify outbid leader
            notificationFacade.sendNotification(leaderId, "OUTBID", "Bạn đã bị vượt giá!",
                    "Có người vừa đặt giá cao hơn bạn trong phiên đấu giá #" + auctionId
                            + ". Giá hiện tại là " + MoneyUtil.formatVnd(newPrice) + ".",
                    "/auctions/" + auctionId);

            auditLogFacade.log("BID_PLACED", bidderId, "Auction", auctionId,
                    "Vượt người dẫn đầu. Giá mới: " + newPrice + ", giá tối đa: " + maxAmount);

            return BidResultDto.success("Đặt giá thành công! Bạn đang dẫn đầu phiên đấu giá.",
                    newPrice, auction.getBidCount(), StringUtil.maskUsername(bidder.getUsername()),
                    auction.isReserveMet(), true);
        } else {
            // Existing leader wins automatically!
            long newPrice = Math.min(maxL, maxAmount + auction.getStepPrice());
            if (auction.getReservePrice() != null && maxL >= auction.getReservePrice()) {
                auction.setReserveMet(true);
                if (newPrice < auction.getReservePrice()) {
                    newPrice = Math.min(maxL, auction.getReservePrice());
                }
            }

            ProxyBid newProxy = bidderProxyOpt.orElseGet(ProxyBid::new);
            newProxy.setAuctionId(auctionId);
            newProxy.setBidderId(bidderId);
            newProxy.setMaxAmount(Math.max(newProxy.getMaxAmount(), maxAmount));
            proxyBidRepository.save(newProxy);

            // Record bidder B's bid
            Bid bidderBid = new Bid();
            bidderBid.setAuctionId(auctionId);
            bidderBid.setBidderId(bidderId);
            bidderBid.setBidAmount(maxAmount);
            bidderBid.setAutoBid(false);
            bidRepository.save(bidderBid);

            // Record leader's auto-outbid
            Bid autoBid = new Bid();
            autoBid.setAuctionId(auctionId);
            autoBid.setBidderId(leaderId);
            autoBid.setBidAmount(newPrice);
            autoBid.setAutoBid(true);
            bidRepository.save(autoBid);

            auction.setCurrentPrice(newPrice);
            auction.setBidCount(auction.getBidCount() + 2);
            auctionRepository.save(auction);

            String leaderMasked = userRepository.findById(leaderId)
                    .map(u -> StringUtil.maskUsername(u.getUsername()))
                    .orElse("***");

            // Notify bidder they are outbid immediately
            notificationFacade.sendNotification(bidderId, "OUTBID", "Giá của bạn đã bị vượt ngay lập tức!",
                    "Giá tối đa bạn đặt (" + MoneyUtil.formatVnd(maxAmount)
                            + ") đã bị hệ thống tự động của người dẫn đầu vượt qua. Giá hiện tại là "
                            + MoneyUtil.formatVnd(newPrice) + ".",
                    "/auctions/" + auctionId);

            auditLogFacade.log("BID_OUTBID", bidderId, "Auction", auctionId,
                    "Bị hệ thống tự động vượt ngay lập tức với giá " + newPrice);

            return BidResultDto.success("Giá đặt của bạn đã bị vượt qua ngay lập tức bởi giá tự động của người dẫn đầu.",
                    newPrice, auction.getBidCount(), leaderMasked,
                    auction.isReserveMet(), false);
        }
    }
}
