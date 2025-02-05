package endpoints;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CartEndpoints {
       public static Response createKioskCart(String payload, String jwtToken) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-jwt-token", jwtToken)
                .header("x-app-name", "kiosk")
                .body(payload)
                .when()
                .post(Routes.CartRoutes.createKioskCartUrl);
        return response;
    }
}
