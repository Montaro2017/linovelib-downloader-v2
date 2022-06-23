package cn.montaro.linovelib.epub;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.Test;

import java.io.File;

public class EpubPackerTest {

    @Test
    public void testInstance() {
        EpubPacker epubPacker = new EpubPacker();
        epubPacker.addChapterResource("aaa", "第一章");
        File file = epubPacker.pack("aaa.zip");
        System.out.println("file.getAbsolutePath() = " + file.getAbsolutePath());
    }

}
