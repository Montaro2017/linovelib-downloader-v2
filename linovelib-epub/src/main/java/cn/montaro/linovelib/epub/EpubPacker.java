package cn.montaro.linovelib.epub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.montaro.linovelib.common.model.SimpleImageInfo;
import cn.montaro.linovelib.common.util.HttpRetryUtil;
import cn.montaro.linovelib.epub.constant.EpubConstant;
import cn.montaro.linovelib.epub.resource.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
public class EpubPacker {

    private final List<EpubResource> resourceList = new ArrayList<>();
    // content.opf 元数据及文件清单
    private final OPFResource opf;
    // toc.ncx 目录
    private final NCXResource ncx;
    private int imageResourceCount = 0;
    private int chapterResourceCount = 0;
    private String cover;

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

    /**
     * <p>生成图片路径</p>
     * <p>如: OEBPS/images/0001.jpg</p>
     *
     * @param index 索引
     * @return
     */
    private static String getImageResourcePath(int index) {
        return String.format(EpubConstant.PATH_IMAGES + "%04d.jpg", index);
    }

    /**
     * <p>生成章节路径</p>
     * <p>如: OEBPS/chapter0001.xhtml</p>
     *
     * @param index 索引
     * @return
     */
    private static String getChapterResourcePath(int index) {
        return String.format(EpubConstant.PATH_OEBPS + "chapter%04d.xhtml", index);
    }

    /**
     * 获取相对路径
     *
     * @param from
     * @param to
     * @return
     */
    public static String relative(String from, String to) {
        Path sourcePath = StrUtil.endWith(from, "/") ? Paths.get(from) : Paths.get(from).getParent();
        Path destPath = Paths.get(to);
        Path relativize = sourcePath.relativize(destPath);
        return relativize.toString().replace("\\", "/");
    }

    /**
     * 设置EPUB名称
     *
     * @param bookName 名称
     */
    public void setBookName(String bookName) {
        opf.setTitle(bookName);
        ncx.setTitle(bookName);
    }

    /**
     * 设置封面图片
     *
     * @param relativePath EPUB中的图片相对路径
     */
    public void setCover(String relativePath) {
        opf.setCover(relativePath);
        this.cover = relativePath;
    }

    /**
     * 设置作者
     *
     * @param author 作者
     */
    public void setAuthor(String author) {
        opf.setCreator(author);
    }

    public String getCoverRelativePath() {
        return this.cover;
    }

    /**
     * 添加图片资源
     *
     * @param imageBytes 图片bytes
     * @return EPUB中的图片路径
     */
    public EpubImageResource addImageResource(byte[] imageBytes) {
        String pathInEpub = getImageResourcePath(++this.imageResourceCount);
        String relativeHref = relative(EpubConstant.PATH_OEBPS, pathInEpub);
        EpubImageResource epubImageResource = new EpubImageResource(pathInEpub, imageBytes, relativeHref);
        this.resourceList.add(epubImageResource);

        // 添加到manifest中 避免Koodo Reader无法正确显示
        this.opf.addManifest(relativeHref, relativeHref, epubImageResource.getImageInfo().getMimeType());
        return epubImageResource;
    }

    /**
     * 添加章节资源
     *
     * @param chapterDocument 章节文档
     * @param chapterName     章节名称
     */
    public void addChapterResource(Document chapterDocument, String chapterName, boolean autoResolveImage) {
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
        if (autoResolveImage) {
            this.resolveImage(chapterDocument, (imageInfo, path) -> {
                if (StrUtil.isEmpty(this.getCoverRelativePath())
                        && imageInfo != null
                        && imageInfo.getRatio() < 1) {
                    this.setCover(path);
                }
            });
        }
    }

    /**
     * 添加章节资源
     *
     * @param chapterDocument 章节文档
     * @param chapterName     章节名称
     */
    public void addChapterResource(Document chapterDocument, String chapterName) {
        this.addChapterResource(chapterDocument, chapterName, true);
    }

    public void resolveImage(Document doc, BiConsumer<SimpleImageInfo, String> consumer) {
        Elements imageList = doc.select("img");
        for (Element image : imageList) {
            String src = image.attr("src");
            if (StrUtil.startWith(src, "//")) {
                src = "https:" + src;
            }
            log.debug("下载图片: {}", src);
            byte[] imageBytes = HttpRetryUtil.getBytes(src);
            EpubImageResource imageResource = this.addImageResource(imageBytes);
            image.attr("src", imageResource.getPath());
            image.wrap("<div class=\"duokan-image-single\"></div>");
            if (consumer != null) {
                consumer.accept(imageResource.getImageInfo(), imageResource.getPath());
            }
        }
    }

    public void addOtherResource(EpubResource resource) {
        this.resourceList.add(resource);
    }

    /**
     * 打包资源为EPUB
     *
     * @param output 输出文件路径
     * @return
     */
    public File pack(String output) {
        File outputFile = FileUtil.file(output);
        return this.pack(outputFile);
    }

    /**
     * 打包资源为EPUB
     *
     * @param file 输出文件
     * @return
     */
    public File pack(File file) {
        int resourceCount = this.resourceList.size();
        Resource[] resources = this.resourceList.toArray(new Resource[resourceCount]);
        log.info("打包EPUB, 资源文件数: {}, 输出路径：{}", resourceCount, FileUtil.getAbsolutePath(file));
        return ZipUtil.zip(file, StandardCharsets.UTF_8, resources);
    }

}
