package cn.montaro.linovelib.epub;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.montaro.linovelib.epub.resource.OPFResource;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class OPFResourceTest {

    @Test
    public void testOPFResource() {
        OPFResource opfResource = OPFResource.newInstance();
        opfResource.setBookId(UUID.fastUUID().toString());
        opfResource.setTitle("第一卷 ABCDEFG");
        opfResource.setCover("images/0001.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IoUtil.copy(opfResource.getStream(), baos);
        System.out.println(IoUtil.toStr(baos, StandardCharsets.UTF_8));
    }
}
