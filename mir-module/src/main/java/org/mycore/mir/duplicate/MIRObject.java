package org.mycore.mir.duplicate;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MIRObject {
    public MIRObject(String id, Map<String, Collection<Object>> fields) {
        this.id = id;
        this.fields = fields;
    }

    public String id;

    public Map<String, Collection<Object>> fields;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, " Similarity Dokument: %s ", id);
    }
}
