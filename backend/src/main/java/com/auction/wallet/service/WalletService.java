package com.auction.wallet.service;

import com.auction.common.exception.AppException;
import com.auction.common.exception.ErrorCode;
import com.auction.wallet.entity.Transaction;
import com.auction.wallet.entity.TransactionType;
import com.auction.wallet.entity.Wallet;
import com.auction.wallet.entity.WalletType;
import com.auction.wallet.facade.WalletFacade;
import com.auction.wallet.repository.TransactionRepository;
import com.auction.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletService implements WalletFacade {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository,
                         TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public long getAvailableBalance(Long userId) {
        return walletRepository.findByUserId(userId)
                .map(Wallet::getAvailableBalance)
                .orElse(0L);
    }

    @Override
    @Transactional
    public void createWalletForUser(Long userId) {
        if (walletRepository.findByUserId(userId).isEmpty()) {
            Wallet wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setWalletType(WalletType.USER);
            wallet.setAvailableBalance(0L);
            walletRepository.save(wallet);
        }
    }

    public Wallet getOrCreateUserWallet(Long userId) {
        return walletRepository.findByUserId(userId).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setWalletType(WalletType.USER);
            wallet.setAvailableBalance(0L);
            return walletRepository.save(wallet);
        });
    }

    public Wallet getOrCreateSystemRevenueWallet() {
        return walletRepository.findByWalletType(WalletType.SYSTEM_REVENUE).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setUserId(null);
            wallet.setWalletType(WalletType.SYSTEM_REVENUE);
            wallet.setAvailableBalance(0L);
            return walletRepository.save(wallet);
        });
    }

    @Override
    @Transactional
    public void topUp(Long userId, long amount, String idempotencyKey) {
        if (amount <= 0) {
            throw new AppException(ErrorCode.INVALID_TRANSACTION_AMOUNT, "Số tiền nạp phải lớn hơn 0");
        }
        if (transactionRepository.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        getOrCreateUserWallet(userId);
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        long before = wallet.getAvailableBalance();
        long after = before + amount;
        wallet.setAvailableBalance(after);
        walletRepository.save(wallet);

        Transaction tx = new Transaction();
        tx.setWalletId(wallet.getId());
        tx.setTransactionType(TransactionType.TOPUP);
        tx.setAmount(amount);
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(after);
        tx.setIdempotencyKey(idempotencyKey);
        tx.setDescription("Nạp tiền vào ví cá nhân: +" + amount + " VNĐ");
        transactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void withdraw(Long userId, long amount, String bankAccount, String idempotencyKey) {
        if (amount <= 0) {
            throw new AppException(ErrorCode.INVALID_TRANSACTION_AMOUNT, "Số tiền rút phải lớn hơn 0");
        }
        if (transactionRepository.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        if (wallet.getAvailableBalance() < amount) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        long before = wallet.getAvailableBalance();
        long after = before - amount;
        wallet.setAvailableBalance(after);
        walletRepository.save(wallet);

        Transaction tx = new Transaction();
        tx.setWalletId(wallet.getId());
        tx.setTransactionType(TransactionType.WITHDRAW);
        tx.setAmount(amount);
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(after);
        tx.setIdempotencyKey(idempotencyKey);
        tx.setDescription("Rút tiền về tài khoản ngân hàng (" + bankAccount + "): -" + amount + " VNĐ");
        transactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void payOrder(Long buyerId, Long orderId, long amount, String idempotencyKey) {
        if (transactionRepository.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        Wallet wallet = walletRepository.findByUserIdForUpdate(buyerId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        if (wallet.getAvailableBalance() < amount) {
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        long before = wallet.getAvailableBalance();
        long after = before - amount;
        wallet.setAvailableBalance(after);
        walletRepository.save(wallet);

        Transaction tx = new Transaction();
        tx.setWalletId(wallet.getId());
        tx.setTransactionType(TransactionType.PAYMENT);
        tx.setAmount(amount);
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(after);
        tx.setReferenceType("ORDER");
        tx.setReferenceId(orderId);
        tx.setIdempotencyKey(idempotencyKey);
        tx.setDescription("Thanh toán tiền hàng cho đơn hàng #" + orderId);
        transactionRepository.save(tx);
    }

    @Override
    @Transactional
    public void payoutSeller(Long sellerId, Long orderId, long finalPrice, double feePercent, String idempotencyKey) {
        if (transactionRepository.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        long feeAmount = Math.round(finalPrice * (feePercent / 100.0));
        long sellerAmount = finalPrice - feeAmount;

        // 1. Cộng phí sàn vào SYSTEM_REVENUE
        getOrCreateSystemRevenueWallet();
        Wallet sysWallet = walletRepository.findByWalletTypeForUpdate(WalletType.SYSTEM_REVENUE)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));
        long sysBefore = sysWallet.getAvailableBalance();
        long sysAfter = sysBefore + feeAmount;
        sysWallet.setAvailableBalance(sysAfter);
        walletRepository.save(sysWallet);

        Transaction sysTx = new Transaction();
        sysTx.setWalletId(sysWallet.getId());
        sysTx.setTransactionType(TransactionType.SERVICE_FEE);
        sysTx.setAmount(feeAmount);
        sysTx.setBalanceBefore(sysBefore);
        sysTx.setBalanceAfter(sysAfter);
        sysTx.setReferenceType("ORDER");
        sysTx.setReferenceId(orderId);
        sysTx.setIdempotencyKey(idempotencyKey + "_fee");
        sysTx.setDescription("Thu phí sàn " + feePercent + "% từ đơn hàng #" + orderId);
        transactionRepository.save(sysTx);

        // 2. Cộng tiền bán hàng cho Seller
        getOrCreateUserWallet(sellerId);
        Wallet sellerWallet = walletRepository.findByUserIdForUpdate(sellerId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));
        long sellerBefore = sellerWallet.getAvailableBalance();
        long sellerAfter = sellerBefore + sellerAmount;
        sellerWallet.setAvailableBalance(sellerAfter);
        walletRepository.save(sellerWallet);

        Transaction sellerTx = new Transaction();
        sellerTx.setWalletId(sellerWallet.getId());
        sellerTx.setTransactionType(TransactionType.PAYOUT);
        sellerTx.setAmount(sellerAmount);
        sellerTx.setBalanceBefore(sellerBefore);
        sellerTx.setBalanceAfter(sellerAfter);
        sellerTx.setReferenceType("ORDER");
        sellerTx.setReferenceId(orderId);
        sellerTx.setIdempotencyKey(idempotencyKey + "_payout");
        sellerTx.setDescription("Nhận tiền bán hàng đơn #" + orderId + " (Đã khấu trừ phí sàn " + feePercent + "%)");
        transactionRepository.save(sellerTx);
    }

    @Override
    @Transactional
    public void refundOrder(Long buyerId, Long orderId, long amount, String idempotencyKey) {
        if (transactionRepository.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        getOrCreateUserWallet(buyerId);
        Wallet wallet = walletRepository.findByUserIdForUpdate(buyerId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        long before = wallet.getAvailableBalance();
        long after = before + amount;
        wallet.setAvailableBalance(after);
        walletRepository.save(wallet);

        Transaction tx = new Transaction();
        tx.setWalletId(wallet.getId());
        tx.setTransactionType(TransactionType.REFUND);
        tx.setAmount(amount);
        tx.setBalanceBefore(before);
        tx.setBalanceAfter(after);
        tx.setReferenceType("ORDER");
        tx.setReferenceId(orderId);
        tx.setIdempotencyKey(idempotencyKey);
        tx.setDescription("Hoàn tiền đơn hàng #" + orderId);
        transactionRepository.save(tx);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactions(Long userId) {
        Wallet wallet = getOrCreateUserWallet(userId);
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }
}

