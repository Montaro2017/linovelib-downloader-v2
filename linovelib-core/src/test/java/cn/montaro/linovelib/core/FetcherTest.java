package cn.montaro.linovelib.core;

import cn.hutool.core.util.StrUtil;
import cn.montaro.linovelib.core.fetcher.Fetcher;
import cn.montaro.linovelib.core.model.Catalog;
import cn.montaro.linovelib.core.model.Chapter;
import cn.montaro.linovelib.core.model.Novel;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FetcherTest {

    @Test
    public void testFetchNovel() {
        // 96
        long id = 0;
        Novel novel = Fetcher.fetchNovel(id);
        assert novel != null;
        System.out.println("novel = " + novel);
    }

    @Test
    public void testFetchNovelCatalog() {
        long id = 1L;
        Catalog catalog = Fetcher.fetchCatalog(id);
        assert catalog != null;
        if (catalog.getVolumeList() == null) {
            System.out.println("volumeList == null");
            return;
        }
        catalog.getVolumeList().forEach(volume -> {
            System.out.println(volume.getVolumeName());
            List<Chapter> chapterList = volume.getChapterList();
            chapterList.forEach(chapter -> {
                assert StrUtil.isNotEmpty(chapter.getChapterUrl());
                System.out.println("\t" + chapter.getChapterName() + "\t" + chapter.getChapterUrl());
            });
        });
        assert catalog.getVolumeList() != null;
        assert catalog.getVolumeList().size() != 0;
    }

    /**
     * 1-1000全部通过 测试暂未发现问题
     */
//    @Test
//    public void testCatalogChapterBatch() {
//        long begin = 66;
//        long end = 1000;
//        for (long id = begin; id <= end; id++) {
//            Catalog catalog = Fetcher.fetchCatalog(id);
//            if (CollUtil.isEmpty(catalog.getVolumeList())) {
//                System.out.println(StrUtil.format("ID = {} NOT EXIST", id));
//                continue;
//            }
//            for (Volume volume : catalog.getVolumeList()) {
//                List<Chapter> chapterList = volume.getChapterList();
//                if (CollUtil.isEmpty(chapterList)) {
//                    System.out.println(StrUtil.format("ID = {}, VOLUME = {}, SIZE = 0", id, volume.getVolumeName()));
//                    continue;
//                }
//                for (Chapter chapter : chapterList) {
//                    if (StrUtil.isEmpty(chapter.getChapterUrl())) {
//                        System.out.println(StrUtil.format("ID = {}, VOLUME = {}, {}->chapterUrl = null", id, volume.getVolumeName(), chapter.getChapterName()));
//                    }
//                }
//            }
//            System.out.println(StrUtil.format("ID = {} IS PASSED", id));
//        }
//    }

    @Test
    public void testFetchChapterContent(){
        String chapterUrl = "https://www.linovelib.com/novel/2704/127910.html";
        String s = Fetcher.fetchChapterContent(chapterUrl);
        System.out.println(s);
    }

}
