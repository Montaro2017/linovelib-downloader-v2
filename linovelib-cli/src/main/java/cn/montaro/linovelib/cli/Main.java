package cn.montaro.linovelib.cli;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.montaro.linovelib.core.fetcher.Fetcher;
import cn.montaro.linovelib.core.model.Catalog;
import cn.montaro.linovelib.core.model.Chapter;
import cn.montaro.linovelib.core.model.Novel;
import cn.montaro.linovelib.core.model.Volume;
import cn.montaro.linovelib.epub.EpubPacker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;

@Slf4j
public class Main {

    public static final String VERSION = "1.0.5";

    public static final String GIT_URL = "https://gitee.com/Montaro2017/linovelib-downloader-v2";

    public static void main(String[] args) {
        try {
            printWelcome();
            start();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
    }

    public static void printWelcome() {
        String latestVersion = getLatestVersion();
        if (StrUtil.isNotBlank(latestVersion)) {
            latestVersion = "(最新版本: " + latestVersion + ")";
        }
        Console.log();
        Console.log("欢迎使用哔哩轻小说下载器！");
        Console.log("作者: {}", "Sparks");
        Console.log("当前版本: {} {}", VERSION, latestVersion);
        Console.log("如遇报错请先查看能否正常访问 https://www.linovelib.com");
        Console.log("否则请至开源地址携带报错信息进行反馈: {}", GIT_URL);
        Console.log();
    }

    public static void start() {
        Console.log("请输入小说id或URL:");
        String input = Console.scanner().nextLine();
        log.info("输入内容: {}", input);
        int novelId = getNovelId(input);
        log.info("获取到ID: {}", novelId);
        Console.log("正在获取小说详情及目录，请耐心等待...");
        Console.log();
        Novel novel = Fetcher.fetchNovel(novelId);
        if (novel == null) {
            throw new RuntimeException("获取小说内容为空");
        }
        Console.log("书名: {}", novel.getNovelName());
        Console.log("作者: {}", novel.getAuthor());
        Console.log("标签: {}", CollectionUtil.join(novel.getLabels(), ", "));
        Console.log("简介: {}", novel.getNormalizedDesc());

        if (Boolean.TRUE.equals(novel.getNotOnTheShelf())) {
            Console.log();
            Console.log("当前小说已下架，无法通过本软件下载！");
            return;
        }
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
        String dirName = novel.getNovelName();
        dirName = ensureFileName(dirName);
        File dir = FileUtil.mkdir(dirName);
        if (dir == null || !dir.exists()) {
            log.error("创建文件夹失败 {}", dirName);
            throw new RuntimeException("创建文件夹失败 " + dirName);
        }
        // 保存目录
        String destDir = dir.getAbsolutePath();
        Catalog catalog = novel.getCatalog();
        List<Volume> volumeList = catalog.getVolumeList();
        for (Volume volume : volumeList) {
            packVolume(novel, volume, destDir);
        }
    }

    public static String ensureFileName(String name) {
        // 去除文件名中的特殊字符及前后空格
        return StrUtil.trim(
                StrUtil.replace(name, "<|>|:|\"|/|\\\\|\\?|\\*|\\\\|\\|", (Func1<Matcher, String>) parameter -> " ")
        );
    }

    private static void packVolume(Novel novel, Volume volume, String destDir) {
        EpubPacker packer = new EpubPacker();
        Console.log("下载：{}", volume.getVolumeName());
        List<Chapter> chapterList = volume.getChapterList();
        for (Chapter chapter : chapterList) {
            Console.print("\t下载: {}", chapter.getChapterName());
            String chapterUrl = chapter.getChapterUrl();
            if (StrUtil.isBlank(chapterUrl)) {
                chapterUrl = Fetcher.resolveChapterUrl(novel.getCatalog(), chapter);
                chapter.setChapterUrl(chapterUrl);
            }
            if (StrUtil.isNotBlank(chapterUrl)) {
                Document doc = Fetcher.fetchChapterContent(chapterUrl);
                packer.addChapterResource(doc, chapter.getChapterName(), true);
                Console.log();
            } else {
                Console.log("\n\t\t 【X 错误 章节链接为空】");
            }
        }
        packer.setAuthor(novel.getAuthor());
        packer.setBookName(StrUtil.format("{} {}", novel.getNovelName(), volume.getVolumeName()));
        String fileName = StrUtil.format("{} {}.epub", novel.getNovelName(), volume.getVolumeName());
        fileName = ensureFileName(fileName);
        File dest = FileUtil.file(destDir, fileName);
        log.info("目标文件名: {}", dest.getAbsolutePath());
        packer.pack(dest);
        if (dest.exists()) {
            Console.log("打包：{} 成功", dest.getName());
            Console.log("EPUB文件保存地址: {}\n", destDir);
        } else {
            Console.log("打包：{} 失败\n", FileUtil.getPrefix(dest));
        }
    }

    private static String getLatestVersion() {
        try {
            String url = GIT_URL + "/raw/master/version";
            return HttpUtil.get(url, 1000);
        } catch (Exception e) {
            return null;
        }
    }
}
