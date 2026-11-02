package com.auction.wallet.entity;

import com.auction.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "wallets")
public class Wallet extends BaseEntity {

    @Column(name = "user_id", unique = true)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type", nullable = false, length = 30)
    private WalletType walletType = WalletType.USER;

    @Column(name = "available_balance", nullable = false)
    private long availableBalance = 0L;

    public Wallet() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    public long getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(long availableBalance) {
        this.availableBalance = availableBalance;
    }
}

