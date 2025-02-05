package utilities;

import endpoints.AuthenticationEndpoints;
import io.restassured.response.Response;
import payload.Authentication;

public class MomTokenManager {
	 private static String accessToken;
	 private static String idToken;

	 public static String getAccessToken() {
	        if (accessToken == null) {
	            authenticate();
	        }
	        System.out.println("MOM ACCESS TOKEN: "+accessToken);
	        return "Bearer "+accessToken;
	    }

	 public static String getIdToken() {
	        if (idToken == null) {
	            authenticate();
	        }
	        System.out.println("MOM ID TOKEN: "+idToken);

	        return idToken;
	    }

	 private static void authenticate() {
	    	Authentication authenticationPayload=new Authentication();
	    	authenticationPayload.setUsername(ConfigManager.getProperty("username"));
	    	authenticationPayload.setPassword("Test@1234");
			Response response=AuthenticationEndpoints.momUserLogin(authenticationPayload);
			accessToken=response.jsonPath().getString("data.authChallengeResponse.AuthenticationResult.AccessToken");
	        idToken = response.jsonPath().getString("data.authChallengeResponse.AuthenticationResult.IdToken");
	 }
}
