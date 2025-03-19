package com.dsj.bi.manager;

import com.dsj.bi.common.ErrorCode;
import com.dsj.bi.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供 RedisLimiter 限流基础服务(提供通用能力)
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     */
    public void doRateLimit(String key){


        // 获取RateLimiter实例
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);

        // 初始化限流配置：允许每个用户每秒最多进行5个请求
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        // 尝试获取一个许可
        boolean canOp = rateLimiter.tryAcquire();
        if (!canOp) {
            throw new BusinessException(ErrorCode.NOT_MANY_REQUEST);
        }
    }

}
