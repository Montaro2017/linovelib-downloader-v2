package cn.montaro.linovelib.epub;

import org.junit.jupiter.api.Test;

public class EpubPackerTest {

    @Test
    public void testInstance() {
        EpubPacker epubPacker = new EpubPacker();
        System.out.println("epubPacker.resourceList = " + epubPacker.resourceList);
    }

}
