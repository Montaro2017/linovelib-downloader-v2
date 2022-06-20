package cn.montaro.linovelib.common.util;

import cn.hutool.http.HttpUtil;
import cn.montaro.linovelib.common.model.SimpleImageInfo;
import org.junit.jupiter.api.Test;

public class RetryUtilTest {

    @Test
    public void testRetry() {
        String url = "asljdlkj";
        long before = System.currentTimeMillis();
        String content = RetryUtil.retry(() -> HttpUtil.get(url), 3, 1000);
        long after = System.currentTimeMillis();
        System.out.println("content = " + content);
        System.out.println("(after-before) = " + (after - before));
    }

}
