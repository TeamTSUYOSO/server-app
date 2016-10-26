package jp.ac.titech.itpro.sdl.tsuyoso2;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;

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
        
        FunctionScoreQueryBuilder scoreQueryBuilder = QueryBuilders.functionScoreQuery(ScoreFunctionBuilders.randomFunction(new Random().nextInt()));
        		                
        //過去に提案したレシピはスコアを下げる
	    for(int past_recipe_id : param.past_recipe_ids){
	    	scoreQueryBuilder.add(QueryBuilders.matchQuery("id", past_recipe_id), ScoreFunctionBuilders.weightFactorFunction(-10));
	    }
	    
	    //過去の評価を反映
	    for(Reputation reputation : param.reputations){
	    	//簡単に評価値*10をスコアにプラス
	    	scoreQueryBuilder.add(QueryBuilders.matchQuery("id", reputation.recipe_id), ScoreFunctionBuilders.weightFactorFunction(reputation.value * 10));
	    }
	                                             
        SearchResponse response = client.prepareSearch("tsuyoso").setTypes("recipe")
                .setSize(param.request_num)
                .setQuery(scoreQueryBuilder)
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
    public List<Integer> past_recipe_ids;
    public List<Reputation> reputations;


    public SuggestParam() {
        this.request_num = 0;
    }

    public SuggestParam(int request_num, List<Integer> past_recipe_ids, List<Reputation> reputations) {
        this.request_num = request_num;
        this.past_recipe_ids = past_recipe_ids;
        this.reputations = reputations;
    }
}

/**
 * レシピの評価
 */
class Reputation{
	public int recipe_id;
	public int value;
	public int proposed_time;
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