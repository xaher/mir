package org.mycore.mir.duplicate;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.mycore.datamodel.metadata.MCRMetadataManager;
import org.mycore.datamodel.metadata.MCRObject;
import org.mycore.datamodel.metadata.MCRObjectID;

/**
 * Created by sven on 23.06.17.
 */
@Path("moreLikeThis")
public class MIRMoreLikeThisQueryResource {

    // ...mir/rsc/moreLikeThis

    NamedList<SolrDocumentList> solrDomcumentList;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response moreLikeThis() {
        MIRMoreLikeThisQuery mlt = new MIRMoreLikeThisQuery();
        try {
            //mlt.saveResultAsJson(mlt.buildUpMoreLikeThisQuery());
            MIRDuplicateObjectStore mirDuplicateObjectStore = new MIRDuplicateObjectStore();
            //mlt.loadJsonResult()
            return Response.ok(mirDuplicateObjectStore.buildJson(
                mirDuplicateObjectStore.sortList(mlt.loadJsonResult()))).build();
        } catch (IOException e) {
            e.printStackTrace();
        }/* catch (SolrServerException e) {
            e.printStackTrace();
        }*/
        //solrDomcumentList = mlt.similarityList(mlt.buildUpMoreLikeThisQuery());
        return Response.serverError().build();
       // return Response.ok(duplicateSet.buildJSON().toString()).build();
    }

    /**
     *
     * @param id  the mycoreobject to be compared with
     * @param doubletOf mycoreobject that is a doublet of id
     * @return
     */

    @POST
    public Response setDoublet(@QueryParam("id") String id,
        @QueryParam("doubletOf") String doubletOf) {
        try {
            MCRObjectID mcrObjectID = MCRObjectID.getInstance(id);
            MCRObject mcrObject = MCRMetadataManager.retrieveMCRObject(mcrObjectID);
            mcrObject.getService().addFlag("doubletOf", doubletOf);
            MCRMetadataManager.update(mcrObject);
        } catch (Exception exc) {
            throw new WebApplicationException("Unable to set doublet of " + id + " to " + doubletOf, exc);
        }
        return Response.ok().build();
    }

}
