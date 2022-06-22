package cn.montaro.linovelib.epub.resource;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.Resource;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;

public abstract class EpubResource implements Resource {

    protected String nameInEpub;

    @Override
    public String getName() {
        return this.nameInEpub;
    }

    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public boolean isModified() {
        return Resource.super.isModified();
    }

    @Override
    public void writeTo(OutputStream out) throws IORuntimeException {
        Resource.super.writeTo(out);
    }

    @Override
    public BufferedReader getReader(Charset charset) {
        return Resource.super.getReader(charset);
    }

    @Override
    public String readStr(Charset charset) throws IORuntimeException {
        return Resource.super.readStr(charset);
    }

    @Override
    public String readUtf8Str() throws IORuntimeException {
        return Resource.super.readUtf8Str();
    }

    @Override
    public byte[] readBytes() throws IORuntimeException {
        return Resource.super.readBytes();
    }
}
