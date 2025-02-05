package endpoints;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.POSTokenManager;
import utilities.WebdashTokenManager;

import static io.restassured.RestAssured.given;

public class CashDrawerEndpoints {
    public static Response createCashDrawer(String payload, String appName) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-app-name", appName)
                .header("x-id-token", WebdashTokenManager.getIdToken())
                .header("Authorization", WebdashTokenManager.getAccessToken())
                .body(payload)
                .when()
                .post(Routes.CashDrawerRoutes.createCashDrawer);
        return response;
    }

    public static Response getAllCashDrawers(String appName) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-app-name", appName)
                .header("x-id-token", POSTokenManager.getIdToken())
                .header("Authorization", POSTokenManager.getAccessToken())
                .get(Routes.CashDrawerRoutes.getAllCashDrawer);
        return response;
    }
}
