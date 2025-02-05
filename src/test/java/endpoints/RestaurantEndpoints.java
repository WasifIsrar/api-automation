package endpoints;

import io.restassured.response.Response;
import io.restassured.http.ContentType;
import utilities.WebdashTokenManager;

import static io.restassured.RestAssured.given;

public class RestaurantEndpoints {
    public static Response onboardKiosk(String payload) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post(Routes.RestaurantRoutes.kioskOnboardUrl);
        return response;
    }

    public static Response getAllPos(int restaurantID) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-id-token", WebdashTokenManager.getIdToken())
                .header("Authorization", "Bearer " + WebdashTokenManager.getAccessToken())
                .queryParam("restaurantId", restaurantID)
                .get(Routes.RestaurantRoutes.getAllPos);
        return response;
    }

    public static Response onboardPos(String payload) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post(Routes.RestaurantRoutes.PosOnboardUrl);
        return response;
    }
}
