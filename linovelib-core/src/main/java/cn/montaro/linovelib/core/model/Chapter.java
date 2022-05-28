package cn.montaro.linovelib.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 章节
 */
@Data
@Accessors(chain = true)
public class Chapter {

    /**
     * 章节标题
     */
    private String chapterName;

    /**
     * Url
     */
    private String chapterUrl;

}