package endpoints;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.WebdashTokenManager;

import static io.restassured.RestAssured.given;

public class DeviceEndpoints {
    public static Response createDevice(String payload, String appName) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-app-name", appName)
                .header("x-id-token", WebdashTokenManager.getIdToken())
                .header("Authorization", WebdashTokenManager.getAccessToken())
                .body(payload)
                .when()
                .post(Routes.DeviceRoutes.createDeviceUrl);
        return response;
    }

    public static Response getDeviceList(String appName, int restaurantId) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("restaurantId", restaurantId)
                .header("x-app-name", appName)
                .header("x-id-token", WebdashTokenManager.getIdToken())
                .header("Authorization", WebdashTokenManager.getAccessToken())
                .when()
                .get(Routes.DeviceRoutes.getDeviceListUrl);
        return response;
    }
}
