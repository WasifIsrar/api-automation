package endpoints;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payload.Table;
import utilities.MposTokenManager;
import utilities.POSTokenManager;

public class TableEndpoints {
	
	public static Response allTables(Table payload)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",MposTokenManager.getAccessToken()).and().header("x-id-token",MposTokenManager.getIdToken())
			.queryParam("available",payload.isAvailable())
		.when()
			.get(Routes.TableRoutes.allTablesUrl);
		return response;
	}
}
