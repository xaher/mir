package org.mycore.mir.duplicate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.mycore.solr.MCRSolrClientFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MIRMoreLikeThisQuery {

    NamedList<SolrDocumentList> solrDocument;

    /*
     * build's the solr query
     */

    public SolrQuery buildUpMoreLikeThisQuery(/*String id, List<String> similarityFields, Integer rows*/) {
        SolrQuery query = new SolrQuery();
        query.setMoreLikeThis(true);
        query.setMoreLikeThisCount(5);
        query.setParam("indent", "true");
        query.setParam("wt", "json");
        query.setParam("fl", "mods.title, number, dateIssued, mods.identifier, id, score");
        query.setMoreLikeThisFields("mods.title", "mods.identifier", "number", "dateIssued");
        /*query.set("defType", "edismax");
        query.set("qf", "mods.identifier^2");*/
        query.setMoreLikeThisMinDocFreq(1);
        query.setMoreLikeThisMinTermFreq(1);
        //query.setFilterQueries("-doubletOf:*");
        query.setQuery("objectType:*");
        query.setRows(8900/*rows*/);
        return query;
    }

    /*
     * @return return a  namedlist of solr documents
     */

    public NamedList<SolrDocumentList> similarityList(SolrQuery solrQuery) {
        QueryResponse response;

        try {
            response = MCRSolrClientFactory.getSolrClient().query(solrQuery);
            solrDocument = response.getMoreLikeThis();

        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return solrDocument;
    }

    public void saveResultAsJson(SolrQuery solrQuery) throws IOException, SolrServerException {
        QueryResponse queryResponse = MCRSolrClientFactory.getSolrClient().query(solrQuery);
        MIRDuplicateObjectStore store = new MIRDuplicateObjectStore(queryResponse.getResults(),
            queryResponse.getMoreLikeThis());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(store);

        FileOutputStream fos = new FileOutputStream("/home/sven/Dokumente/MoreLikeThis/JsonTest.xml");
        fos.write(json.getBytes(Charset.forName("UTF-8")));

    }

    public Map<String, List<MIRDuplicateObjectStore.MIRDuplicate>> loadJsonResult() throws IOException{
        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(
            "/home/sven/Dokumente/MoreLikeThis/JsonTest.xml")); // ToDo: Replace with Property
        MIRDuplicateObjectStore jsonResultList = gson.fromJson(br, MIRDuplicateObjectStore.class);
        Map<String, List<MIRDuplicateObjectStore.MIRDuplicate>> duplicates = jsonResultList.duplicates;
    /*    for (Map.Entry<String, List<MIRDuplicateObjectStore.MIRDuplicate>> elements : duplicates.entrySet()) {
            System.out.println(elements.getKey());
            for (MIRDuplicateObjectStore.MIRDuplicate duplicate: elements.getValue()) {
                System.out.println(duplicate.objectID + "\n" + duplicate.score);
            }
        }*/
        return duplicates;
    }

}
