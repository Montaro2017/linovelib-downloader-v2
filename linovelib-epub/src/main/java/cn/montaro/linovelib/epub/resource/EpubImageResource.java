package cn.montaro.linovelib.epub.resource;

import cn.hutool.core.io.IoUtil;
import cn.montaro.linovelib.common.model.SimpleImageInfo;
import cn.montaro.linovelib.common.util.FastImageUtil;

import java.io.InputStream;

public class EpubImageResource extends EpubResource {

    private final byte[] imageBytes;
    private final SimpleImageInfo imageInfo;
    private final String path;

    public EpubImageResource(String pathInEpub, byte[] imageBytes,String path) {
        super(pathInEpub);
        this.imageBytes = imageBytes;
        this.imageInfo = FastImageUtil.getImageInfo(imageBytes);
        this.path = path;
    }

    @Override
    public InputStream getStream() {
        return IoUtil.toStream(this.imageBytes);
    }

    public SimpleImageInfo getImageInfo() {
        return imageInfo;
    }

    public String getPath() {
        return path;
    }
}
