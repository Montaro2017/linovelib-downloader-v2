package cn.montaro.linovelib.cli;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.montaro.linovelib.common.model.SimpleImageInfo;
import cn.montaro.linovelib.common.util.FastImageUtil;
import cn.montaro.linovelib.core.fetcher.Fetcher;
import cn.montaro.linovelib.core.model.Catalog;
import cn.montaro.linovelib.core.model.Chapter;
import cn.montaro.linovelib.core.model.Novel;
import cn.montaro.linovelib.core.model.Volume;
import cn.montaro.linovelib.epub.EpubPacker;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.List;

/**
 * Description:
 *
 * @author ZhangJiaYu
 * @date 2022/6/23
 */
public class Main {

    public static final String VERSION = "1.0.0";

    public static final String GIT_URL = "https://gitee.com/Montaro2017/linovelib-downloader-v2";

    public static void main(String[] args) {
        printWelcome();
        start();
    }

    public static void printWelcome() {
        Console.log();
        Console.log("欢迎使用哔哩轻小说下载器！");
        Console.log("作者: {}", "Sparks");
        Console.log("当前版本: {}", VERSION);
        Console.log("如遇报错请先查看能否正常访问 https://www.linovelib.com");
        Console.log("否则请至开源地址携带报错信息进行反馈: {}", GIT_URL);
        Console.log();
    }

    public static void start() {
        Console.log("请输入小说id或URL:");
        String input = Console.scanner().nextLine();
        int novelId = getNovelId(input);
        Console.log();
        Novel novel = Fetcher.fetchNovel(novelId);
        Console.log("书名: {}", novel.getNovelName());
        Console.log("作者: {}", novel.getAuthor());
        Console.log("标签: {}", CollectionUtil.join(novel.getLabels(), ", "));
        Console.log("简介: {}", novel.getNovelDesc());

        pause();

        epub(novel);

    }

    private static int getNovelId(String input) {
        Integer id = Convert.toInt(input);
        if (id != null) {
            return id;
        }
        id = Convert.toInt(ReUtil.get("novel/(\\d+)", input, 1));
        if (id == null) {
            throw new RuntimeException("请输入小说id或url！");
        }
        return id;
    }

    @SneakyThrows
    public static void pause() {
        Console.log("请按回车键继续...");
        Console.scanner().nextLine();
    }

    private static void epub(Novel novel) {
        File dir = FileUtil.mkdir(novel.getNovelName());
        if (dir == null) {
            throw new RuntimeException("创建文件夹失败");
        }
        Console.log("EPUB文件保存地址: {}", dir.getAbsolutePath());
        Catalog catalog = novel.getCatalog();
        List<Volume> volumeList = catalog.getVolumeList();
        for (Volume volume : volumeList) {
            EpubPacker packer = new EpubPacker();
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
                        }
                    }
                });
            }
            File dest = FileUtil.file(dir, StrUtil.format("{} {}.epub", novel.getNovelName(), volume.getVolumeName()));
            packer.pack(dest);
            if (dest.exists()) {
                Console.log("打包：{} 成功", FileUtil.getPrefix(dest));
            } else {
                Console.log("打包：{} 失败", FileUtil.getPrefix(dest));
            }
        }
    }
}
