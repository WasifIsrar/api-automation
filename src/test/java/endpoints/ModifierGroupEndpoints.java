package endpoints;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.WebdashTokenManager;

public class ModifierGroupEndpoints {
	public static Response create(String payload)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",WebdashTokenManager.getAccessToken()).and().header("x-id-token",WebdashTokenManager.getIdToken())
			.and().header("X-App-Name","dashboard")
			.body(payload)
		.when()
			.post(Routes.ModifierGroupRoutes.createModifierGroupUrl);
		return response;
	}
	
	public static Response get(int restaurantId)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",WebdashTokenManager.getAccessToken()).and().header("x-id-token",WebdashTokenManager.getIdToken())
			.and().header("x-app-name","dashboard")
			.and().queryParam("restaurantId", restaurantId)
		.when()
			.get(Routes.ModifierGroupRoutes.getModifierGroupUrl);
		return response;
	}

}
