package cn.montaro.linovelib.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleImageInfo {

    private Integer width;
    private Integer height;
    private String mimeType;

    /**
     * 获取图片比例信息 宽比高
     *
     * @return
     */
    public Double getRatio() {
        return width.doubleValue() / height.doubleValue();
    }

}
