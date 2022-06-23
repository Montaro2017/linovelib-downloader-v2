package cn.montaro.linovelib.epub.resource;

import cn.hutool.core.io.IoUtil;
import cn.montaro.linovelib.epub.constant.EpubConstant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class OPFResource extends EpubResource {

    private Document doc = null;

    private Element idEl;
    private Element titleEl;
    private Element coverEl;
    private Element manifestEl;
    private Element spineEl;

    private OPFResource(Document doc) {
        super(EpubConstant.PATH_CONTENT_OPF);
        assert doc != null;
        this.doc = doc;
        this.idEl = doc.getElementsByTag("dc:identifier").first();
        this.titleEl = doc.getElementsByTag("dc:title").first();
        this.manifestEl = doc.getElementsByTag("manifest").first();
        this.spineEl = doc.getElementsByTag("spine").first();
    }

    public static OPFResource newInstance() {
        Document doc = Jsoup.parse(EpubConstant.CONTENT_CONTENT_OPF, Parser.xmlParser());
        doc.outputSettings().prettyPrint(true);
        return new OPFResource(doc);
    }

    public void setBookId(String bookId) {
        this.idEl.text(bookId);
    }

    public void setTitle(String epubTitle) {
        this.titleEl.text(epubTitle);
    }

    public void setCover(String relativeImagePath) {
        if (this.coverEl == null) {
            Element item = doc.createElement("item");
            item.attr("id", "cover-image");
            item.attr("media-type", "image/jpeg");
            this.manifestEl.appendChild(item);
            this.coverEl = item;
        }
        this.coverEl.attr("href", relativeImagePath);
    }

    public void addChapter(String relativeChapterPath) {
        Element item = doc.createElement("item");
        item.attr("id", relativeChapterPath);
        item.attr("href", relativeChapterPath);
        item.attr("media-type", "application/xhtml+xml");
        this.manifestEl.appendChild(item);

        Element itemRef = doc.createElement("itemref");
        itemRef.attr("idref", relativeChapterPath);
        this.spineEl.appendChild(itemRef);
    }

    @Override
    public InputStream getStream() {
        return IoUtil.toStream(doc.outerHtml(), StandardCharsets.UTF_8);
    }
}
