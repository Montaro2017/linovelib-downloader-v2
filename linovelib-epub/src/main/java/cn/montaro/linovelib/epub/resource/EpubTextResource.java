package cn.montaro.linovelib.epub.resource;

import cn.hutool.core.io.IoUtil;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpubTextResource extends EpubResource {

    private String content;

    public EpubTextResource(String nameInEpub, String content) {
        this.nameInEpub = nameInEpub;
        this.content = content;
    }

    @Override
    public InputStream getStream() {
        return IoUtil.toStream(content, StandardCharsets.UTF_8);
    }
}
