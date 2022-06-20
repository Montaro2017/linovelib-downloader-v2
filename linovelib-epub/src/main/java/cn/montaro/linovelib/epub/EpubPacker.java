package cn.montaro.linovelib.epub;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.util.ZipUtil;
import cn.montaro.linovelib.epub.resource.EpubResource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class EpubPacker {

    List<EpubResource> resourceList = new ArrayList<>();

    public void addResource(EpubResource resource) {
        resourceList.add(resource);
    }

    public void pack(String output) {
        File outputFile = FileUtil.file(output);
        Resource[] resources = this.resourceList.toArray(new Resource[]{});
        ZipUtil.zip(outputFile, StandardCharsets.UTF_8, resources);
    }

}
