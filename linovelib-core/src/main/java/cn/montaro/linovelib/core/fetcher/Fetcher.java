package cn.montaro.linovelib.core.fetcher;

import cn.hutool.core.util.StrUtil;
import cn.montaro.linovelib.common.util.HttpRetryUtil;
import cn.montaro.linovelib.core.constant.Constant;
import cn.montaro.linovelib.core.constant.TextMapConstant;
import cn.montaro.linovelib.core.model.Catalog;
import cn.montaro.linovelib.core.model.Chapter;
import cn.montaro.linovelib.core.model.Novel;
import cn.montaro.linovelib.core.model.Volume;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Fetcher {

    /**
     * 获取小说基本信息
     *
     * @param id 小说id
     * @return
     */
    public static Novel fetchNovel(long id) {
        String novelUrl = getNovelUrl(id);

        Document doc = Jsoup.parse(HttpRetryUtil.get(novelUrl));
        if (isErrorPage(doc)) {
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
        Catalog catalog = Fetcher.fetchCatalog(id);
        novel.setCatalog(catalog);
        return novel;
    }

    /**
     * 获取小说目录信息
     *
     * @param id 小说id
     * @return
     */
    public static Catalog fetchCatalog(long id) {
        String novelCatalogUrl = getNovelCatalogUrl(id);

        Document doc = Jsoup.parse(HttpRetryUtil.get(novelCatalogUrl));
        if (isErrorPage(doc)) {
            return null;
        }

        Catalog catalog = new Catalog();

        Elements elements = doc.select(".chapter-list > *");
        Chapter beforeChapter = null;

        Volume volume = new Volume();
        for (Element element : elements) {
            // 卷 div.class=volume
            if (element.classNames().contains("volume")) {
                volume = new Volume();
                volume.setVolumeName(element.text());
                catalog.addVolume(volume);
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

    /**
     * 当前是否是错误页面
     *
     * @param doc 页面文档
     * @return
     */
    private static boolean isErrorPage(Document doc) {
        Element title = doc.head().selectFirst("title");
        if (title == null || StrUtil.contains(title.text(), "错误")) {
            return true;
        }
        return false;
    }

    /**
     * 获取章节内容文档
     *
     * @param chapterUrl
     * @return
     */
    public static Document fetchChapterContent(String chapterUrl) {
        // 避免缺失html、head和body标签以至于某些软件无法识别
        Document doc = Jsoup.parse("");
        Element body = doc.body();
        Element lastParagraph = null;
        boolean isEnd;
        do {
            Document page = Jsoup.parse(HttpRetryUtil.get(chapterUrl));
            Element currentDocEl = page.selectFirst("#TextContent");
            if (currentDocEl == null) {
                return doc;
            }
            currentDocEl.select(".tp").remove();
            currentDocEl.select(".bd").remove();
            currentDocEl = handleFontSecret(currentDocEl);
            // 删除img标签的外部标签以解决在某些软件上只能显示一张图的问题
            currentDocEl.select(".divimage").unwrap();
            if (lastParagraph != null && !isParagraphEnds(lastParagraph)) {
                // 处理断行问题 文字拼接成一个段落
                String text = lastParagraph.text();
                Element firstParagraph = currentDocEl.select("p").first();
                if (firstParagraph != null) {
                    text = text + firstParagraph.text();
                    firstParagraph.remove();
                }
                lastParagraph.text(text);
            }
            // 获取最后一个段落 要在appendChildren之前 否则获取到为null
            lastParagraph = currentDocEl.select("p").last();
            body.appendChildren(currentDocEl.children());
            Element next = page.selectFirst(".mlfy_page>a:last-child");
            if (next == null) {
                return doc;
            }
            isEnd = StrUtil.containsIgnoreCase(next.text(), Constant.NEXT_CHAPTER) || StrUtil.containsIgnoreCase(next.text(), Constant.RETURN_CATALOG);
            chapterUrl = Constant.DOMAIN + next.attr(Constant.LINK_ATTR_HREF);
        } while (!isEnd);
        return doc;
    }

    /**
     * 获取小说主页url
     * 形如：<a>https://www.linovelib.com/novel/1.html</a>
     *
     * @param id 小说数字id
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

    /**
     * 获得上一章节的url
     *
     * @param chapterUrl 当前章节url
     * @return
     */
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

    /**
     * 获得下一章节的url
     *
     * @param chapterUrl 当前章节url
     * @return
     */
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

    /**
     * 处理字体加密
     *
     * @param el 文档
     * @return
     */
    private static Element handleFontSecret(Element el) {
        if (el == null) {
            return null;
        }
        el.forEachNode((node) -> {
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                StringBuilder sb = new StringBuilder(textNode.text());
                int length = sb.length();
                for (int i = 0; i < length; i++) {
                    char c = sb.charAt(i);
                    Character replacement = TextMapConstant.TEXT_MAP.get(c);
                    if (replacement != null) {
                        sb.setCharAt(i, replacement);
                    }
                }
                textNode.text(sb.toString());
            }
        });
        return el;
    }

    /**
     * 判断当前段落是否结束
     *
     * @param lastParagraph 当前页最后一个段落
     * @return 段落是否结束
     */
    private static boolean isParagraphEnds(Element lastParagraph) {
        if (lastParagraph == null) {
            return true;
        }
        String lastParagraphContent = lastParagraph.text();
        if (StrUtil.isEmpty(lastParagraphContent)) {
            return true;
        }
        if (StrUtil.contains(lastParagraphContent, "「") && !StrUtil.contains(lastParagraphContent, "」")) {
            return false;
        }
        // 判断段落最后一个字符是否是任意结束字符
        char lastChar = lastParagraphContent.charAt(lastParagraphContent.length() - 1);
        for (char endChar : Constant.END_CHARS) {
            if (lastChar == endChar) {
                return true;
            }
        }
        return false;
    }

}
