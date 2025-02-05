package endpoints;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payload.Authentication;
import utilities.MomTokenManager;
import utilities.MposTokenManager;
import utilities.WebdashTokenManager;

import static io.restassured.RestAssured.given;

import com.fasterxml.jackson.databind.node.ObjectNode;


public class AuthenticationEndpoints {
	
	public static Response loginWithPos(Authentication payload)
	{
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.queryParam("deviceId",payload.getDeviceId())
			.queryParam("logoutNeeded",payload.getLogoutNeeded())
			.body(payload)
		.when()
			.post(Routes.AuthenticationRoutes.loginPosUrl);
		return response;
	}
	
	public static Response loginWithmPos(Authentication payload)
	{
		System.out.println("Authentication Payload: "+payload);
		Response response=given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.queryParam("deviceId",payload.getDeviceId())
			.queryParam("logoutNeeded",payload.getLogoutNeeded())
			.body(payload)
		.when()
			.post(Routes.AuthenticationRoutes.loginmPosUrl);
		return response;
	}
	
	public static Response webdashUserLogin(Authentication payload) {
		Response response=given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(payload)
			.when()
				.post(Routes.AuthenticationRoutes.userLoginUrl);
			return response;
	}
	
	public static Response momUserLogin(Authentication payload) {
		Response response=given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.header("x-app-name","mom")
				.body(payload)
			.when()
				.post(Routes.AuthenticationRoutes.userLoginUrl);
		System.out.println("MOM LOGIN RESPONSE");
		response.then().log().all();
		return response;
	}

	public static Response passwordlessLogin(String payload) {
		Response response = given()
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	        .body(payload)
	    .when()
	        .post(Routes.AuthenticationRoutes.passwordlessLoginUrl);
	    return response;
	}

	public static Response passwordlessVerify(String payload) {
	    Response response = given()
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	        .body(payload)
	    .when()
	        .post(Routes.AuthenticationRoutes.passwordlessVerifyUrl);
	    return response;
	}
	
	public static Response setUserPassword(String payload,String accessToken,String idToken) {
	    Response response = given()
	        .contentType(ContentType.JSON)
	        .accept(ContentType.JSON)
	        .header("Authorization","Bearer "+ accessToken)
            .header("x-id-token", idToken)
	        .body(payload)
	    .when()
	        .post(Routes.AuthenticationRoutes.setUserPasswordUrl);
	    return response;
	}

}
