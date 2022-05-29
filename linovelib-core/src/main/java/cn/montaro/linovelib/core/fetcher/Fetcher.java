package cn.montaro.linovelib.core.fetcher;

import cn.hutool.core.util.StrUtil;
import cn.montaro.linovelib.common.util.HttpRetryUtil;
import cn.montaro.linovelib.core.constant.Constant;
import cn.montaro.linovelib.core.model.Catalog;
import cn.montaro.linovelib.core.model.Chapter;
import cn.montaro.linovelib.core.model.Novel;
import cn.montaro.linovelib.core.model.Volume;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Fetcher {

    /**
     * 获取小说基本信息
     *
     * @param id
     * @return
     */
    public static Novel fetchNovel(long id) {
        String novelUrl = getNovelUrl(id);

        Document doc = Jsoup.parse(HttpRetryUtil.get(novelUrl));
        if (isError(doc)) {
            return null;
        }

        Novel novel = new Novel();
        novel.setId(id)
                .setNovelName(doc.selectFirst(".book-name").text())
                .setAuthor(doc.selectFirst(".au-name>a").text())
                .setNovelDesc(doc.selectFirst(".book-dec>p").html())
                .setLabels(new ArrayList<>());

        Elements labelElements = doc.select(".book-label a");
        labelElements.forEach(element -> novel.getLabels().add(element.text()));
        return novel;
    }

    /**
     * 获取小说目录信息
     *
     * @param id
     * @return
     */
    public static Catalog fetchCatalog(long id) {
        String novelCatalogUrl = getNovelCatalogUrl(id);

        Document doc = Jsoup.parse(HttpRetryUtil.get(novelCatalogUrl));
        if (isError(doc)) {
            return null;
        }

        Catalog catalog = new Catalog();

        Elements elements = doc.select(".chapter-list > *");
        boolean first = true;
        Chapter beforeChapter = null;

        Volume volume = new Volume();
        for (Element element : elements) {
            // 卷 div.class=volume
            if (element.classNames().contains("volume")) {
                if (!first) {
                    catalog.addVolume(volume);
                }
                first = false;
                volume = new Volume();
                volume.setVolumeName(element.text());

            }
            // 章节 li.class=col-4
            if (element.classNames().contains("col-4")) {
                Elements a = element.select("a");
                String chapterUrl = a.attr(Constant.LINK_ATTR_HREF);

                chapterUrl = (StrUtil.isEmpty(chapterUrl) || StrUtil.containsIgnoreCase(chapterUrl, "javascript")) ? null : Constant.DOMAIN + chapterUrl;

                Chapter chapter = new Chapter()
                        .setChapterName(a.text())
                        .setChapterUrl(chapterUrl);
                if (beforeChapter != null && StrUtil.isEmpty(beforeChapter.getChapterUrl()) && StrUtil.isNotEmpty(chapterUrl)) {
                    // 如果上一章url为空且本章url不为空 则从本章获取
                    String prevChapterUrl = getPrevChapterUrl(chapterUrl);
                    beforeChapter.setChapterUrl(prevChapterUrl);
                }
                if (beforeChapter != null && StrUtil.isNotEmpty(beforeChapter.getChapterUrl()) && StrUtil.isEmpty(chapter.getChapterUrl())) {
                    // 从本章url为空且上一章不url为空 则从上一章获取
                    String nextChapterUrl = getNextChapterUrl(beforeChapter.getChapterUrl());
                    chapter.setChapterUrl(nextChapterUrl);
                }
                volume.addChapter(chapter);
                beforeChapter = chapter;
            }
        }

        // 针对部分章节获取不到url的情况，遍历出没有url的章节，通过上一章或下一章页面中的html获取到
        return catalog;
    }

    private static boolean isError(Document doc) {
        Element title = doc.head().selectFirst("title");
        if (title == null || StrUtil.contains(title.text(), "错误")) {
            return true;
        }
        return false;
    }

    public static String fetchChapterContent(String chapterUrl) {
        Document doc = new Document("");
        boolean isEnd;
        do {
            Document page = Jsoup.parse(HttpRetryUtil.get(chapterUrl));
            Element contentElement = page.selectFirst("#TextContent");
            if (contentElement == null) {
                return doc.html();
            }
            contentElement.select(".tp").remove();
            contentElement.select(".bd").remove();
            doc.append(contentElement.html());
            Element next = page.selectFirst(".mlfy_page>a:last-child");
            if (next == null) {
                return "";
            }
            isEnd = StrUtil.containsIgnoreCase(next.text(), Constant.NEXT_CHAPTER);
            chapterUrl = Constant.DOMAIN + next.attr(Constant.LINK_ATTR_HREF);
        } while (!isEnd);
        return doc.html();
    }

    /**
     * 获取小说主页url
     * 形如：<a>https://www.linovelib.com/novel/1.html</a>
     *
     * @param id
     * @return
     */
    private static String getNovelUrl(long id) {
        return StrUtil.format("{}/novel/{}.html", Constant.DOMAIN, id);
    }

    /**
     * 获取小说目录url
     * 形如：<a>https://www.linovelib.com/novel/1/catalog</a>
     *
     * @param id
     * @return
     */
    private static String getNovelCatalogUrl(long id) {
        return StrUtil.format("{}/novel/{}/catalog", Constant.DOMAIN, id);
    }

    private static String getPrevChapterUrl(String chapterUrl) {
        Document doc = Jsoup.parse(HttpRetryUtil.get(chapterUrl));
        Element prev = doc.selectFirst(".mlfy_page>a");
        if (prev == null) {
            return null;
        }
        if (StrUtil.containsIgnoreCase(prev.text(), Constant.PREV_CHAPTER)) {
            return Constant.DOMAIN + prev.attr(Constant.LINK_ATTR_HREF);
        }
        return null;
    }

    private static String getNextChapterUrl(String chapterUrl) {
        int maxTimes = 20;
        for (int i = 0; i < maxTimes; i++) {
            Document doc = Jsoup.parse(HttpRetryUtil.get(chapterUrl));
            Element next = doc.selectFirst(".mlfy_page>a:last-child");
            if (next == null) {
                return null;
            }
            String nextUrl = next.attr(Constant.LINK_ATTR_HREF);
            if (StrUtil.containsIgnoreCase(next.text(), Constant.NEXT_CHAPTER)) {
                return Constant.DOMAIN + nextUrl;
            } else {
                chapterUrl = Constant.DOMAIN + nextUrl;
            }
        }
        return null;
    }

}
