package cn.montaro.linovelib.epub.resource;

import java.io.InputStream;

public class EpubImageResource extends EpubResource {

    private byte[] imageBytes;

    public EpubImageResource(String name, byte[] imageBytes) {
        super(name);
        this.imageBytes = imageBytes;
    }

    @Override
    public InputStream getStream() {
        return null;
    }
}
