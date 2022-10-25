package cn.montaro.linovelib.epub.resource;

import cn.hutool.core.io.IoUtil;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Description:
 *
 * @author ZhangJiaYu
 * @date 2022/6/23
 */
public class EpubChapterResource extends EpubResource {

    private final Document doc;

    public EpubChapterResource(String pathInEpub, Document doc) {
        super(pathInEpub);
        this.doc = doc;
        doc.outputSettings(
                new Document.OutputSettings()
                        .syntax(Document.OutputSettings.Syntax.xml)
                        .prettyPrint(true)
        );
    }

    @Override
    public InputStream getStream() {
        return IoUtil.toStream(doc.html(), StandardCharsets.UTF_8);
    }
}
