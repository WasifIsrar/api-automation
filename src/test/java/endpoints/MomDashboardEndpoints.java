package endpoints;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.MomTokenManager;

public class MomDashboardEndpoints {
	
	public static Response getReports()
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",MomTokenManager.getAccessToken()).and().header("x-id-token",MomTokenManager.getIdToken())
		.when()
			.get(Routes.MomDashboardRoutes.getReportsUrl);
		return response;
	}

}
