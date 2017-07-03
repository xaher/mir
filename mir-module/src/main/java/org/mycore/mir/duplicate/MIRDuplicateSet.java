package org.mycore.mir.duplicate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.jdom2.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by sven on 20.06.17.
 */
public class MIRDuplicateSet {

    private List<String> ids;

    private Map<String, List<MIRPossibleDuplicate>> idDuplicateMap = new ConcurrentHashMap<>();

    private Map<String, MIRPossibleDuplicate> maxScoreMap = new ConcurrentHashMap<>();


    public MIRDuplicateSet(NamedList<SolrDocumentList> documentList) {
        for (Map.Entry<String, SolrDocumentList> element : documentList) {
            SolrDocumentList docList = element.getValue();
            String id = element.getKey();
            docList.forEach(doc -> {
                MIRPossibleDuplicate duplicate = new MIRPossibleDuplicate();
                duplicate.setId((String) doc.getFieldValue("id"));
                duplicate.setScore(Double.parseDouble(doc.getFieldValue("score").toString()));
                addPossibleDuplicate(id, duplicate);

            });

        }
    }

    /**
     *
     * @param id a certain mycorobject
     * @param duplicate the associated duplicates
     */

    public void addPossibleDuplicate(String id, MIRPossibleDuplicate duplicate) {
        List<MIRPossibleDuplicate> mirPossibleDuplicates = idDuplicateMap
            .computeIfAbsent(id, (a) -> new ArrayList<>());

        mirPossibleDuplicates.add(duplicate);
    }

    /**
     * takes the list an rearranged the entries in such a manner that the highest possible score cam's first
     */

    public void sortList() {
        Comparator<MIRPossibleDuplicate> scoreComperator = Comparator.comparing(MIRPossibleDuplicate::getScore);
            idDuplicateMap.entrySet().forEach((entry) -> {
                Optional<MIRPossibleDuplicate> maxScore = entry.getValue().stream().max(scoreComperator);
                maxScoreMap.put(entry.getKey(), maxScore.get());
            });

            Comparator<Map.Entry<String, List<MIRPossibleDuplicate>>> comparing = Comparator
                .comparing(duplicatePair -> maxScoreMap.get(duplicatePair.getKey()).getScore()); // set the entries in numerical order
            Predicate<Map.Entry<String, List<MIRPossibleDuplicate>>> filteredByScore = value ->
                (maxScoreMap.get(value.getKey()).getScore() > 4.00); // if you only want to see scores lower or higher than a certain value
            ids = idDuplicateMap
                .entrySet()
                .stream()
                //.sorted(comparing.reversed())
                //.filter(filteredByScore)
                //.limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

    }

    /**
     *
     * @return return the object and there possible duplicates as Element
     */

    public Element buildElement () {
        Element root = new Element("similarityList");
        for (String id : ids) {
            List<MIRPossibleDuplicate> duplicateSet = this.idDuplicateMap.get(id);
            Element currentObject = new Element("object");
            currentObject.setAttribute("id", id);
            root.addContent( currentObject);
            for (MIRPossibleDuplicate duplicate : duplicateSet) {
                Element similarityObject = new Element("similarityObject");
                currentObject.addContent(similarityObject);
                similarityObject.setAttribute("id", duplicate.getId());
                similarityObject.setAttribute("score", duplicate.getScore().toString());
            }
        }
        return root;
    }

    /**
     *
     * @return return the object and there possible duplicates as JsonArray
     */

    public JsonArray buildJSON() {
        JsonArray array = new JsonArray();
        for (String id : ids) {
            JsonObject entry = new JsonObject();
            List<MIRPossibleDuplicate> duplicateSet = this.idDuplicateMap.get(id);
            entry.addProperty("comparativeID", id);
            JsonArray jsonArray = new JsonArray();
            entry.add("similarityList", jsonArray);
            for (MIRPossibleDuplicate duplicate : duplicateSet) {
                JsonObject jsonObject = new JsonObject();
                jsonArray.add(jsonObject);
                jsonObject.addProperty("id", duplicate.getId());
                jsonObject.addProperty("score", duplicate.getScore().toString());
            }
            array.add(entry);
        }
        return array;
    }

    /**
     *
     * @return return the List of possible duplicates as string
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        for (String id : ids) {
            List<MIRPossibleDuplicate> duplicateSet = this.idDuplicateMap.get(id);
            stringBuilder.append("Object: " + id + System.lineSeparator());
            for (MIRPossibleDuplicate duplicate : duplicateSet) {
                stringBuilder.append(duplicate.toString() + System.lineSeparator());
            }
        }
        return stringBuilder.toString();
    }
}
