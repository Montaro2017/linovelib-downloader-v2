package cn.montaro.linovelib.epub.resource;

import cn.hutool.core.io.IoUtil;

import java.io.InputStream;

public class EpubImageResource extends EpubResource {

    private byte[] imageBytes;

    public EpubImageResource(String nameInEpub, byte[] imageBytes) {
        this.nameInEpub = nameInEpub;
        this.imageBytes = imageBytes;
    }

    @Override
    public InputStream getStream() {
        return IoUtil.toStream(this.imageBytes);
    }
}