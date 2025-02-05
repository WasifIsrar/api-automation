package endpoints;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.MomTokenManager;
import utilities.POSTokenManager;

import static io.restassured.RestAssured.given;

public class PaymentEndpoints {

	public static Response createCalculation(String payload,String appName,int ticketId)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",POSTokenManager.getAccessToken()).and().header("x-id-token",POSTokenManager.getIdToken())
			.and().header("X-App-Name",appName)
			.pathParam("ticketId", ticketId)
			.body(payload)
		.when()
			.post(Routes.PaymentRoutes.createCalculationUrl);
		return response;
	}

	public static Response calculateKioskPayment(String payload, int ticketId, String jwtToken)
	{
		Response response=given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("x-jwt-token", jwtToken)
				.and().header("X-App-Name","kiosk")
				.pathParam("ticketId", ticketId)
				.body(payload)
				.when()
				.post(Routes.PaymentRoutes.createCalculationUrl);
		return response;
	}


	public static Response getTicket(int ticketId)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization", MomTokenManager.getAccessToken()).and().header("x-id-token",MomTokenManager.getIdToken())
			.pathParam("ticketId", ticketId)
		.when()
			.get(Routes.PaymentRoutes.getTicketUrl);
		return response;
	}

	public static Response createPayment(String payload,String appName,int ticketId)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization", POSTokenManager.getAccessToken()).and().header("x-id-token",POSTokenManager.getIdToken())
			.and().header("X-App-Name",appName)
			.pathParam("ticketId", ticketId)
			.body(payload)
		.when()
			.post(Routes.PaymentRoutes.createPaymentUrl);
		return response;
	}
}
