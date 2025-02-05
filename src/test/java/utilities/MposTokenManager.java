package utilities;

import endpoints.AuthenticationEndpoints;
import io.restassured.response.Response;
import payload.Authentication;

public class MposTokenManager {
	 private static String accessToken;
	 private static String idToken;

	 public static String getAccessToken() {
	        if (accessToken == null) {
	            authenticate();
	        }
	        return "Bearer "+accessToken;
	    }

	 public static String getIdToken() {
	        if (idToken == null) {
	            authenticate();
	        }
	        return idToken;
	    }

	 private static void authenticate() {
	    	Authentication authenticationPayload=new Authentication();
			authenticationPayload.setRestaurantId(Integer.parseInt(ConfigManager.getProperty("restaurantId")));
			authenticationPayload.setPosPin(Integer.parseInt(ConfigManager.getProperty("pin")));
			authenticationPayload.setDeviceId(ConfigManager.getProperty("deviceId"));
			authenticationPayload.setLogoutNeeded(true);
			Response response=AuthenticationEndpoints.loginWithmPos(authenticationPayload);
			System.out.println("Authenticate Response: ");
			response.then().log().all();
			accessToken=response.jsonPath().getString("data.authChallengeResponse.AuthenticationResult.AccessToken");
	        idToken = response.jsonPath().getString("data.authChallengeResponse.AuthenticationResult.IdToken");
	    }
}
