package cn.montaro.linovelib.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 目录
 */
@Data
@Accessors
public class Catalog {

    private List<Volume> volumeList;

    public void addVolume(Volume volume) {
        if (this.volumeList == null) {
            this.volumeList = new ArrayList<>();
        }
        this.volumeList.add(volume);
    }

    public Chapter getPrevChapter(Chapter findChapter) {
        List<Chapter> chapterList = volumeList.stream()
                .flatMap(volume -> volume.getChapterList().stream())
                .collect(Collectors.toList());
        int pos = chapterList.indexOf(findChapter);
        if (pos < 1) {
            return null;
        }
        return chapterList.get(pos - 1);

    }

    public Chapter getNextChapter(Chapter findChapter) {
        List<Chapter> chapterList = volumeList.stream()
                .flatMap(volume -> volume.getChapterList().stream())
                .collect(Collectors.toList());
        int pos = chapterList.indexOf(findChapter);
        if (pos == -1 || pos > chapterList.size() - 1) {
            return null;
        }
        return chapterList.get(pos + 1);
    }

}
