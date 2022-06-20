package cn.montaro.linovelib.common.util;

import cn.hutool.core.thread.ThreadUtil;

import java.util.function.Supplier;

public class RetryUtil {

    public static final Integer DEFAULT_RETRY = 3;

    public static final Integer DEFAULT_DELAY = 0;

    /**
     * 异常重试
     *
     * @param supplier 执行方法
     * @param retry    重试次数 当第一次执行发生异常就会开始重试 总共会执行retry+1次
     * @param delay    重试延迟 单位ms 不太准确
     * @return
     */
    public static <T> T retry(Supplier<T> supplier, int retry, int delay) {
        int tryTimes = 0;
        do {
            try {
                return supplier.get();
            } catch (Throwable ignored) {
                if (delay > 0) {
                    ThreadUtil.safeSleep(delay);
                }
            }
        } while (tryTimes++ < retry);
        return null;
    }

    public static <T> T retry(Supplier<T> supplier) {
        return retry(supplier, DEFAULT_RETRY, DEFAULT_DELAY);
    }

}
