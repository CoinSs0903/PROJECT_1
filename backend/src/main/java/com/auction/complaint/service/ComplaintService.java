package com.auction.complaint.service;

import com.auction.audit.facade.AuditLogFacade;
import com.auction.complaint.entity.Complaint;
import com.auction.complaint.entity.Review;
import com.auction.complaint.repository.ComplaintRepository;
import com.auction.complaint.repository.ReviewRepository;
import com.auction.notification.facade.NotificationFacade;
import com.auction.order.entity.Order;
import com.auction.order.entity.OrderStatus;
import com.auction.order.repository.OrderRepository;
import com.auction.wallet.facade.WalletFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ComplaintService {

    private final ReviewRepository reviewRepository;
    private final ComplaintRepository complaintRepository;
    private final OrderRepository orderRepository;
    private final WalletFacade walletFacade;
    private final NotificationFacade notificationFacade;
    private final AuditLogFacade auditLogFacade;

    public ComplaintService(
            ReviewRepository reviewRepository,
            ComplaintRepository complaintRepository,
            OrderRepository orderRepository,
            WalletFacade walletFacade,
            NotificationFacade notificationFacade,
            AuditLogFacade auditLogFacade) {
        this.reviewRepository = reviewRepository;
        this.complaintRepository = complaintRepository;
        this.orderRepository = orderRepository;
        this.walletFacade = walletFacade;
        this.notificationFacade = notificationFacade;
        this.auditLogFacade = auditLogFacade;
    }

    @Transactional
    public Review addReview(Long orderId, Long reviewerId, int rating, String comment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Chỉ có thể đánh giá khi đơn hàng đã hoàn tất.");
        }

        if (!order.getBuyerId().equals(reviewerId) && !order.getSellerId().equals(reviewerId)) {
            throw new IllegalArgumentException("Bạn không có quyền đánh giá đơn hàng này.");
        }

        if (reviewRepository.findByOrderIdAndReviewerId(orderId, reviewerId).isPresent()) {
            throw new IllegalStateException("Bạn đã đánh giá đơn hàng này rồi.");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Số sao đánh giá phải từ 1 đến 5.");
        }

        Long revieweeId = order.getBuyerId().equals(reviewerId) ? order.getSellerId() : order.getBuyerId();

        Review review = new Review();
        review.setOrderId(orderId);
        review.setReviewerId(reviewerId);
        review.setRevieweeId(revieweeId);
        review.setRating(rating);
        review.setComment(comment != null ? comment.trim() : "");
        Review saved = reviewRepository.save(review);

        notificationFacade.sendNotification(revieweeId, "GENERAL", "Đánh giá mới cho đơn hàng #" + orderId,
                "Bạn nhận được đánh giá " + rating + " sao cho đơn hàng #" + orderId,
                "/orders/" + orderId);

        auditLogFacade.log("REVIEW_ADDED", reviewerId, "Review", saved.getId(),
                "Đánh giá " + rating + " sao cho người dùng #" + revieweeId);

        return saved;
    }

    @Transactional
    public Complaint fileComplaint(Long orderId, Long complainantId, String complaintType, String reason, String evidenceUrl) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + orderId));

        if (!order.getBuyerId().equals(complainantId) && !order.getSellerId().equals(complainantId)) {
            throw new IllegalArgumentException("Bạn không phải bên tham gia đơn hàng này.");
        }

        Complaint complaint = new Complaint();
        complaint.setOrderId(orderId);
        complaint.setComplainantId(complainantId);
        complaint.setComplaintType(complaintType != null ? complaintType : "ITEM_ISSUE");
        complaint.setReason(reason != null ? reason.trim() : "Khiếu nại về đơn hàng");
        complaint.setEvidenceUrl(evidenceUrl);
        complaint.setStatus("PENDING");
        Complaint saved = complaintRepository.save(complaint);

        auditLogFacade.log("COMPLAINT_FILED", complainantId, "Complaint", saved.getId(),
                "Khiếu nại đơn hàng #" + orderId + ": " + reason);

        return saved;
    }

    @Transactional
    public Complaint resolveComplaint(Long complaintId, Long adminId, String resolutionNote, boolean refundBuyer) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khiếu nại: " + complaintId));

        complaint.setStatus("RESOLVED");
        complaint.setResolutionNote(resolutionNote);
        complaint.setResolvedAt(LocalDateTime.now());
        Complaint updated = complaintRepository.save(complaint);

        Order order = orderRepository.findById(complaint.getOrderId()).orElse(null);
        if (order != null && refundBuyer) {
            String idempotencyKey = "REFUND_" + order.getId() + "_" + UUID.randomUUID();
            walletFacade.refundOrder(order.getBuyerId(), order.getId(), order.getTotalAmount(), idempotencyKey);
            notificationFacade.sendNotification(order.getBuyerId(), "GENERAL", "Khiếu nại được chấp thuận & Đã hoàn tiền",
                    "Khiếu nại đơn hàng #" + order.getId() + " đã được xử lý. Số tiền " + order.getTotalAmount() + " ₫ đã được hoàn vào ví của bạn.",
                    "/wallet");
        }

        auditLogFacade.log("COMPLAINT_RESOLVED", adminId, "Complaint", complaint.getId(),
                "Admin xử lý khiếu nại #" + complaintId + ". Hoàn tiền buyer: " + refundBuyer);

        return updated;
    }

    @Transactional(readOnly = true)
    public List<Complaint> getPendingComplaints() {
        return complaintRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsForUser(Long userId) {
        return reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(userId);
    }
}
