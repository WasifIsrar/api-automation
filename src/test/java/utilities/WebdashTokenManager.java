package utilities;

import endpoints.AuthenticationEndpoints;
import io.restassured.response.Response;
import payload.Authentication;

public class WebdashTokenManager {
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
	    	authenticationPayload.setUsername(ConfigManager.getProperty("username"));
	    	authenticationPayload.setPassword("Test@1234");
			Response response=AuthenticationEndpoints.webdashUserLogin(authenticationPayload);
			accessToken=response.jsonPath().getString("data.authChallengeResponse.AuthenticationResult.AccessToken");
	        idToken = response.jsonPath().getString("data.authChallengeResponse.AuthenticationResult.IdToken");
	 }
}
