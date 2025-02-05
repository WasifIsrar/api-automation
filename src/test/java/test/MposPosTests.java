package test;

import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import endpoints.ItemEndpoints;
import endpoints.Routes;
import endpoints.TableEndpoints;
import endpoints.TicketEndpoints;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import payload.Table;
import payload.Ticket;
import utilities.ConfigManager;


public class MposPosTests extends Routes{
	
	@Test
	public void verifyMposTicketOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
		Table tablePayload=new Table();
		tablePayload.setAvailable(true);
		Response response=TableEndpoints.allTables(tablePayload);
		System.out.println("---Response Of Get All Tables-----");
        response.then().log().all();
		JsonPath jsonPath = response.jsonPath();
        int tableId = jsonPath.getInt("data[0].id");
        Ticket ticketPayload=new Ticket();
        ticketPayload.setCustomItems(new ArrayList<>());
        ticketPayload.setEmployeeId(ConfigManager.getProperty("employeeId"));
        ticketPayload.setOrderType("dine-in");
        ticketPayload.setNoOfGuests(5);
        ticketPayload.setNote("");
        ticketPayload.setOrderSource("mpos");
        ticketPayload.setServeType("fullServe");
        ticketPayload.setStatus("pending");
        ticketPayload.setTableNumber(tableId);
        ticketPayload.setTaxExemption(false);
        ticketPayload.setTicketItems(new ArrayList<>());
        ticketPayload.setTicketPaymentStatus("unpaid");
        response= TicketEndpoints.createId(ticketPayload);
		System.out.println("---Response Of Create ID-----");
        response.then().log().all();
        jsonPath = response.jsonPath();
        int ticketId=jsonPath.getInt("data[0].id");
        response=ItemEndpoints.getItemsList("dashboard", restaurantId);
		System.out.println("---Response Of Get Items-----");
		response.then().log().all();
		jsonPath=response.jsonPath();
		int itemId=jsonPath.getInt("response.data.items[0].id");
		String itemName=jsonPath.getString("response.data.items[0].itemName");
		double itemPrice=jsonPath.getDouble("response.data.items[0].itemPrice");
		String categoryId=null;
		if(!jsonPath.getList("response.data.items[0].categoryIds").isEmpty()) {
			categoryId=jsonPath.getString("response.data.items[0].categoryIds[0]");
		}
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonNode = null;
        try {
			jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"updatePayload.json")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        jsonNode.put("tableId", tableId);
        jsonNode.put("ticketId", ticketId);
        ArrayNode ticketItemsNode=(ArrayNode)jsonNode.get("ticketItems");
        ObjectNode firstTicketItem = (ObjectNode) ticketItemsNode.get(0);
        if(categoryId!=null) {
        firstTicketItem.put("categoryId", Integer.parseInt(categoryId));
        }
        firstTicketItem.put("itemId", itemId);
        firstTicketItem.put("itemName", itemName);
        firstTicketItem.put("price", itemPrice);
        String updatedPayload = null;
        try {
        	updatedPayload = mapper.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("UPDATED PAYLOAD: "+updatedPayload);
		response=TicketEndpoints.update(updatedPayload,"mpos",ticketId);
		System.out.println("---Response Of Update-----");
		response.then().log().all();
        jsonPath=response.jsonPath();
        int ticketItemId=jsonPath.getInt("data[0].ticketItems[0].id");
        List<Map<String, Object>> ticketItems = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", ticketItemId);
        item.put("status", "send");
        ticketItems.add(item);

        // Create the payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("ticketItems", ticketItems);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = null;
		try {
			jsonPayload = objectMapper.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("UPDATE_STATUS PAYLOAD: "+jsonPayload);
		System.out.println("TICKETID: "+ticketId);
		System.out.println("---Response Of UpdateStatus-----");
		response.then().log().all();
		jsonPath=response.jsonPath();
		String expectedItemName=jsonPath.getString("data[0].ticketItems[0].item.itemName");
        ticketPayload=new Ticket();
        ticketPayload.setOrdertype("all");
        ticketPayload.setOrderStatus("active");
        response=TicketEndpoints.getAllOrders(ticketPayload);
		System.out.println("---Response Of GetAllOrders-----");
        response.then().log().all();
        jsonPath=response.jsonPath();
        List<Integer> ticketIds=jsonPath.getList("data.id");
        Assert.assertTrue(ticketIds.contains(ticketId));
        response=TicketEndpoints.get(ticketId);
		System.out.println("---Response Of Get Ticket-----");
        response.then().log().all();
        jsonPath=response.jsonPath();
        Assert.assertEquals(jsonPath.getString("data.ticketItems[0].item.itemName"), expectedItemName);
	}

}
