package cn.montaro.linovelib.cli;

import cn.hutool.core.util.StrUtil;
import cn.montaro.linovelib.common.model.SimpleImageInfo;
import cn.montaro.linovelib.common.util.FastImageUtil;
import cn.montaro.linovelib.core.fetcher.Fetcher;
import cn.montaro.linovelib.core.model.Catalog;
import cn.montaro.linovelib.core.model.Chapter;
import cn.montaro.linovelib.core.model.Novel;
import cn.montaro.linovelib.core.model.Volume;
import cn.montaro.linovelib.epub.EpubPacker;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Description:
 *
 * @author ZhangJiaYu
 * @date 2022/6/23
 */
@Slf4j
public class MainTest {

    @Test
    public void test() {
        long bookId = 2704;
        Novel novel = Fetcher.fetchNovel(bookId);
        assert novel != null;
        Catalog catalog = novel.getCatalog();
        Volume volume = catalog.getVolumeList().get(0);
        EpubPacker packer = new EpubPacker();

        packer.setBookName(novel.getNovelName() + " " + volume.getVolumeName());
        List<Chapter> chapterList = volume.getChapterList();
        for (Chapter chapter : chapterList) {
            String chapterUrl = chapter.getChapterUrl();
            Document doc = Fetcher.fetchChapterContent(chapterUrl);
            packer.addChapterResource(doc, chapter.getChapterName(), false);
            packer.resolveImage(doc, (bytes, path) -> {
                if (StrUtil.isEmpty(packer.getCoverRelativePath())) {
                    SimpleImageInfo imageInfo = FastImageUtil.getImageInfo(bytes);
                    if (imageInfo != null && imageInfo.getRatio() < 1) {
                        packer.setCover(path);
                        log.debug("设置封面: {}", path);
                    }
                }
            });
        }
    }

}
