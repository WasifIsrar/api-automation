package endpoints;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.POSTokenManager;
import utilities.WebdashTokenManager;

import static io.restassured.RestAssured.given;

public class TenderEndpoints {
    public static Response createTender(String payload, String appName) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-app-name", appName)
                .header("x-id-token", WebdashTokenManager.getIdToken())
                .header("Authorization", WebdashTokenManager.getAccessToken())
                .body(payload)
                .when()
                .post(Routes.TenderRoutes.createTender);
        return response;
    }

    public static Response getActiveTenders(String appName) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-app-name", appName)
                .header("x-id-token", POSTokenManager.getIdToken())
                .header("Authorization", POSTokenManager.getAccessToken())
                .when()
                .get(Routes.TenderRoutes.getActiveTenders);
        return response;
    }

    public static Response getDashboardTenderList(String appName, int restaurantId) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-app-name", appName)
                .header("x-id-token", WebdashTokenManager.getIdToken())
                .header("Authorization", WebdashTokenManager.getAccessToken())
                .queryParam("restaurantId", restaurantId) // Corrected this line
                .when()
                .get(Routes.TenderRoutes.getTenderList);
        return response;
    }

}
