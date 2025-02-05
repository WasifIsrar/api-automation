package endpoints;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payload.TimeLog;
import utilities.POSTokenManager;
import static io.restassured.RestAssured.given;


public class TimelogEndpoints {
		public static Response clockin(TimeLog payload) {
			System.out.println("Access Token: "+POSTokenManager.getAccessToken());
			System.out.println("Id Token: "+POSTokenManager.getIdToken());
			Response response=given()
			.header("Authorization",POSTokenManager.getAccessToken()).and().header("x-id-token",POSTokenManager.getIdToken())
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(payload)
		.when()
			.post(Routes.TimelogRoutes.clockinUrl);
			return response;
		}
}
