package com.jxust.qq.superquestionlib.util.algo;


import lombok.Getter;

public enum QuestionQuality {

    UNKNOWN_QUALITY(1),
    VAGUE_QUALITY(3),
    GRASP_QUALITY(5);
    @Getter
    private int id;
    QuestionQuality(int id) {
       this.id = id;
    }

    public static QuestionQuality qualityById(int id) {
        for (QuestionQuality quality : QuestionQuality.values()) {
            if (quality.getId() == id) {
                return quality;
            }
        }
        return null;
    }
}
