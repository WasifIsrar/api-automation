package endpoints;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.WebdashTokenManager;

public class ItemEndpoints {
	public static Response create(String payload)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",WebdashTokenManager.getAccessToken()).and().header("x-id-token",WebdashTokenManager.getIdToken())
			.and().header("X-App-Name","dashboard")
			.body(payload)
		.when()
			.post(Routes.ItemRoutes.createItemUrl);
		return response;
	}

	public static Response getItemsList(String appName, int restaurantId)
	{
		Response response = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", WebdashTokenManager.getAccessToken())
				.header("x-id-token", WebdashTokenManager.getIdToken())
				.header("x-app-name", appName)
				.queryParam("restaurantId", restaurantId)
				.when()
				.get(Routes.ItemRoutes.getItemsUrl);
		return response;
	}
}
