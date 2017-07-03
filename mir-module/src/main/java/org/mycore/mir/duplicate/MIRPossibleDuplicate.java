package org.mycore.mir.duplicate;

import java.util.Locale;
import java.util.Map;


public class MIRPossibleDuplicate {
    private String id;

    private Double score;

    private Map<String, String> fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override public String toString() {
        return String.format(Locale.ROOT, " Similarity Dokument: %s  Score: %s ", id, score);
    }
}
