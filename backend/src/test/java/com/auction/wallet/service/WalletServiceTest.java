package com.auction.wallet.service;

import com.auction.common.exception.AppException;
import com.auction.common.exception.ErrorCode;
import com.auction.wallet.entity.Transaction;
import com.auction.wallet.entity.Wallet;
import com.auction.wallet.entity.WalletType;
import com.auction.wallet.repository.TransactionRepository;
import com.auction.wallet.repository.WalletRepository;
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

import static org.junit.jupiter.api.Assertions.*;

class WalletServiceTest {

    private InMemoryWalletRepository walletRepository;
    private InMemoryTransactionRepository transactionRepository;
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletRepository = new InMemoryWalletRepository();
        transactionRepository = new InMemoryTransactionRepository();
        walletService = new WalletService(walletRepository, transactionRepository);

        Wallet wallet = new Wallet();
        wallet.setUserId(1L);
        wallet.setWalletType(WalletType.USER);
        wallet.setAvailableBalance(500_000L);
        walletRepository.save(wallet);
    }

    @Test
    @DisplayName("Nạp tiền vào ví thành công và ghi nhận giao dịch")
    void testTopUpSuccess() {
        walletService.topUp(1L, 200_000L, "KEY_TOPUP_1");

        long balance = walletService.getAvailableBalance(1L);
        assertEquals(700_000L, balance);

        List<Transaction> txs = walletService.getTransactions(1L);
        assertEquals(1, txs.size());
        assertEquals(200_000L, txs.get(0).getAmount());
    }

    @Test
    @DisplayName("Rút tiền thất bại khi số dư không đủ")
    void testWithdrawInsufficientBalance() {
        AppException ex = assertThrows(AppException.class, () ->
                walletService.withdraw(1L, 600_000L, "VCB 123456", "KEY_WD_FAIL"));
        assertEquals(ErrorCode.INSUFFICIENT_BALANCE, ex.getErrorCode());
        assertEquals(500_000L, walletService.getAvailableBalance(1L));
    }

    @Test
    @DisplayName("Thanh toán đơn hàng trừ tiền thành công")
    void testPayOrderSuccess() {
        walletService.payOrder(1L, 99L, 300_000L, "KEY_PAY_1");

        assertEquals(200_000L, walletService.getAvailableBalance(1L));
        List<Transaction> txs = walletService.getTransactions(1L);
        assertEquals(1, txs.size());
        assertEquals("ORDER", txs.get(0).getReferenceType());
        assertEquals(99L, txs.get(0).getReferenceId());
    }

    @Test
    @DisplayName("Bảo vệ tính Idempotent: không cộng tiền 2 lần khi trùng idempotencyKey")
    void testIdempotencyProtection() {
        walletService.topUp(1L, 100_000L, "KEY_DUP");
        assertEquals(600_000L, walletService.getAvailableBalance(1L));

        // Gọi lại lần 2 với cùng key
        walletService.topUp(1L, 100_000L, "KEY_DUP");
        assertEquals(600_000L, walletService.getAvailableBalance(1L));
    }

    // --- In-memory test repositories ---
    private static class InMemoryWalletRepository implements WalletRepository {
        private final Map<Long, Wallet> db = new ConcurrentHashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public Optional<Wallet> findByUserId(Long userId) {
            return db.values().stream().filter(w -> Objects.equals(w.getUserId(), userId)).findFirst();
        }

        @Override
        public Optional<Wallet> findByUserIdForUpdate(Long userId) {
            return findByUserId(userId);
        }

        @Override
        public Optional<Wallet> findByWalletTypeForUpdate(WalletType walletType) {
            return findByWalletType(walletType);
        }

        @Override
        public Optional<Wallet> findByWalletType(WalletType walletType) {
            return db.values().stream().filter(w -> w.getWalletType() == walletType).findFirst();
        }

        @Override
        public <S extends Wallet> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idGen.getAndIncrement());
            }
            db.put(entity.getId(), entity);
            return entity;
        }

        @Override public Optional<Wallet> findById(Long aLong) { return Optional.ofNullable(db.get(aLong)); }
        @Override public boolean existsById(Long aLong) { return db.containsKey(aLong); }
        @Override public List<Wallet> findAll() { return new ArrayList<>(db.values()); }
        @Override public List<Wallet> findAllById(Iterable<Long> longs) { return null; }
        @Override public long count() { return db.size(); }
        @Override public void deleteById(Long aLong) { db.remove(aLong); }
        @Override public void delete(Wallet entity) { db.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> longs) {}
        @Override public void deleteAll(Iterable<? extends Wallet> entities) {}
        @Override public void deleteAll() { db.clear(); }
        @Override public void flush() {}
        @Override public <S extends Wallet> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Wallet> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<Wallet> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public Wallet getOne(Long aLong) { return db.get(aLong); }
        @Override public Wallet getById(Long aLong) { return db.get(aLong); }
        @Override public Wallet getReferenceById(Long aLong) { return db.get(aLong); }
        @Override public <S extends Wallet> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Wallet> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends Wallet> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends Wallet> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Wallet> long count(Example<S> example) { return 0; }
        @Override public <S extends Wallet> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Wallet, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public <S extends Wallet> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public List<Wallet> findAll(Sort sort) { return null; }
        @Override public Page<Wallet> findAll(Pageable pageable) { return null; }
    }

    private static class InMemoryTransactionRepository implements TransactionRepository {
        private final Map<Long, Transaction> db = new ConcurrentHashMap<>();
        private final AtomicLong idGen = new AtomicLong(1);

        @Override
        public List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId) {
            List<Transaction> list = new ArrayList<>();
            for (Transaction t : db.values()) {
                if (Objects.equals(t.getWalletId(), walletId)) {
                    list.add(t);
                }
            }
            return list;
        }

        @Override
        public boolean existsByIdempotencyKey(String idempotencyKey) {
            return db.values().stream().anyMatch(t -> idempotencyKey.equals(t.getIdempotencyKey()));
        }

        @Override
        public Optional<Transaction> findByIdempotencyKey(String idempotencyKey) {
            return db.values().stream().filter(t -> idempotencyKey.equals(t.getIdempotencyKey())).findFirst();
        }

        @Override
        public <S extends Transaction> S save(S entity) {
            if (entity.getId() == null) {
                entity.setId(idGen.getAndIncrement());
            }
            db.put(entity.getId(), entity);
            return entity;
        }

        @Override public Optional<Transaction> findById(Long aLong) { return Optional.ofNullable(db.get(aLong)); }
        @Override public boolean existsById(Long aLong) { return db.containsKey(aLong); }
        @Override public List<Transaction> findAll() { return new ArrayList<>(db.values()); }
        @Override public List<Transaction> findAllById(Iterable<Long> longs) { return null; }
        @Override public long count() { return db.size(); }
        @Override public void deleteById(Long aLong) { db.remove(aLong); }
        @Override public void delete(Transaction entity) { db.remove(entity.getId()); }
        @Override public void deleteAllById(Iterable<? extends Long> longs) {}
        @Override public void deleteAll(Iterable<? extends Transaction> entities) {}
        @Override public void deleteAll() { db.clear(); }
        @Override public void flush() {}
        @Override public <S extends Transaction> S saveAndFlush(S entity) { return save(entity); }
        @Override public <S extends Transaction> List<S> saveAllAndFlush(Iterable<S> entities) { return null; }
        @Override public void deleteAllInBatch(Iterable<Transaction> entities) {}
        @Override public void deleteAllByIdInBatch(Iterable<Long> longs) {}
        @Override public void deleteAllInBatch() {}
        @Override public Transaction getOne(Long aLong) { return db.get(aLong); }
        @Override public Transaction getById(Long aLong) { return db.get(aLong); }
        @Override public Transaction getReferenceById(Long aLong) { return db.get(aLong); }
        @Override public <S extends Transaction> Optional<S> findOne(Example<S> example) { return Optional.empty(); }
        @Override public <S extends Transaction> List<S> findAll(Example<S> example) { return null; }
        @Override public <S extends Transaction> List<S> findAll(Example<S> example, Sort sort) { return null; }
        @Override public <S extends Transaction> Page<S> findAll(Example<S> example, Pageable pageable) { return null; }
        @Override public <S extends Transaction> long count(Example<S> example) { return 0; }
        @Override public <S extends Transaction> boolean exists(Example<S> example) { return false; }
        @Override public <S extends Transaction, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) { return null; }
        @Override public <S extends Transaction> List<S> saveAll(Iterable<S> entities) { return null; }
        @Override public List<Transaction> findAll(Sort sort) { return null; }
        @Override public Page<Transaction> findAll(Pageable pageable) { return null; }
    }
}
