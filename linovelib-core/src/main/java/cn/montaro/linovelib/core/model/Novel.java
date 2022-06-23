package cn.montaro.linovelib.core.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Novel {

    /**
     * id
     */
    private Long id;
    /**
     * 小说标题
     */
    private String novelName;

    /**
     * 标签
     */
    private List<String> labels;

    /**
     * 小说简介
     */
    private String novelDesc;

    /**
     * 作者
     */
    private String author;

    /**
     * 目录
     */
    private Catalog catalog;

    public String getNovelDesc() {
        String novelDesc = this.novelDesc;
        novelDesc = StrUtil.replace(novelDesc, "<br> ", "\r\n");
        novelDesc = StrUtil.replace(novelDesc, "<br>", "\r\n");
        return novelDesc;
    }
}
