package org.mycore.mir.duplicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MIRDuplicateObjectStore {

    public MIRDuplicateObjectStore(SolrDocumentList result, NamedList<SolrDocumentList> documentList) {
        this();

        result.forEach(document -> {
            Map<String, Collection<Object>> fieldValuesMap = document.getFieldValuesMap();
            HashMap<String, Collection<Object>> hashMap = new HashMap<>();

            fieldValuesMap.keySet().forEach(key -> {
                if (!key.contains("score"))
                    hashMap.put(key, fieldValuesMap.get(key));
            });
            addObject(new MIRObject((String) document.getFieldValue("id"), hashMap));
        });

        for (Map.Entry<String, SolrDocumentList> element : documentList) {
            SolrDocumentList docList = element.getValue();
            String id = element.getKey();
            docList.forEach(doc -> {
                double score = Double.parseDouble(doc.getFieldValue("score").toString());
                if (score >= 40) {
                    addDuplicate(id, (String) doc.getFieldValue("id"), score);
                }
            });
        }
    }

    public MIRDuplicateObjectStore() {
        this(new HashMap<>(), new HashMap<>());
    }

    public MIRDuplicateObjectStore(Map<String, MIRObject> objects, Map<String, List<MIRDuplicate>> duplicates) {
        this.objects = objects;
        this.duplicates = duplicates;
    }

    // String = Objekt-ID
    public Map<String, MIRObject> objects;

    // String = Objekt-ID
    public Map<String, List<MIRDuplicate>> duplicates;

    public void addObject(MIRObject obj) {
        objects.put(obj.getId(), obj);
    }

    public void addDuplicate(String objectID, String duplicateObjectID, double score) {
        List<MIRDuplicate> duplicateList = this.duplicates
            .computeIfAbsent(objectID, (key) -> new ArrayList<MIRDuplicate>());
        duplicateList.add(new MIRDuplicate(score, duplicateObjectID));
    }

    public static class MIRDuplicate {
        public MIRDuplicate(double score, String objectID) {
            this.score = score;
            this.objectID = objectID;
        }

        public double score;

        public String objectID;
    }

    public Map<String, List<MIRDuplicate>> sortList(
        Map<String, List<MIRDuplicateObjectStore.MIRDuplicate>> duplicates) {
        return duplicates.entrySet().stream()
            .sorted(
                (e1, e2) -> {
                    int compare = Double.compare(e1.getValue().get(0).score, e2.getValue().get(0).score);
                    return compare*-1; // reverse the list
                }) /*
                 new Comparator<Map.Entry<String, List<MIRDuplicate>>>() {
                    @Override public int compare(
                        Map.Entry<String, List<MIRDuplicate>> e1,
                        Map.Entry<String, List<MIRDuplicate>> e2) {
                        int compare = Double.compare(e1.getValue().get(0).score, e2.getValue().get(0).score);
                        return compare*-1; // reverse the list
                    }
                })
                */
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        //same result just without stream
       /* List<Map.Entry<String, List<MIRDuplicate>>> toSort = new ArrayList<>();
        for (Map.Entry<String, List<MIRDuplicate>> stringListEntry : duplicates.entrySet()) {
            toSort.add(stringListEntry);
        }
        toSort.sort(Comparator.comparingDouble(e -> e.getValue().get(0).score));
        LinkedHashMap<String, List<MIRDuplicate>> map = new LinkedHashMap<>();
        for (Map.Entry<String, List<MIRDuplicate>> stringListEntry : toSort) {
            map.putIfAbsent(stringListEntry.getKey(), stringListEntry.getValue());
        }
        Map<String, List<MIRDuplicate>> result = map;
        result.forEach((key, val) -> {
            System.out.println(key + " = "  + val.get(0).score);
        });*/
    }

    public String buildJson(Map<String, List<MIRDuplicateObjectStore.MIRDuplicate>> duplicates) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(duplicates);

        return json;
    }
}
