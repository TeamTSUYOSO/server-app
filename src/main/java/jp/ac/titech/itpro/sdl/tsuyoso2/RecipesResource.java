package jp.ac.titech.itpro.sdl.tsuyoso2;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.InetAddress;
import java.util.*;

@Path("recipes")
public class RecipesResource {

    /**
     * POST recipes/suggest - 自動提案
     * @param param パラメータ (JSON)
     * @return 結果 (JSON)
     */
    @Path("suggest")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<SuggestResult> suggest(SuggestParam param) throws Exception {
        Client client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        SearchResponse response = client.prepareSearch("tsuyoso").setTypes("recipe")
                .setSize(param.request_num)
                .setQuery(QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.randomFunction(new Random().nextInt())))
                .execute()
                .actionGet();

        SearchHit[] hits = response.getHits().getHits();

        ArrayList<SuggestResult> result = new ArrayList<>();

        for (SearchHit hit : hits) {
            Map<String, Object> source = hit.getSource();
            int id = (int) source.get("id");
            String name = (String) source.get("name");
            result.add(new SuggestResult(id, name));
        }

        return result;
        //return Arrays.asList(new SuggestResult(param.request_num, "hoge"));
    }
}

/**
 * POST recipes/suggest のパラメータ
 */
class SuggestParam {
    public int request_num;

    public SuggestParam() {
        this.request_num = 0;
    }

    public SuggestParam(int request_num) {
        this.request_num = request_num;
    }
}


/**
 * POST recipes/suggest の結果
 */
class SuggestResult {
    public int recipeId;
    public String name;

    public SuggestResult(int recipeId, String name) {
        this.recipeId = recipeId;
        this.name = name;
    }
}