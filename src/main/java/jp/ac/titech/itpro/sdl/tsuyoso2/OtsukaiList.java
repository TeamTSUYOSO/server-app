package jp.ac.titech.itpro.sdl.tsuyoso2;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

@Path("lists")
public class OtsukaiList {

	/**
	 * GET recipe/:id - レシピの詳細情報
	 *
	 * @param :id
	 *            レシピのID
	 * @return Json
	 */
	@Path("list")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<Lists> getOtsukaiList(IdLists param) throws Exception {
		Client client = TransportClient.builder().build()
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
		List<Lists> li = new ArrayList<Lists>();
		for (int id : param.getIds()) {
			SearchResponse response = client.prepareSearch("tsuyoso").setTypes("recipe")
					.setQuery(QueryBuilders.matchQuery("id", id)).execute().actionGet();
			SearchHit hit = response.getHits().getHits()[0];
			Map<String, Object> source = hit.getSource();
			int listid = (int) source.get("id");
			String name = (String) source.get("name");
			int serving_num=(int)source.get("serving_num");
			ArrayList<RecipeIngredient> ingredient = new ArrayList<RecipeIngredient>();
			ingredient = (ArrayList<RecipeIngredient>) source.get("ingredients");
			li.add(new Lists(listid, name, ingredient,serving_num));
		}
		return li;
	}
}

class IdLists {
	public int[] past_recipe_ids;

	public IdLists(int[] past_recipe_ids) {
		this.past_recipe_ids = past_recipe_ids;
	}
	public IdLists(){}
	public int[] getIds() {
		return this.past_recipe_ids;
	}
}

class Lists {
	public int recipeId;
	public String name;
	public int serving_num;
	public ArrayList<RecipeIngredient> ingredients;

	public Lists() {
	}

	public Lists(int recipeId, String name, ArrayList<RecipeIngredient> ingredients,int serving_num) {
		this.recipeId = recipeId;
		this.name = name;
		this.ingredients = ingredients;
		this.serving_num=serving_num;
	}
}