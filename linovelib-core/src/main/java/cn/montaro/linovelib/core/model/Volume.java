package cn.montaro.linovelib.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 卷
 */
@Data
@Accessors
public class Volume {

    /**
     * 卷名
     */
    private String volumeName;

    /**
     * 章节列表
     */
    private List<Chapter> chapterList;

    public void addChapter(Chapter chapter) {
        if (this.chapterList == null) {
            this.chapterList = new ArrayList<>();
        }
        this.chapterList.add(chapter);
    }

}