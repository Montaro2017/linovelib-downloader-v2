package cn.montaro.linovelib.common.util;

import cn.hutool.http.HttpUtil;

public class HttpRetryUtil {

    public static String get(String url) {
        return RetryUtil.retry(() -> HttpUtil.get(url));
    }

    public static byte[] getBytes(String url) {
        return RetryUtil.retry(() -> HttpUtil.downloadBytes(url));
    }

}
