package endpoints;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payload.Ticket;
import utilities.MposTokenManager;

public class TicketEndpoints {
	public static Response createId(Ticket payload)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",MposTokenManager.getAccessToken()).and().header("x-id-token",MposTokenManager.getIdToken())
			.body(payload)
		.when()
			.post(Routes.TicketRoutes.createIdUrl);
		return response;
	}

	public static Response createId(String payload)
	{
		Response response = given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("Authorization", MposTokenManager.getAccessToken())
				.header("x-id-token", MposTokenManager.getIdToken())
				.body(payload)
				.when()
				.post(Routes.TicketRoutes.createIdUrl);
		return response;
	}
	
	public static Response update(String payload,String appName,int ticketId)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",MposTokenManager.getAccessToken()).and().header("x-id-token",MposTokenManager.getIdToken())
			.and().header("x-app-name",appName)
			.pathParam("ticketId",ticketId)
			.body(payload)
		.when()
			.patch(Routes.TicketRoutes.updateUrl);
		return response;
	}
	
	public static Response updateStatus(String payload,int ticketId,String appName)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",MposTokenManager.getAccessToken()).and().header("x-id-token",MposTokenManager.getIdToken())
			.header("x-app-name",appName)
			.pathParam("ticketId",ticketId)
			.body(payload)
		.when()
			.patch(Routes.TicketRoutes.updateStatusUrl);
		System.out.println("UPDATE STATUS URL:"+Routes.TicketRoutes.updateStatusUrl);
		return response;
	}
	
	public static Response getAllOrders(Ticket payload)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",MposTokenManager.getAccessToken()).and().header("x-id-token",MposTokenManager.getIdToken())
			.queryParam("orderStatus", payload.getOrderStatus())
			.queryParam("orderType", payload.getOrdertype())
			.queryParam("page", "")
			.queryParam("perPage", "")
		.when()
			.get(Routes.TicketRoutes.getAllOrdersUrl);
		return response;
	}
	
	public static Response get(int ticketId)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",MposTokenManager.getAccessToken()).and().header("x-id-token",MposTokenManager.getIdToken())
			.header("x-app-name","pos")
			.pathParam("ticketId", ticketId)
			.when()
			.get(Routes.TicketRoutes.getUrl);
		return response;
	}
	
	public static Response voidOrder(int ticketId,String appName)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.header("Authorization",MposTokenManager.getAccessToken()).and().header("x-id-token",MposTokenManager.getIdToken())
			.header("x-app-name",appName)
			.queryParam("orderId", ticketId)
			.when()
			.patch(Routes.TicketRoutes.voidOrderUrl);
		return response;
	}
}
