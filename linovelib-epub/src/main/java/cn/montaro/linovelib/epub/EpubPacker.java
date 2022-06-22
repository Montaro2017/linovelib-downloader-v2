package cn.montaro.linovelib.epub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.util.ZipUtil;
import cn.montaro.linovelib.epub.constant.EpubConstant;
import cn.montaro.linovelib.epub.resource.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class EpubPacker {

    List<EpubResource> resourceList = new ArrayList<>();

    private int imageResourceCount = 0;
    private int chapterResourceCount = 0;

    private OPFResource opfResource;
    private NCXResource ncxResource;

    public EpubPacker() {
        this.resourceList.add(EpubConstant.RESOURCE_MIMETYPE);
        this.resourceList.add(EpubConstant.RESOURCE_CONTAINER);
        this.opfResource = OPFResource.newInstance();
        this.ncxResource = NCXResource.newInstance();
        this.resourceList.add(opfResource);
        this.resourceList.add(ncxResource);
    }

    public String addImageResource(byte[] imageBytes) {
        String nameInEpub = getImageResourceName(this.imageResourceCount);
        EpubImageResource epubImageResource = new EpubImageResource(nameInEpub, imageBytes);
        this.resourceList.add(epubImageResource);
        this.imageResourceCount++;
        return nameInEpub;
    }

    public String addChapterResource(String chapterContent, String chapterName) {
        String nameInEpub = getChapterResourceName(this.chapterResourceCount);
        EpubTextResource epubTextResource = new EpubTextResource(nameInEpub, chapterContent);
        this.resourceList.add(epubTextResource);
        this.chapterResourceCount++;
        return nameInEpub;
    }

    public void addOtherResource(EpubResource resource) {
        this.resourceList.add(resource);
    }

    public File pack(String output) {
        File outputFile = FileUtil.file(output);
        int resourceCount = this.resourceList.size();
        Resource[] resources = this.resourceList.toArray(new Resource[resourceCount]);
        return ZipUtil.zip(outputFile, StandardCharsets.UTF_8, resources);
    }

    private static String getImageResourceName(int index) {
        return String.format(EpubConstant.PATH_IMAGES + "%4d.jpg", index + 1);
    }

    private static String getChapterResourceName(int index) {
        return String.format(EpubConstant.PATH_OEBPS + "chapter%4d.xhtml", index + 1);
    }

}
