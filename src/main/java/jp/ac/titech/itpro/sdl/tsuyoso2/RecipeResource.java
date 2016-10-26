package jp.ac.titech.itpro.sdl.tsuyoso2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.net.InetAddress;
import java.util.ArrayList;

@Path("recipe")
public class RecipeResource {

    /**
     * GET recipe/:id - レシピの詳細情報
     * @param :id レシピのID
     * @return 詳細情報
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public RecipeInfo getRecipeInfo(@PathParam("id") int id) throws Exception {
        Client client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

        SearchResponse response = client.prepareSearch("tsuyoso").setTypes("recipe")
                .setQuery(QueryBuilders.matchQuery("id", id))
                .execute()
                .actionGet();

        SearchHit hit = response.getHits().getHits()[0];

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(hit.getSourceAsString(), RecipeInfo.class);
    }
}

class RecipeInfo {
    @JsonProperty("id") public int recipe_id;
    public String name;
    public int serving_num;
    public String ingredients_genre;
    public String process;
    public String foods_genre;
    public int takes_time;
    public String kitchenware;
    public int calorie;
    public int price;
    public ArrayList<RecipeIngredient> ingredients;
    public ArrayList<RecipeInstruction> instructions;

    public RecipeInfo() {}

    public RecipeInfo(int recipe_id, String name, int serving_num, String ingredients_genre, String process, String foods_genre, int takes_time, String kitchenware, int calorie, int price, ArrayList<RecipeIngredient> ingredients, ArrayList<RecipeInstruction> instructions) {
        this.recipe_id = recipe_id;
        this.name = name;
        this.serving_num = serving_num;
        this.ingredients_genre = ingredients_genre;
        this.process = process;
        this.foods_genre = foods_genre;
        this.takes_time = takes_time;
        this.kitchenware = kitchenware;
        this.calorie = calorie;
        this.price = price;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }
}

class RecipeIngredient {
    public String name;
    public String quantity;

    public RecipeIngredient() {}

    public RecipeIngredient(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}

class RecipeInstruction {
    public String content;
    public String order;

    public RecipeInstruction() {}

    public RecipeInstruction(String content, String order) {
        this.content = content;
        this.order = order;
    }
}
