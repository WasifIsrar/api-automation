package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import endpoints.AuthenticationEndpoints;
import endpoints.DiscountEndpoints;
import endpoints.ItemEndpoints;
import endpoints.MomDashboardEndpoints;
import endpoints.PaymentEndpoints;
import endpoints.ReportsEndpoints;
import endpoints.Routes;
import endpoints.TableEndpoints;
import endpoints.TicketEndpoints;
import endpoints.TicketV2Endpoints;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import payload.Authentication;
import payload.Table;
import payload.Ticket;
import utilities.Calculations;
import utilities.ConfigManager;

public class PosMomTests extends Routes{
	Response response;
	JsonPath jsonPath;
	String momDeviceId="d183fca146ce617f";
	
	@Test
	public void verifyUnpaidPosTicketOnMom() {
		int restaurantId=Integer.parseInt( ConfigManager.getProperty("restaurantId"));
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
        ticketPayload.setOrderSource("pos");
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
		response=TicketEndpoints.updateStatus(jsonPayload,ticketId,"pos");
		System.out.println("---Response Of UpdateStatus-----");
		response.then().log().all();
		response=TicketV2Endpoints.ticket();
		System.out.println("---Response Of Get Ticket V2-----");
		response.then().log().all();
		jsonPath=response.jsonPath();
		List<Integer> ticketIds=jsonPath.getList("data.id");
		System.out.println("TICKET IDS: "+ticketIds);
		Assert.assertTrue(ticketIds.contains(ticketId),"Ticket not found on Mom: ");
		response=PaymentEndpoints.getTicket(ticketId);
		System.out.println("Response of GET PAYMENT TICKET: ");
		response.then().log().all();
		jsonPath=response.jsonPath();
		String actualPaymentStatus=jsonPath.getString("data.ticketPaymentStatus");
		Assert.assertEquals(actualPaymentStatus, "unpaid");
	}
	
	@Test
	public void verifyPaidPosTicketOnMom() {
		int restaurantId=Integer.parseInt( ConfigManager.getProperty("restaurantId"));
		response=MomDashboardEndpoints.getReports();
		System.out.println("---Response Of Get Reports-----");
        response.then().log().all();
        jsonPath=response.jsonPath();
        double netSalesBefore=jsonPath.getDouble("data.netSales.currentAmount");
        int totalGuestsBefore=jsonPath.getInt("data.guestSummary.totalGuests");
        int completedGuestsBefore=jsonPath.getInt("data.guestSummary.completedGuests");
		Table tablePayload=new Table();
		tablePayload.setAvailable(true);
		response=TableEndpoints.allTables(tablePayload);
		System.out.println("---Response Of Get All Tables-----");
        response.then().log().all();
		jsonPath = response.jsonPath();
        int tableId = jsonPath.getInt("data[0].id");
        Ticket ticketPayload=new Ticket();
        ticketPayload.setCustomItems(new ArrayList<>());
        ticketPayload.setEmployeeId(ConfigManager.getProperty("employeeId"));
        ticketPayload.setOrderType("dine-in");
        ticketPayload.setNoOfGuests(5);
        ticketPayload.setNote("");
        ticketPayload.setOrderSource("pos");
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
		response=TicketEndpoints.updateStatus(jsonPayload,ticketId,"pos");
		System.out.println("---Response Of UpdateStatus-----");
		response.then().log().all();
		try {
			jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"calculationPayload.json")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonNode.put("restaurantId", restaurantId);
		 try {
	        	updatedPayload = mapper.writeValueAsString(jsonNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 response=PaymentEndpoints.createCalculation(updatedPayload, "pos",ticketId);
		 System.out.println("---Response Of Create Calculation-----");
		 response.then().log().all();
		 jsonPath=response.jsonPath();
		 double convenienceFee=jsonPath.getDouble("data.ticketInfo.paymentInfo.totalConvenienceFee");
		 double discount=jsonPath.getDouble("data.ticketInfo.paymentInfo.discount");
		 double total=jsonPath.getDouble("data.ticketInfo.paymentInfo.totalBill");
		 double serviceChargesAmount=jsonPath.getDouble("data.ticketInfo.paymentInfo.totalServiceCharge");
		 double subTotal=jsonPath.getDouble("data.ticketInfo.paymentInfo.discountedPrice");
		 double tax=jsonPath.getDouble("data.ticketInfo.paymentInfo.totalTax");
		 double tips=jsonPath.getDouble("data.ticketInfo.paymentInfo.tipAmount");
		 try {
				jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"paymentPayload.json")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 jsonNode.put("convenienceFee", convenienceFee);
		 jsonNode.put("discount", discount);
		 jsonNode.put("paid", total);
		 jsonNode.put("serviceChargesAmount", serviceChargesAmount);
		 jsonNode.put("subTotal", subTotal);
		 jsonNode.put("tax", tax);
		 jsonNode.put("tips", tips);
		 jsonNode.put("total", total);
		 try {
	        	updatedPayload = mapper.writeValueAsString(jsonNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 System.out.println("Payment Payload: "+updatedPayload);
		 response=PaymentEndpoints.createPayment(updatedPayload, "pos",ticketId);
		 System.out.println("---Response Of Payment-----");
		 response.then().log().all();
		 jsonPath=response.jsonPath();
		 double subtotal=jsonPath.getDouble("data.subTotal");
		 double expectedTotal=jsonPath.getDouble("data.transactionAmount");
		 String expectedPaymentStatus=jsonPath.getString("data.paymentStatus");
		 response=PaymentEndpoints.getTicket(ticketId);
		 System.out.println("---Response Of Get Ticket-----");
		 response.then().log().all();
		 jsonPath=response.jsonPath();
		 double actualPaidAmount=jsonPath.getDouble("data.paymentInfo.paidAmount");
		 Assert.assertEquals(actualPaidAmount, expectedTotal,"Paid Amount is incorrect: ");
		 double remainingAmount=jsonPath.getDouble("data.paymentInfo.remainingAmount");
		 Assert.assertEquals(remainingAmount, 0,"Remaining Amount is incorrect: ");
		 String paymentStatus=jsonPath.getString("data.ticketPaymentStatus");
		 Assert.assertEquals(paymentStatus, expectedPaymentStatus,"Payment Status is incorrect: ");
		 double expectedNetSales=Double.sum(netSalesBefore, subtotal);
		 int expectedGuests=totalGuestsBefore+5;
		 int expectedCompletedGuests=completedGuestsBefore+5;
		 response=MomDashboardEndpoints.getReports();
		 System.out.println("---Response Of Get Reports-----");
	     response.then().log().all();
	     jsonPath=response.jsonPath();
	     double netSalesAfter=jsonPath.getDouble("data.netSales.currentAmount");
	     int totalGuestsAfter=jsonPath.getInt("data.guestSummary.totalGuests");
	     int completedGuestsAfter=jsonPath.getInt("data.guestSummary.completedGuests");
	     Assert.assertEquals(netSalesAfter, expectedNetSales,"Net Sales Incorrect: ");
	     Assert.assertEquals(totalGuestsAfter, expectedGuests,"Total Guests Incorrect: ");
	     Assert.assertEquals(completedGuestsAfter, expectedCompletedGuests,"Completed Guests Incorrect: ");
	}
	
	@Test
	public void verifyVoidsOnMom() {
		int restaurantId=Integer.parseInt( ConfigManager.getProperty("restaurantId"));
		response=ReportsEndpoints.getCompsAndVoids();
		System.out.println("___________Get Comps and Voids Response______");
		response.then().log().all();
		jsonPath=response.jsonPath();
		double voidsBefore;
		try {
		voidsBefore=jsonPath.getDouble("data.voidsAndComps.voids.amount");
		}
		catch(NullPointerException e) {
			voidsBefore=0;
		}
		Table tablePayload=new Table();
		tablePayload.setAvailable(true);
		response=TableEndpoints.allTables(tablePayload);
		System.out.println("---Response Of Get All Tables-----");
        response.then().log().all();
		jsonPath = response.jsonPath();
        int tableId = jsonPath.getInt("data[0].id");
        Ticket ticketPayload=new Ticket();
        ticketPayload.setCustomItems(new ArrayList<>());
        ticketPayload.setEmployeeId(ConfigManager.getProperty("employeeId"));
        ticketPayload.setOrderType("dine-in");
        ticketPayload.setNoOfGuests(5);
        ticketPayload.setNote("");
        ticketPayload.setOrderSource("pos");
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
        String updatedPayload = null;
        try {
        	updatedPayload = mapper.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("UPDATED PAYLOAD: "+updatedPayload);
		response=TicketEndpoints.update(updatedPayload,"pos",ticketId);
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
		response=TicketEndpoints.updateStatus(jsonPayload,ticketId,"pos");
		System.out.println("---Response Of UpdateStatus-----");
		response.then().log().all();
		try {
			jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"calculationPayload.json")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonNode.put("restaurantId", restaurantId);
		 try {
	        	updatedPayload = mapper.writeValueAsString(jsonNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		response=PaymentEndpoints.createCalculation(updatedPayload, "pos",ticketId);
		System.out.println("---Response Of Create Calculation-----");
		response.then().log().all();
		jsonPath=response.jsonPath();
		double subTotal=jsonPath.getDouble("data.ticketInfo.paymentInfo.discountedPrice");
		double expectedVoids=Calculations.roundHalfUp(Double.sum(voidsBefore, subTotal));
		response=TicketEndpoints.voidOrder(ticketId,"pos");
		System.out.println("---Response Of Void Order-----");
		response.then().log().all();
		response=ReportsEndpoints.getCompsAndVoids();
		System.out.println("___________Get Comps and Voids Response______");
		response.then().log().all();
		jsonPath=response.jsonPath();
		double actualVoids=jsonPath.getDouble("data.voidsAndComps.voids.amount");
		Assert.assertEquals(actualVoids, expectedVoids,"Void Amount Not Equal: ");
	}
	
	@Test
	public void verifyVoidsWithTwoItemsOnMom() {
		int restaurantId=Integer.parseInt( ConfigManager.getProperty("restaurantId"));
		response=ReportsEndpoints.getCompsAndVoids();
		System.out.println("___________Get Comps and Voids Response______");
		response.then().log().all();
		jsonPath=response.jsonPath();
		double voidsBefore;
		try {
			voidsBefore=jsonPath.getDouble("data.voidsAndComps.voids.amount");
			}
			catch(NullPointerException e) {
				voidsBefore=0;
			}
		System.out.println("Voids Before:"+voidsBefore);
		Table tablePayload=new Table();
		tablePayload.setAvailable(true);
		response=TableEndpoints.allTables(tablePayload);
		System.out.println("---Response Of Get All Tables-----");
        response.then().log().all();
		jsonPath = response.jsonPath();
        int tableId = jsonPath.getInt("data[0].id");
        Ticket ticketPayload=new Ticket();
        ticketPayload.setCustomItems(new ArrayList<>());
        ticketPayload.setEmployeeId(ConfigManager.getProperty("employeeId"));
        ticketPayload.setOrderType("dine-in");
        ticketPayload.setNoOfGuests(5);
        ticketPayload.setNote("");
        ticketPayload.setOrderSource("pos");
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
		int itemId1=jsonPath.getInt("response.data.items[0].id");
		String itemName1=jsonPath.getString("response.data.items[0].itemName");
		double itemPrice1=jsonPath.getDouble("response.data.items[0].itemPrice");
		String categoryId1=null;
		if(!jsonPath.getList("response.data.items[0].categoryIds").isEmpty()) {
			categoryId1=jsonPath.getString("response.data.items[0].categoryIds[0]");
		}
		int itemId2=jsonPath.getInt("response.data.items[1].id");
		String itemName2=jsonPath.getString("response.data.items[1].itemName");
		double itemPrice2=jsonPath.getDouble("response.data.items[1].itemPrice");
		String categoryId2=null;
		if(!jsonPath.getList("response.data.items[1].categoryIds").isEmpty()) {
			categoryId2=jsonPath.getString("response.data.items[1].categoryIds[0]");
		}
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonNode = null;
        try {
			jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"TwoItemsPayload.json")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        jsonNode.put("tableId", tableId);
        jsonNode.put("ticketId", ticketId);
        ArrayNode ticketItemsNode=(ArrayNode)jsonNode.get("ticketItems");
        ObjectNode firstTicketItem = (ObjectNode) ticketItemsNode.get(0);
        if(categoryId1!=null) {
        firstTicketItem.put("categoryId", Integer.parseInt(categoryId1));
        }
        firstTicketItem.put("itemId", itemId1);
        firstTicketItem.put("itemName", itemName1);
        firstTicketItem.put("price", itemPrice1);
        ObjectNode secondTicketItem = (ObjectNode) ticketItemsNode.get(1);
        if(categoryId2!=null) {
        	secondTicketItem.put("categoryId", Integer.parseInt(categoryId2));
        }
        firstTicketItem.put("itemId", itemId2);
        firstTicketItem.put("itemName", itemName2);
        firstTicketItem.put("price", itemPrice2);
        String updatedPayload = null;
        try {
        	updatedPayload = mapper.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("UPDATED PAYLOAD: "+updatedPayload);
		response=TicketEndpoints.update(updatedPayload,"pos",ticketId);
		System.out.println("---Response Of Update-----");
		response.then().log().all();
		jsonPath=response.jsonPath();
		List<Integer> idsFromJson = jsonPath.getList("data[0].ticketItems.id", Integer.class);
		System.out.println("Ids List Size:"+idsFromJson.size());
		for(int id:idsFromJson) {
			System.out.println("Ids:"+id);
		}
        List<Map<String, Object>> ticketItems = new ArrayList<>();
        Map<String, Object> item;
        for(int ticketid:idsFromJson) {
        System.out.println("ticket id:"+ticketid);
        item = new HashMap<>();
        item.put("id", ticketid);
        item.put("status", "send");
        ticketItems.add(item);
        }
        System.out.println("Ticket Items:"+ticketItems.size());
        System.out.println("First ticket Item:"+ticketItems.get(0));
        System.out.println("Second ticket Item:"+ticketItems.get(1));

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
		response=TicketEndpoints.updateStatus(jsonPayload,ticketId,"pos");
		
		System.out.println("---Response Of UpdateStatus-----");
		response.then().log().all();
		System.out.println("----Voiding the Item at first index and updating the payload----");
		int firstIndex =idsFromJson.get(0);  
		List<Map<String, Object>> ticketItemsId = new ArrayList<>();
	    Map<String, Object> itemId = new HashMap<>();
	    itemId.put("id", firstIndex);
	    itemId.put("status", "void");
	    ticketItemsId.add(itemId);
	    payload = new HashMap<>();
        payload.put("ticketItems", ticketItemsId);
        objectMapper = new ObjectMapper();
        jsonPayload = null;
		try {
			jsonPayload = objectMapper.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("UPDATE_STATUS PAYLOAD: "+jsonPayload);
	    response=TicketEndpoints.updateStatus(jsonPayload,ticketId,"pos");
	    System.out.println("---Response Of Update Voiding the item-----");
		response.then().log().all();
		double amount=jsonPath.getDouble("data[0].ticketItems[0].price");
		System.out.println("amount"+ amount);
		
		try {
			jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"calculationPayload.json")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonNode.put("restaurantId", restaurantId);
		 try {
	        	updatedPayload = mapper.writeValueAsString(jsonNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		response=PaymentEndpoints.createCalculation(updatedPayload, "pos",ticketId);
		System.out.println("---Response Of Create Calculation-----");
		response.then().log().all();
		jsonPath=response.jsonPath();
		double expectedVoids=Calculations.roundHalfUp(Double.sum(voidsBefore, amount));
		response=ReportsEndpoints.getCompsAndVoids();
		System.out.println("___________Get Comps and Voids Response______");
		response.then().log().all();
		jsonPath=response.jsonPath();
		double actualVoids=jsonPath.getDouble("data.voidsAndComps.voids.amount");
		System.out.println("actual voids"+actualVoids);
		Assert.assertEquals(actualVoids, expectedVoids,"Void Amount Not Equal: ");
	}
	
	@Test
	public void verifyCompsOnMom() {
		int restaurantId=Integer.parseInt( ConfigManager.getProperty("restaurantId"));
		response=ReportsEndpoints.getCompsAndVoids();
		System.out.println("___________Get Comps and Voids Response______");
		response.then().log().all();
		jsonPath=response.jsonPath();
		double compsBefore=jsonPath.getDouble("data.voidsAndComps.comps.amount");
		Table tablePayload=new Table();
		tablePayload.setAvailable(true);
		response=TableEndpoints.allTables(tablePayload);
		System.out.println("---Response Of Get All Tables-----");
        response.then().log().all();
		jsonPath = response.jsonPath();
        int tableId = jsonPath.getInt("data[0].id");
        Ticket ticketPayload=new Ticket();
        ticketPayload.setCustomItems(new ArrayList<>());
        ticketPayload.setEmployeeId(ConfigManager.getProperty("employeeId"));
        ticketPayload.setOrderType("dine-in");
        ticketPayload.setNoOfGuests(5);
        ticketPayload.setNote("");
        ticketPayload.setOrderSource("pos");
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
        String updatedPayload = null;
        try {
        	updatedPayload = mapper.writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("UPDATED PAYLOAD: "+updatedPayload);
		response=TicketEndpoints.update(updatedPayload,"pos",ticketId);
		System.out.println("---Response Of Update-----");
		response.then().log().all();
		jsonPath=response.jsonPath();
		response=DiscountEndpoints.getList(restaurantId);
		System.out.println("---Response Of Get Discount List-----");
		response.then().log().all();
		jsonPath=response.jsonPath();
		
		int discountId=jsonPath.getInt("response.data.discounts[0].id");
		String amountInPercent=jsonPath.getString("response.data.discounts[0].value");
        double discountValue = Double.parseDouble(amountInPercent.replace("%", ""));
        List<Map<String, Object>> discounts = new ArrayList<>();
        Map<String, Object> discount = new HashMap<>();
        discount.put("id", discountId);
        discount.put("value", discountValue);
        discounts.add(discount);
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("discounts", discounts);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = null;
		try {
			jsonPayload = objectMapper.writeValueAsString(updatePayload);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("UPDATE Ticket With Discount Payload: "+jsonPayload);
		response=TicketEndpoints.update(jsonPayload,"pos",ticketId);
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
        objectMapper = new ObjectMapper();
        jsonPayload = null;
		try {
			jsonPayload = objectMapper.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("UPDATE_STATUS PAYLOAD: "+jsonPayload);
		System.out.println("TICKETID: "+ticketId);
		response=TicketEndpoints.updateStatus(jsonPayload,ticketId,"pos");
		System.out.println("---Response Of UpdateStatus-----");
		response.then().log().all();
		try {
			jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"calculationPayload.json")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonNode.put("restaurantId", restaurantId);
		 try {
	        	updatedPayload = mapper.writeValueAsString(jsonNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		response=PaymentEndpoints.createCalculation(updatedPayload, "pos",ticketId);
		System.out.println("---Response Of Create Calculation-----");
		response.then().log().all();
		jsonPath=response.jsonPath();
		double convenienceFee=jsonPath.getDouble("data.ticketInfo.paymentInfo.totalConvenienceFee");
		double discountamount=jsonPath.getDouble("data.ticketInfo.paymentInfo.discount");
		double total=jsonPath.getDouble("data.ticketInfo.paymentInfo.totalBill");
		double serviceChargesAmount=jsonPath.getDouble("data.ticketInfo.paymentInfo.totalServiceCharge");
		double subTotal=jsonPath.getDouble("data.ticketInfo.paymentInfo.discountedPrice");
		double tax=jsonPath.getDouble("data.ticketInfo.paymentInfo.totalTax");
		double tips=jsonPath.getDouble("data.ticketInfo.paymentInfo.tipAmount");
		try {
				jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"paymentPayload.json")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		jsonNode.put("convenienceFee", convenienceFee);
		jsonNode.put("discount", discountamount);
		jsonNode.put("paid", total);
		jsonNode.put("serviceChargesAmount", serviceChargesAmount);
		jsonNode.put("subTotal", subTotal);
		jsonNode.put("tax", tax);
		jsonNode.put("tips", tips);
		jsonNode.put("total", total);
		try {
	        	updatedPayload = mapper.writeValueAsString(jsonNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		System.out.println("Payment Payload: "+updatedPayload);
		response=PaymentEndpoints.createPayment(updatedPayload, "pos",ticketId);
		System.out.println("---Response Of Payment-----");
		response.then().log().all();
		jsonPath=response.jsonPath();
        List<Map<String, Object>> discountsList = response.jsonPath().getList("response.data.discounts");
        double discountAmount = 0;
        int targetId=2;
        for(Map<String, Object> discountMap : discountsList) {
            if (discount.get("id") instanceof Integer && (Integer) discount.get("id") == targetId) {
            	discountAmount=(double) discountMap.get("value");
            	break;
            }
        }
		double expectedComps=Calculations.roundHalfUp(Double.sum(compsBefore, discountAmount));
		response=ReportsEndpoints.getCompsAndVoids();
		System.out.println("___________Get Comps and Voids Response______");
		response.then().log().all();
		jsonPath=response.jsonPath();
		double actualComps=jsonPath.getDouble("data.voidsAndComps.comps.amount");
		Assert.assertEquals(actualComps, expectedComps,"Comps Not Equal: ");
	}
}
	
