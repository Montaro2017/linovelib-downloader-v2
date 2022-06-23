package cn.montaro.linovelib.epub.resource;

import cn.hutool.core.io.resource.Resource;

import java.net.URL;

public abstract class EpubResource implements Resource {

    protected String pathInEpub;

    public EpubResource(String pathInEpub) {
        this.pathInEpub = pathInEpub;
    }

    @Override
    public String getName() {
        return this.pathInEpub;
    }

    @Override
    public URL getUrl() {
        return null;
    }

}
