package cn.montaro.linovelib.epub.resource;

import cn.hutool.core.io.IoUtil;
import cn.montaro.linovelib.epub.constant.EpubConstant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class NCXResource extends EpubResource {

    private final Document doc;

    private final Element idEl;
    private final Element titleEl;
    private final Element navMapEl;

    private NCXResource(Document doc) {
        super(EpubConstant.PATH_TOC_NCX);
        assert doc != null;
        this.doc = doc;
        this.idEl = doc.select("meta[name='dtb:uid']").first();
        this.titleEl = doc.select("docTitle>text").first();
        this.navMapEl = doc.select("navMap").first();
    }

    public static NCXResource newInstance() {
        Document doc = Jsoup.parse(EpubConstant.CONTENT_TOC_NCX, Parser.xmlParser());
        doc.outputSettings().prettyPrint(true);
        return new NCXResource(doc);
    }

    public void setBookId(String id) {
        this.idEl.attr("content", "urn:uuid:" + id);
    }

    public void setTitle(String title) {
        this.titleEl.text(title);
    }

    public void addChapter(String chapterName, String relativeChapterPath) {
        int index = this.navMapEl.childNodeSize() + 1;
        Element navPoint = doc.createElement("navPoint");
        navPoint.attr("id", "navPoint-" + index);
        navPoint.attr("playOrder", String.valueOf(index));

        Element navLabel = doc.createElement("navLabel");

        Element text = doc.createElement("text");
        text.text(chapterName);
        navLabel.appendChild(text);

        Element content = doc.createElement("content");
        content.attr("src", relativeChapterPath);

        navPoint.appendChild(navLabel);
        navPoint.appendChild(content);
        this.navMapEl.appendChild(navPoint);
    }

    @Override
    public InputStream getStream() {
        return IoUtil.toStream(doc.outerHtml(), StandardCharsets.UTF_8);
    }
}
