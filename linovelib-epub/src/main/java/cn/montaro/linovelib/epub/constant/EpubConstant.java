package cn.montaro.linovelib.epub.constant;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.montaro.linovelib.epub.resource.EpubTextResource;

import java.nio.charset.StandardCharsets;

public class EpubConstant {

    public static final String PATH_MIMETYPE = "mimetype";

    public static final String PATH_CONTAINER = "META-INF/container.xml";

    public static final String PATH_OEBPS = "OEBPS/";

    public static final String PATH_IMAGES = PATH_OEBPS + "images/";

    public static final String PATH_CONTENT_OPF = PATH_OEBPS + "content.opf";

    public static final String PATH_TOC_NCX = PATH_OEBPS + "toc.ncx";

    public static final String CONTENT_MIMETYPE = "application/epub+zip";

    public static final String CONTENT_CONTENT_OPF = ResourceUtil.readStr(PATH_CONTENT_OPF, StandardCharsets.UTF_8);

    public static final String CONTENT_TOC_NCX = ResourceUtil.readStr(PATH_TOC_NCX, StandardCharsets.UTF_8);

    /**
     * MIMETYPE文件
     */
    public static final EpubTextResource RESOURCE_MIMETYPE = new EpubTextResource(PATH_MIMETYPE, CONTENT_MIMETYPE);

    /**
     * container.xml文件
     */
    public static final EpubTextResource RESOURCE_CONTAINER = new EpubTextResource(PATH_CONTAINER, ResourceUtil.readStr(PATH_CONTAINER, StandardCharsets.UTF_8));

}
