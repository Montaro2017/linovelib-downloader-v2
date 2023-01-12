package cn.montaro.linovelib.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 章节
 */
@Data
@EqualsAndHashCode
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
