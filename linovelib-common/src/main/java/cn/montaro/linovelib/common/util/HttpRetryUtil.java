package cn.montaro.linovelib.common.util;

import cn.hutool.http.HttpUtil;

public class HttpRetryUtil {

    // 默认http请求超时时间 单位毫秒
    private static final int DEFAULT_TIMEOUT = 3 * 1000;

    public static String get(String url) {
        return RetryUtil.retry(() -> HttpUtil.get(url, DEFAULT_TIMEOUT));
    }

    public static byte[] getBytes(String url) {
        return RetryUtil.retry(() ->
                HttpUtil.createGet(url, true)
                        .timeout(DEFAULT_TIMEOUT)
                        .executeAsync().bodyBytes()
        );
    }

}
