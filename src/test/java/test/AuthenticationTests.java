package test;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.json.JSONObject;
import org.testng.Assert;

import endpoints.AuthenticationEndpoints;
import endpoints.Routes;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payload.Authentication;
import static io.restassured.RestAssured.given;


public class AuthenticationTests{
	
	@Parameters({"restaurantId","posPin","deviceId"})
	@Test
	public void verifyUserLogin(int restaurantId,int posPin,String deviceId) {
		Authentication authenticationPayload=new Authentication();
		authenticationPayload.setRestaurantId(restaurantId);
		authenticationPayload.setPosPin(posPin);
		authenticationPayload.setDeviceId(deviceId);
		authenticationPayload.setLogoutNeeded(true);
		Response response=AuthenticationEndpoints.webdashUserLogin(authenticationPayload);
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
	}	
  }

