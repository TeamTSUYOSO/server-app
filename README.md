# Server Application

Javaです

## Usage

```sh
$ mvn clean test
$ mvn exec:java
```

http://localhost:8080/api/ 以下からAPIにアクセスできます

## APIを実装する

例えば `GET recipe/:id` を実装することを考えます

`src/main/java/jp/ac/titech/itpro/sdl/tsuyoso2` 以下に `RecipeResource.java` を作ります

GETで `recipe/:id` なので次のようになります。

```java
@Path("recipe")
public class RecipeResource {

    // GET recipe/:id
    @GET
    @Path("{id}")
    public Recipe getRecipe(@PathParam("id") int id) {
        return getRecipeById(id);
    }

}
```


## 使っているライブラリなど

- [Grizzly](https://grizzly.java.net/)

HTTP 通信をいい感じにしてくれる、多分いじることはあまりない

- [Jersey](https://jersey.java.net/)

RESTful APIを簡単に書ける。JAX-RSという仕様があるらしい

とても簡単に見える

## 使うかもしれないライブラリとか技術

- [MOXy](https://jersey.java.net/documentation/2.16/media.html#json.moxy)

Jerseyに入ってる、リクエスト/レスポンスをJSONで行うための機構

上のコードで言えば`Recipe`クラスをJSONにできるように定める

- [Jackson](https://github.com/FasterXML/jackson)

上がヤバそうな時の代替手段
