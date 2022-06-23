package cn.montaro.linovelib.epub;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import cn.montaro.linovelib.epub.resource.NCXResource;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class NCXResourceTest {

    @Test
    public void testNCXResource(){
        NCXResource ncxResource = NCXResource.newInstance();
        ncxResource.setId(UUID.fastUUID().toString());
        ncxResource.setTitle("ABCDEFGHIJKLMN");
        ncxResource.addChapter("第一章","chapter0001.xhtml");
        ncxResource.addChapter("第二章","chapter0002.xhtml");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IoUtil.copy(ncxResource.getStream(), baos);
        System.out.println(IoUtil.toStr(baos, StandardCharsets.UTF_8));
    }
}
