package com.auction.system.service;

import com.auction.common.util.MoneyUtil;
import com.auction.system.entity.SystemConfig;
import com.auction.system.facade.SystemConfigFacade;
import com.auction.system.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class SystemConfigService implements SystemConfigFacade {

    private final SystemConfigRepository systemConfigRepository;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public SystemConfigService(SystemConfigRepository systemConfigRepository) {
        this.systemConfigRepository = systemConfigRepository;
    }

    public String getConfigValue(String key, String defaultValue) {
        return cache.computeIfAbsent(key, k -> systemConfigRepository.findByConfigKey(k)
                .map(SystemConfig::getConfigValue)
                .orElse(defaultValue));
    }

    @Override
    public int getPlatformFeePercent() {
        try {
            return Integer.parseInt(getConfigValue("platform_fee_percent", "5"));
        } catch (NumberFormatException e) {
            return 5;
        }
    }

    @Override
    public int getPaymentDeadlineHours() {
        try {
            return Integer.parseInt(getConfigValue("payment_deadline_hours", "72"));
        } catch (NumberFormatException e) {
            return 72;
        }
    }

    @Override
    public int getMaxUnpaidStrikes() {
        try {
            return Integer.parseInt(getConfigValue("max_unpaid_strikes", "3"));
        } catch (NumberFormatException e) {
            return 3;
        }
    }

    @Override
    public long calculateMinimumNextBid(long currentPrice) {
        return MoneyUtil.calculateMinimumNextBid(currentPrice, 0);
    }

    public void updateConfig(String key, String value, String description) {
        SystemConfig config = systemConfigRepository.findByConfigKey(key)
                .orElseGet(() -> {
                    SystemConfig c = new SystemConfig();
                    c.setConfigKey(key);
                    return c;
                });
        config.setConfigValue(value);
        config.setDescription(description);
        systemConfigRepository.save(config);
        cache.put(key, value);
    }
}

