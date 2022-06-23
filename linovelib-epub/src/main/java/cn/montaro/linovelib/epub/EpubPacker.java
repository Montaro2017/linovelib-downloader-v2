package cn.montaro.linovelib.epub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.montaro.linovelib.epub.constant.EpubConstant;
import cn.montaro.linovelib.epub.resource.*;
import org.jsoup.nodes.Document;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class EpubPacker {

    private List<EpubResource> resourceList = new ArrayList<>();

    private int imageResourceCount = 0;
    private int chapterResourceCount = 0;

    // content.opf 元数据及文件清单
    private OPFResource opf;
    // toc.ncx 目录
    private NCXResource ncx;

    public EpubPacker() {
        this.resourceList.add(EpubConstant.RESOURCE_MIMETYPE);
        this.resourceList.add(EpubConstant.RESOURCE_CONTAINER);

        this.opf = OPFResource.newInstance();
        this.ncx = NCXResource.newInstance();

        String bookId = UUID.fastUUID().toString();
        this.opf.setBookId(bookId);
        this.ncx.setBookId(bookId);

        this.resourceList.add(opf);
        this.resourceList.add(ncx);
    }

    public void setBookName(String bookName) {
        opf.setTitle(bookName);
        ncx.setTitle(bookName);
    }

    public void setCover(String relativePath) {
        opf.setCover(relativePath);
    }

    public String addImageResource(byte[] imageBytes) {
        String pathInEpub = getImageResourcePath(++this.imageResourceCount);
        EpubImageResource epubImageResource = new EpubImageResource(pathInEpub, imageBytes);
        this.resourceList.add(epubImageResource);
        return relative(EpubConstant.PATH_OEBPS, pathInEpub);
    }

    /**
     * @param chapterDocument 章节文档
     * @param chapterName     章节名称
     */
    public void addChapterResource(Document chapterDocument, String chapterName) {
        // 自动生成路径
        String pathInEpub = getChapterResourcePath(++this.chapterResourceCount);
        EpubResource epubResource = new EpubChapterResource(pathInEpub, chapterDocument);
        // 添加资源
        this.resourceList.add(epubResource);
        // 获取相对路径
        String relativePath = relative(EpubConstant.PATH_OEBPS, pathInEpub);
        // 添加章节信息和文件信息
        this.opf.addChapter(relativePath);
        this.ncx.addChapter(chapterName, relativePath);
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

    private static String getImageResourcePath(int index) {
        return String.format(EpubConstant.PATH_IMAGES + "%04d.jpg", index);
    }

    private static String getChapterResourcePath(int index) {
        return String.format(EpubConstant.PATH_OEBPS + "chapter%04d.xhtml", index);
    }

    public static String relative(String from, String to) {
        Path sourcePath = StrUtil.endWith(from, "/") ? Paths.get(from) : Paths.get(from).getParent();
        Path destPath = Paths.get(to);
        Path relativize = sourcePath.relativize(destPath);
        return relativize.toString().replace("\\", "/");
    }

}
