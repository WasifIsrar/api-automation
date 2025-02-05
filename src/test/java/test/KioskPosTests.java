package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;
import endpoints.CartEndpoints;
import endpoints.PaymentEndpoints;
import endpoints.RestaurantEndpoints;
import endpoints.Routes;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import utilities.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KioskPosTests extends Routes {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonNode = null;
    Faker faker=new Faker();
    Response response;
    JsonPath jsonPath;
    String updatedPayload = null;


    @Test
    public void verifyKioskOrderOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+ File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"kioskOnboardPayload.json")));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        response = RestaurantEndpoints.onboardKiosk(updatedPayload);
        System.out.println("Onboard Response: ");
        response.then().log().all();
        jsonPath = response.jsonPath();
        String jwtToken = jsonPath.getString("data.access_token");

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+ File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"createKioskCartPayload.json")));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        response = CartEndpoints.createKioskCart(updatedPayload, jwtToken);
        System.out.println("Create KIOSK Cart Response: ");
        response.then().log().all();
        jsonPath = response.jsonPath();
        int id = jsonPath.getInt("data.id");

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+ File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"kioskCalculationPayload.json")));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        jsonNode.put("restaurantId", restaurantId);

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        response = PaymentEndpoints.calculateKioskPayment(updatedPayload, id, jwtToken);
        System.out.println("KIOSK Payment Response: ");
        response.then().log().all();
    }
}
