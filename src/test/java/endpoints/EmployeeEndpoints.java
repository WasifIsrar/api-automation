package endpoints;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import utilities.WebdashTokenManager;

import static io.restassured.RestAssured.given;

public class EmployeeEndpoints {
    public static Response createEmployee(String payload, String appName) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-app-name", appName)
                .header("Authorization", WebdashTokenManager.getAccessToken())
                .header("x-id-token", WebdashTokenManager.getIdToken())
                .body(payload)
                .when()
                .post(Routes.EmployeeRoutes.createEmployee);
        return response;
    }
    
    public static Response createEmployeeWithPermission(String payload, String appName) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-app-name", appName)
                .header("Authorization", WebdashTokenManager.getAccessToken())
                .header("x-id-token", WebdashTokenManager.getIdToken())
                .body(payload)
                .when()
                .post(Routes.EmployeeRoutes.createEmployeeWithPermissionUrl);
        return response;
    }

    public static Response getEmployeeList(String appName, int restaurantId) {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-app-name", appName)
                .header("Authorization", WebdashTokenManager.getAccessToken())
                .header("x-id-token", WebdashTokenManager.getIdToken())
                .queryParam("restaurantId", restaurantId)
                .when()
                .get(Routes.EmployeeRoutes.getEmployeeListUrl);
        return response;
    }
}