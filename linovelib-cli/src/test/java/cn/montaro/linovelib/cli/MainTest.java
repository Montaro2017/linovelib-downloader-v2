package cn.montaro.linovelib.cli;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.montaro.linovelib.common.model.SimpleImageInfo;
import cn.montaro.linovelib.common.util.FastImageUtil;
import cn.montaro.linovelib.common.util.HttpRetryUtil;
import cn.montaro.linovelib.core.fetcher.Fetcher;
import cn.montaro.linovelib.core.model.Catalog;
import cn.montaro.linovelib.core.model.Chapter;
import cn.montaro.linovelib.core.model.Novel;
import cn.montaro.linovelib.core.model.Volume;
import cn.montaro.linovelib.epub.EpubPacker;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Description:
 *
 * @author ZhangJiaYu
 * @date 2022/6/23
 */
public class MainTest {

    @Test
    public void test() {
        long bookId = 2704;
        Novel novel = Fetcher.fetchNovel(2704);

        Catalog catalog = Fetcher.fetchCatalog(bookId);

        Volume volume = catalog.getVolumeList().get(0);
        EpubPacker packer = new EpubPacker();

        packer.setBookName(novel.getNovelName() + " " + volume.getVolumeName());
        List<Chapter> chapterList = volume.getChapterList();
        for (Chapter chapter : chapterList) {
            String chapterUrl = chapter.getChapterUrl();
            Document doc = Fetcher.fetchChapterContent(chapterUrl);
            resolveImages(doc, packer);
            packer.addChapterResource(doc, chapter.getChapterName(), false);
            packer.resolveImage(doc, (bytes, path) -> {
                if (StrUtil.isEmpty(packer.getCoverRelativePath())) {
                    SimpleImageInfo imageInfo = FastImageUtil.getImageInfo(bytes);
                    if (imageInfo != null && imageInfo.getRatio() < 1) {
                        packer.setCover(path);
                    }
                }
            });
        }
        packer.pack("Volume1.epub");
    }

    public void resolveImages(Document doc, EpubPacker packer) {
        Elements imageList = doc.select("img");
        for (Element img : imageList) {
            String src = img.attr("src");
            if (src.startsWith("//")) {
                src = "https:" + src;
            }
            Console.log("下载图片 {} ", src);
            byte[] imageBytes = HttpRetryUtil.getBytes(src);
            src = packer.addImageResource(imageBytes);
            img.attr("src", src);
        }
    }

}
