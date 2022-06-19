package cn.montaro.linovelib.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * read.ttf字体加密映射关系
 * 字体url：https://www.linovelib.com//public/font/read.ttf
 * 使用工具FontCreator打开就能看到unicode和字形的关系
 * OCR识别然后手动处理
 * </p>
 * <p>
 * 字体加密可能会随时间而改变
 * 本次时间为：2022/06/19
 * </p>
 */
public class TextMapConstant {

    /**
     * unicode
     * 如果不使用指定字体会显示成乱码
     */
    public static final Character[] UNICODE_ARR = new Character[]{
            '\uE800', '\uE801', '\uE802', '\uE803', '\uE804', '\uE805', '\uE806', '\uE807', '\uE808', '\uE809',
            '\uE80A', '\uE80B', '\uE80C', '\uE80D', '\uE80E', '\uE80F', '\uE810', '\uE811', '\uE812', '\uE813',
            '\uE814', '\uE815', '\uE816', '\uE817', '\uE818', '\uE819', '\uE81A', '\uE81B', '\uE81C', '\uE81D',
            '\uE81E', '\uE81F', '\uE820', '\uE821', '\uE822', '\uE823', '\uE824', '\uE825', '\uE826', '\uE827',
            '\uE828', '\uE829', '\uE82A', '\uE82B', '\uE82C', '\uE82D', '\uE82E', '\uE82F', '\uE830', '\uE831',
            '\uE832', '\uE833', '\uE834', '\uE835', '\uE836', '\uE837', '\uE838', '\uE839', '\uE83A', '\uE83B',
            '\uE83C', '\uE83D', '\uE83E', '\uE83F', '\uE840', '\uE841', '\uE842', '\uE843', '\uE844', '\uE845',
            '\uE846', '\uE847', '\uE848', '\uE849', '\uE84A', '\uE84B', '\uE84C', '\uE84D', '\uE84E', '\uE84F',
            '\uE850', '\uE851', '\uE852', '\uE853', '\uE854', '\uE855', '\uE856', '\uE857', '\uE858', '\uE859',
            '\uE85A', '\uE85B', '\uE85C', '\uE85D', '\uE85E', '\uE85F', '\uE860', '\uE861', '\uE862', '\uE863'
    };

    /**
     * 对应显示文字
     */
    public static final Character[] TEXT_ARR = new Character[]{
            '的', '一', '是', '了', '我', '不', '人', '在', '他', '有',
            '这', '个', '上', '们', '来', '到', '时', '大', '地', '为',
            '子', '中', '你', '说', '生', '国', '年', '着', '就', '那',
            '和', '要', '她', '出', '也', '得', '里', '后', '自', '以',
            '会', '家', '可', '下', '而', '过', '天', '去', '能', '对',
            '小', '多', '然', '于', '心', '学', '么', '之', '都', '好',
            '看', '起', '发', '当', '没', '成', '只', '如', '事', '把',
            '还', '用', '第', '样', '道', '想', '作', '种', '开', '美',
            '乳', '阴', '液', '茎', '欲', '呻', '肉', '交', '性', '胸',
            '私', '穴', '淫', '臀', '舔', '射', '脱', '裸', '骚', '唇'
    };

    public static Map<Character, Character> TEXT_MAP = new HashMap<>();

    static {
        int size = UNICODE_ARR.length;
        for (int i = 0; i < size; i++) {
            TEXT_MAP.put(UNICODE_ARR[i], TEXT_ARR[i]);
        }
    }
}
