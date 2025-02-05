package endpoints;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class MiscEndpoints {
	
	public static Response clearCache(int restaurantId,String cacheType)
	{
		Response response=given()
			.contentType(ContentType.JSON)
		    .accept(ContentType.JSON)
			.queryParam("restaurantId", restaurantId).and()
			.queryParam("cacheType", cacheType)
		.when()
			.post(Routes.MiscRoutes.clearCacheUrl);
		return response;
	}

}
