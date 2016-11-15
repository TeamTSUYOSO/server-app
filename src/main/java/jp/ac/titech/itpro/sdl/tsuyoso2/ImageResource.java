package jp.ac.titech.itpro.sdl.tsuyoso2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("image")
public class ImageResource {

    /**
     * GET image/:id - レシピの画像
     * @param :id レシピのID
     * @return 画像
     */
	@GET
	@Path("{id}")
    @Produces("image/jpeg")
	public Response getImage(@PathParam("id") int id) {
		//TODO::DBから画像取得に変更
		String dir = "~/server-app/image/";
		String absolutePath = dir + id + ".jpg";
		System.out.println(absolutePath);
		try {
            return Response.ok(new FileInputStream(absolutePath)).build();
        } catch (FileNotFoundException e) {
        	System.out.println("notfound");
            return Response.status(Status.NOT_FOUND).build();
        }
    }
}

