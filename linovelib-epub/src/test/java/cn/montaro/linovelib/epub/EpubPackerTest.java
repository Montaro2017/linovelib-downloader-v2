package cn.montaro.linovelib.epub;

import cn.montaro.linovelib.epub.resource.EpubTextResource;
import org.junit.jupiter.api.Test;

import java.io.File;

public class EpubPackerTest {

    @Test
    public void testInstance() {
        EpubPacker epubPacker = new EpubPacker();
        epubPacker.addOtherResource(new EpubTextResource("OEBPS/aaa.xhtml", "第一章"));
        File file = epubPacker.pack("aaa.zip");
        System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
    }

}
