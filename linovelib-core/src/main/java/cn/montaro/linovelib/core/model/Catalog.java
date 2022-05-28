package cn.montaro.linovelib.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

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

}
