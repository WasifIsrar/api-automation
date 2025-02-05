package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;

import endpoints.AuthenticationEndpoints;
import endpoints.DeviceEndpoints;
import endpoints.EmployeeEndpoints;
import endpoints.RestaurantEndpoints;
import endpoints.Routes;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import payload.Authentication;
import utilities.ConfigManager;

public class SuperAdminTests extends Routes{
	ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonNode = null;
    Faker faker = new Faker();
    Response response;
    JsonPath jsonPath;
    String updatedPayload = null;
	
    @Test
    public void createDeviceAndVerifyOnboarded() {
    	int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
    	int businessId = Integer.parseInt(ConfigManager.getProperty("businessId"));

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "createDevicePayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String deviceName = "POS" + faker.bothify("####??");
        System.out.println("Device Name: " + deviceName);
        String deviceSerialNumber = faker.bothify("############????");
        System.out.println("Device Serial Number: " + deviceSerialNumber);

        jsonNode.put("businessId", businessId);
        jsonNode.put("restaurantId", restaurantId);

        ObjectNode deviceNode = (ObjectNode) jsonNode.get("device");
        deviceNode.put("deviceName", deviceName);
        deviceNode.put("deviceSerialNumber", deviceSerialNumber);

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        System.out.println("Create Device Response: ");
        response = DeviceEndpoints.createDevice(updatedPayload, "dashboard");
        response.then().log().all();

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "posOnboardPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        jsonNode.put("identificationNumber", deviceSerialNumber);
        jsonNode.put("name", deviceName);

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        System.out.println("POS Onboard Response: ");
        response = RestaurantEndpoints.onboardPos(updatedPayload);
        response.then().log().all();

        jsonPath = response.jsonPath();

        String message = jsonPath.getString("message");
        Assert.assertEquals(message, "POS already exists", "POS does not exists");
    }
    
    @Test
    public void verifyCreateEmployeeWithSuperAdminAndLoginOnPos() {
    	int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        int businessId = Integer.parseInt(ConfigManager.getProperty("businessId"));
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") +
                "/src/test/java/payload/createEmployeePayload.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String phoneNumber = "+1212" + faker.number().digits(7);

        ObjectNode employeeNode = (ObjectNode) jsonNode.get("employee");
        employeeNode.put("firstName", firstName);
        employeeNode.put("lastName", lastName);
        employeeNode.put("email", email);
        employeeNode.put("phoneNumber", phoneNumber);

        jsonNode.put("restaurantId", restaurantId);
        jsonNode.put("businessId", businessId);

        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // Create Employee
        System.out.println("------Response of Created Employee-------");
        response = EmployeeEndpoints.createEmployee(updatedPayload, "dashboard");
        response.then().log().all();
        jsonPath = response.jsonPath();
        Assert.assertEquals(response.statusCode(), 201, "Employee creation failed!");

        //checking employeeid and POS pin
        String employeeId = jsonPath.getString("response.data.creationStatus.UserSub");
        response = EmployeeEndpoints.getEmployeeList("dashboard", restaurantId);
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<Map<String, ?>> employees = jsonPath.getList("response.data.employees");
        Integer posPin = null;
        String username = null;
        for (Map<String, ?> employee : employees)
        {
            if (employee.get("id").equals(employeeId))
            {
                posPin = (Integer) employee.get("posPin");
                username=(String) employee.get("email");
                break;
            }
        }
        if (posPin == null)
        {
            System.out.println("Pos Pin is not available for the employee.");
            return;
        }
        System.out.println("Employee Pin: " + posPin);

        //passwordless login
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") +
                "/src/test/java/payload/passwordlessLoginPayload.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonNode.put("username", username);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("------Response of Passwordless Login-------");
        response = AuthenticationEndpoints.passwordlessLogin(updatedPayload);
        response.then().log().all();
        jsonPath = response.jsonPath();


      //passwordless verify
        String session = jsonPath.getString("data.Session");
        System.out.println("session: " + session);
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") +
                "/src/test/java/payload/passwordlessVerifyPayload.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonNode.put("username", username);
        jsonNode.put("session", session);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("------Response of Passwordless Verify-------");
        response = AuthenticationEndpoints.passwordlessVerify(updatedPayload);
        response.then().log().all();
        jsonPath = response.jsonPath();
        String accessToken=jsonPath.getString("data.authChallengeResponse.AuthenticationResult.AccessToken");
        String idToken=jsonPath.getString("data.authChallengeResponse.AuthenticationResult.IdToken");

        //set User Password
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") +
                "/src/test/java/payload/setUserPasswordPayload.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonNode.put("userName", username);
        jsonNode.put("password", "Test@1234");
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("------Response of Set User Password-------");
        response = AuthenticationEndpoints.setUserPassword(updatedPayload,accessToken,idToken);
        response.then().log().all();
        jsonPath = response.jsonPath();

        //login with POS
        Authentication authenticationPayload = new Authentication();
        authenticationPayload.setRestaurantId(Integer.parseInt(ConfigManager.getProperty("restaurantId")));
        authenticationPayload.setPosPin(posPin);
        authenticationPayload.setDeviceId(ConfigManager.getProperty("deviceId"));
        authenticationPayload.setLogoutNeeded(true);
        authenticationPayload.setUsername(username);
        authenticationPayload.setPassword("Test@1234");
        Response loginResponse = AuthenticationEndpoints.loginWithPos(authenticationPayload);
        loginResponse.then().log().all();
        jsonPath = loginResponse.jsonPath();
        String message = jsonPath.getString("message");
        Assert.assertEquals(message, "login with pos pin successful", "User not logged in");
    }
}
