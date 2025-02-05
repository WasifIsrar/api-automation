package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.beust.ah.A;
import endpoints.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javafaker.Faker;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import payload.Authentication;
import payload.Table;
import utilities.ConfigManager;
import utilities.WebdashTokenManager;

import static utilities.Calculations.roundHalfUp;

public class WebDashPosTests extends Routes {
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode jsonNode = null;
    Faker faker = new Faker();
    Response response;
    JsonPath jsonPath;
    String updatedPayload = null;

    @Test
    public void verifyMenuOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "menuPayload.json")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String menu = faker.food().dish();
        System.out.println(menu);
        ObjectNode menuNode = (ObjectNode) jsonNode.get("menu");
        menuNode.put("menuName", menu);
        menuNode.put("posDisplayName", menu);
        menuNode.put("restaurantId", restaurantId);
        jsonNode.put("restaurantId", restaurantId);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("UPDATED PAYLOAD: " + updatedPayload);
        response = MenuEndpoints.create(updatedPayload);
        System.out.println("RESPONSE OF CREATE MENU");
        response.then().log().all();
        response = MenuEndpoints.get(restaurantId);
        System.out.println("RESPONSE OF GET MENU");
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<String> menuNames = jsonPath.getList("data.menu.menuName");
        Assert.assertTrue(menuNames.contains(menu));
    }

    @Test
    public void verifyItemOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "itemPayload.json")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String item = faker.food().dish();
        System.out.println(item);
        ObjectNode itemNode = (ObjectNode) jsonNode.get("item");
        itemNode.put("itemName", item);
        itemNode.put("posDisplayName", item);
        itemNode.put("kdsDisplayName", item);
        itemNode.put("restaurantId", restaurantId);
        double itemPrice = 13.75;
        itemNode.put("itemPrice", itemPrice);
        itemNode.put("totalPrice", itemPrice);
        jsonNode.put("restaurantId", restaurantId);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("UPDATED PAYLOAD: " + updatedPayload);
        response = ItemEndpoints.create(updatedPayload);
        System.out.println("____Create ITEM RESPONSE_____");
        response.then().log().all();
        response = MenuEndpoints.get(restaurantId);
        System.out.println("RESPONSE OF GET MENU");
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<String> itemNames = jsonPath.getList("data.item.itemName");
        int index = -1;
        for (int i = 0; i < itemNames.size(); i++) {
            if (itemNames.get(i).equals(item)) {
                index = i; // Capture the index
                break;
            }
        }
        Assert.assertTrue(itemNames.contains(item));
        double actualItemPrice = jsonPath.getDouble("data.item[" + index + "].itemPrice");
        Assert.assertEquals(actualItemPrice, itemPrice);
    }

    @Test
    public void verifyCategoryOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "categoryPayload.json")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String category = faker.food().dish();
        System.out.println(category);
        ObjectNode categoryNode = (ObjectNode) jsonNode.get("category");
        categoryNode.put("categoryName", category);
        categoryNode.put("posDisplayName", category);
        categoryNode.put("kdsDisplayName", category);
        categoryNode.put("restaurantId", restaurantId);
        jsonNode.put("restaurantId", restaurantId);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("UPDATED PAYLOAD: " + updatedPayload);
        response = CategoryEndpoints.create(updatedPayload);
        System.out.println("____Create CATEGORY RESPONSE_____");
        response.then().log().all();
        response = MenuEndpoints.get(restaurantId);
        System.out.println("RESPONSE OF GET MENU");
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<String> itemNames = jsonPath.getList("data.category.categoryName");
        Assert.assertTrue(itemNames.contains(category));
    }

    @Test
    public void verifyModifierOptionOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "modifierOptionPayload.json")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String modifierOption = faker.food().dish();
        ObjectNode modifierOptionNode = (ObjectNode) jsonNode.get("modifierOption");
        modifierOptionNode.put("optionName", modifierOption);
        modifierOptionNode.put("posDisplayName", modifierOption);
        modifierOptionNode.put("restaurantId", restaurantId);
        jsonNode.put("restaurantId", restaurantId);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("UPDATED PAYLOAD: " + updatedPayload);
        response = ModifierOptionEndpoints.create(updatedPayload);
        System.out.println("____Create Modifier Option RESPONSE_____");
        response.then().log().all();
        response = MiscEndpoints.clearCache(restaurantId, "menu");
        System.out.println("CLEAR CACHE RESPONSE_____");
        response.then().log().all();
        response = MenuEndpoints.get(restaurantId);
        System.out.println("RESPONSE OF GET MENU");
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<String> modifierOptionNames = jsonPath.getList("data.modifier_option.optionName");
        for (String modifierOptionName : modifierOptionNames) {
            System.out.println(modifierOptionName);
        }
        Assert.assertTrue(modifierOptionNames.contains(modifierOption));
    }

    @Test
    public void verifyModifierOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        response = ModifierOptionEndpoints.get(restaurantId);
        System.out.println("____GET MODIFIER OPTION RESPONSE_____");
        response.then().log().all();
        jsonPath = response.jsonPath();
        int id = jsonPath.getInt("response.data.modifierOptions[0].id");
        String optionName = jsonPath.getString("response.data.modifierOptions[0].optionName");
        System.out.println("OPTION NAME: " + optionName);
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "modifierPayload.json")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String modifier = faker.food().dish();
        ObjectNode modifierNode = (ObjectNode) jsonNode.get("modifier");
        modifierNode.put("modifierName", modifier);
        modifierNode.put("posDisplayName", modifier);
        modifierNode.put("restaurantId", restaurantId);
        ArrayNode modifierOptionsNode = (ArrayNode) jsonNode.get("modifierOptions");
        ObjectNode firstOption = (ObjectNode) modifierOptionsNode.get(0);
        firstOption.put("id", id);
        firstOption.put("optionName", optionName);
        firstOption.put("optionDisplayName", optionName);
        jsonNode.put("restaurantId", restaurantId);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("UPDATED PAYLOAD: " + updatedPayload);
        response = ModifierEndpoints.create(updatedPayload);
        System.out.println("____Create MODIFIER RESPONSE_____");
        response.then().log().all();
        response = MiscEndpoints.clearCache(restaurantId, "menu");
        System.out.println("CLEAR CACHE RESPONSE_____");
        response.then().log().all();
        response = MenuEndpoints.get(restaurantId);
        System.out.println("RESPONSE OF GET MENU");
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<String> modifiers = jsonPath.getList("data.modifier.modifierName");
        for (String modifierName : modifiers) {
            System.out.println(modifierName);
        }
        Assert.assertTrue(modifiers.contains(modifier));
        List<String> list_modifier_modifierOption = jsonPath.getList("data.modifier_modifierOption.optionDisplayName");
        for (String optionDisplayName : list_modifier_modifierOption) {
            System.out.println(optionDisplayName);
        }
        Assert.assertTrue(list_modifier_modifierOption.contains(optionName));
    }

    @Test
    public void verifyModifierGroupOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        response = ModifierEndpoints.get(restaurantId);
        response.then().log().all();
        jsonPath = response.jsonPath();
        int id = jsonPath.getInt("response.data.modifiers[0].id");
        String modifierName = jsonPath.getString("response.data.modifiers[0].modifierName");
        System.out.println("Id" + id);
        System.out.println("modifierName" + modifierName);
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "modifierGroupPayload.json")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String modifierGroup = faker.food().dish();
        ObjectNode modifierGroupNode = (ObjectNode) jsonNode.get("modifierGroupData");
        modifierGroupNode.put("groupName", modifierGroup);
        modifierGroupNode.put("posDisplayName", modifierGroup);
        modifierGroupNode.put("restaurantId", restaurantId);
        ArrayNode modifierGroupNodes = (ArrayNode) jsonNode.get("modifierList");
        ObjectNode firstOption = (ObjectNode) modifierGroupNodes.get(0);
        firstOption.put("value", id);
        firstOption.put("label", modifierName);
        jsonNode.put("restaurantId", restaurantId);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("UPDATED PAYLOAD: " + updatedPayload);
        response = ModifierGroupEndpoints.create(updatedPayload);
        System.out.println("____Create Modifier Group RESPONSE_____");
        response.then().log().all();
        jsonPath = response.jsonPath();
        int actualModifierGroupId = jsonPath.getInt("response.data.id");
        response = MiscEndpoints.clearCache(restaurantId, "menu");
        System.out.println("CLEAR CACHE RESPONSE_____");
        response.then().log().all();
        System.out.println("RESPONSE OF GET MENU");
        response = MenuEndpoints.get(restaurantId);
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<String> modifiersGroups = jsonPath.getList("data.modifier_group.groupName");
        System.out.println("Modifiers List" + modifiersGroups);
        Assert.assertTrue(modifiersGroups.contains(modifierGroup), "Modifier Group Not Found: ");
        List<Integer> listofModifierID = jsonPath.getList("data.modifierGroup_modifiers.modifierId");
        List<Integer> listofModifierGroupID = jsonPath.getList("data.modifierGroup_modifiers.modifierGroupId");
        System.out.println("Modifiers id: " + listofModifierID);
        System.out.println("Modifier Groups id: " + listofModifierGroupID);
        Assert.assertTrue(listofModifierGroupID.contains(actualModifierGroupId), "Modifier Group Id not found in modifierGroup_modifiers: ");
        Assert.assertTrue(listofModifierID.contains(id), "Modifier Id not found in modifierGroup_modifiers: ");
    }

    @Test
    public void verifyServiceChargeonPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "serviceChargesPayload.json")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String serviceChargeName = faker.funnyName().name();
        int value = faker.number().randomDigitNotZero();
        ObjectNode serviceChargeNode = (ObjectNode) jsonNode.get("serviceCharge");
        serviceChargeNode.put("name", serviceChargeName);
        serviceChargeNode.put("value", value);
        jsonNode.put("restaurantId", restaurantId);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("UPDATED PAYLOAD: " + updatedPayload);
        response = ServiceChargeEndpoints.create(updatedPayload);
        System.out.println("____Create Service Charge RESPONSE_____");
        response.then().log().all();
        response = MiscEndpoints.clearCache(restaurantId, "menu");
        System.out.println("CLEAR CACHE RESPONSE_____");
        response.then().log().all();
        response = MenuEndpoints.get(restaurantId);
        System.out.println("RESPONSE OF GET MENU");
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<String> serviceChargeNames = jsonPath.getList("data.service_charge_configurations.name");
        List<Integer> serviceChargeValues = jsonPath.getList("data.service_charge_configurations.value");
        Assert.assertTrue(serviceChargeNames.contains(serviceChargeName), "Service Charge Name Not Fetched in Get Menu: ");
        Assert.assertTrue(serviceChargeValues.contains(value), "Service Charge Value Not Fetched in Get Menu: ");
        List<String> financialConfigServiceChargeNames = jsonPath.getList("data.financialConfigurations.serviceChargesConfigurations.name");
        List<Integer> financialConfigServiceChargeValues = jsonPath.getList("data.service_charge_configurations.value");
        Assert.assertTrue(financialConfigServiceChargeNames.contains(serviceChargeName), "Service Charge Name Not Fetched in Financial Configuration Get Menu: ");
        Assert.assertTrue(financialConfigServiceChargeValues.contains(value), "Service Charge Value Not Fetched in Financial Configuration Get Menu: ");
    }

    @Test
    public void verifySalesTaxOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "salesTaxPayload.json")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String salesTaxName = faker.funnyName().name();
        int value = faker.number().randomDigitNotZero();
        ObjectNode salesTaxNode = (ObjectNode) jsonNode.get("salesTax");
        salesTaxNode.put("name", salesTaxName);
        salesTaxNode.put("restaurantId", restaurantId);
        ObjectNode formulaNode = (ObjectNode) salesTaxNode.get("formula");
        formulaNode.put("rate", value);
        jsonNode.put("restaurantId", restaurantId);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("UPDATED PAYLOAD: " + updatedPayload);
        response = SalesTaxEndpoints.create(updatedPayload);
        System.out.println("__________Create Sales Tax Response_________");
        response.then().log().all();
        response = MiscEndpoints.clearCache(restaurantId, "menu");
        System.out.println("CLEAR CACHE RESPONSE_____");
        response.then().log().all();
        response = MenuEndpoints.get(restaurantId);
        System.out.println("RESPONSE OF GET MENU");
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<String> salesTaxNames = jsonPath.getList("data.tax_configurations.name");
        List<Integer> rates = jsonPath.getList("data.tax_configurations.formula.rate");
        Assert.assertTrue(salesTaxNames.contains(salesTaxName), "Sales Tax Name Not Fetched in Get Menu: ");
        Assert.assertTrue(rates.contains(value), "Sales Tax Rate Not Fetched in Get Menu: ");
        List<String> financialConfigTaxNames = jsonPath.getList("data.financialConfigurations.taxConfigurations.name");
        List<Integer> financialConfigTaxRates = jsonPath.getList("data.financialConfigurations.taxConfigurations.formula.rate");
        Assert.assertTrue(financialConfigTaxNames.contains(salesTaxName), "Sales Tax Name Not Fetched in Financial Configuration Get Menu: ");
        Assert.assertTrue(financialConfigTaxRates.contains(value), "Sales Tax Rate Not Fetched in Financial Configuration Get Menu: ");
    }

    @Test
    public void verifyCompsOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "compsPayload.json")));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        jsonNode.put("restaurantId", restaurantId);
        String compName = faker.funnyName().name();
        int value = faker.number().randomDigitNotZero();
        ObjectNode discountConfigNode = (ObjectNode) jsonNode.get("discountConfiguration");
        discountConfigNode.put("name", compName);
        discountConfigNode.put("posName", compName);
        discountConfigNode.put("value", value);
        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("UPDATED PAYLOAD: " + updatedPayload);
        response = DiscountEndpoints.create(updatedPayload);
        System.out.println("__________Create Comp Response_________");
        response.then().log().all();
        response = MiscEndpoints.clearCache(restaurantId, "menu");
        System.out.println("CLEAR CACHE RESPONSE_____");
        response.then().log().all();
        response = MenuEndpoints.get(restaurantId);
        System.out.println("RESPONSE OF GET MENU");
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<String> discountNames = jsonPath.getList("data.discount_configuration.name");
        List<String> posDisplayNames = jsonPath.getList("data.discount_configuration.posName");
        List<Integer> values = jsonPath.getList("data.discount_configuration.value");
        Assert.assertTrue(posDisplayNames.contains(compName), "Pos Name of discount not found in Get Menu: ");
        Assert.assertTrue(discountNames.contains(compName), "Discount Name not found in Get Menu: ");
        Assert.assertTrue(values.contains(value), "Discount Value not found in Get Menu: ");
    }

    @Test
    public void verifyCashDrawerOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        response = RestaurantEndpoints.getAllPos(restaurantId);
        response.then().log().all();
        int id = response.jsonPath().getInt("data[0].id");
        System.out.println("POS ID: " + id);

        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "createCashDrawerPayload.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String cashDrawerName = faker.funnyName().name();

        ((ObjectNode) jsonNode).put("name", cashDrawerName);
        ((ObjectNode) jsonNode).put("posId", id);
        ((ObjectNode) jsonNode).put("restaurantId", restaurantId);

        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        response = CashDrawerEndpoints.createCashDrawer(updatedPayload, "dashboard");
        System.out.println("Create Cash Drawer Response: ");
        response.then().log().all();

        response = CashDrawerEndpoints.getAllCashDrawers("pos");
        System.out.println("Get All Cash Drawers Response: ");
        response.then().log().all();
        jsonPath = response.jsonPath();
        List<String> cashDrawerNamesList = jsonPath.getList("data.name");
        Assert.assertTrue(cashDrawerNamesList.contains(cashDrawerName), "Cash Drawer Is Not Found On POS");
    }

    @Test
    public void verifyTendersOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));

        try {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "createTenderPayload.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String tenderName = faker.funnyName().name();

        ((ObjectNode) jsonNode).put("tenderName", tenderName);
        ((ObjectNode) jsonNode).put("restaurantId", restaurantId);

        try {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        response = TenderEndpoints.createTender(updatedPayload, "dashboard");
        System.out.println("Create Tender Response: ");
        response.then().log().all();

        response = TenderEndpoints.getActiveTenders("pos");
        System.out.println("Get Active Tenders: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        List<String> activeTendersList = jsonPath.getList("data.tenderName");
        Assert.assertTrue(activeTendersList.contains(tenderName), "Tender Is Not Created On POS");
    }

    @Test
    public void verifyTenderPaymentOnDashboard() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        String employeeId = ConfigManager.getProperty("employeeId");

        Table tablePayload = new Table();
        tablePayload.setAvailable(true);
        Response response = TableEndpoints.allTables(tablePayload);
        System.out.println("All Tables Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        int tableId = jsonPath.getInt("data[0].id");
        System.out.println("Table ID: " + tableId);

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "createTicketIdPayload.json")));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        jsonNode.put("employeeId", employeeId);
        jsonNode.put("tableNumber", tableId);

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        response = TicketEndpoints.createId(updatedPayload);
        System.out.println("Create Ticket Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        int ticketId = jsonPath.getInt("data[0].id");
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

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "updatePayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        jsonNode.put("ticketId", ticketId);
        jsonNode.put("tableId", tableId);
        ArrayNode ticketItemsNode=(ArrayNode)jsonNode.get("ticketItems");
        ObjectNode firstTicketItem = (ObjectNode) ticketItemsNode.get(0);
        if(categoryId!=null) {
        firstTicketItem.put("categoryId", Integer.parseInt(categoryId));
        }
        firstTicketItem.put("itemId", itemId);
        firstTicketItem.put("itemName", itemName);
        firstTicketItem.put("price", itemPrice);
        
        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        response = TicketEndpoints.update(updatedPayload, "pos", ticketId);
        System.out.println("Update Ticket Response: ");
        response.then().log().all();

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "calculationPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        jsonNode.put("restaurantId", restaurantId);

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        response = PaymentEndpoints.createCalculation(updatedPayload, "pos", ticketId);
        System.out.println("Calculation Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        double convenienceFee = jsonPath.getDouble("data.ticketInfo.paymentInfo.totalConvenienceFee");
        double discount = jsonPath.getDouble("data.ticketInfo.paymentInfo.discount");
        double total = jsonPath.getDouble("data.ticketInfo.paymentInfo.totalBill");
        double serviceChargesAmount = jsonPath.getDouble("data.ticketInfo.paymentInfo.totalServiceCharge");
        double subTotal = jsonPath.getDouble("data.ticketInfo.paymentInfo.discountedPrice");
        double tax = jsonPath.getDouble("data.ticketInfo.paymentInfo.totalTax");
        double tips = jsonPath.getDouble("data.ticketInfo.paymentInfo.tipAmount");

        response = TenderEndpoints.getActiveTenders("pos");
        System.out.println("Get Active Tenders: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        int tenderId = jsonPath.getInt("data[0].id");

        response = TenderEndpoints.getDashboardTenderList("dashboard", restaurantId);
        System.out.println("Get Dashboard Tenders List: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        String tenderAmountStr = jsonPath.getString("response.data.tender.find{it.id == " + tenderId + "}.amount");
        double tenderAmount = Double.parseDouble(tenderAmountStr);

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"payload"+File.separator+"paymentPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        jsonNode.put("convenienceFee", convenienceFee);
        jsonNode.put("discount", discount);
        jsonNode.put("paid", total);
        jsonNode.put("paymentMethod", "tender");
        jsonNode.put("serviceChargesAmount", serviceChargesAmount);
        jsonNode.put("subTotal", subTotal);
        jsonNode.put("tax", tax);
        jsonNode.put("tips", tips);
        jsonNode.put("total", total);
        jsonNode.put("tenderId", tenderId);

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        response = PaymentEndpoints.createPayment(updatedPayload, "pos", ticketId);
        System.out.println("Create Payment Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        double totalAmount = jsonPath.getDouble("data.total");

        double updatedTenderAmount = roundHalfUp(tenderAmount + totalAmount);

        response = TenderEndpoints.getDashboardTenderList("dashboard", restaurantId);
        System.out.println("Get Dashboard Tenders List: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        String finalTenderAmountStr = jsonPath.getString("response.data.tender.find{it.id == " + tenderId + "}.amount");
        double finalTenderAmount = Double.parseDouble(finalTenderAmountStr);
        Assert.assertEquals(finalTenderAmount, updatedTenderAmount, "Ticket Is Not Paid By The Tender");
    }

    @Test
    public void verifyMenuWithCategoryOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));

        response = CategoryEndpoints.getAllCategories("dashboard", restaurantId);
        System.out.println("Get Category List: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        int categoryId = jsonPath.getInt("response.data.categories[0].id");
        String categoryName = jsonPath.getString("response.data.categories[0].categoryName");

        System.out.println("Get Category Id: " + categoryId);
        System.out.println("Get Category Name: " + categoryName);

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "menuPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String menu = faker.food().dish();
        System.out.println("Menu Name: " + menu);
        ObjectNode menuNode = (ObjectNode) jsonNode.get("menu");
        menuNode.put("menuName", menu);
        menuNode.put("posDisplayName", menu);
        menuNode.put("restaurantId", restaurantId);

        ObjectNode categoryNode = mapper.createObjectNode();
        categoryNode.put("value", categoryId);
        categoryNode.put("label", categoryName);
        ArrayNode categoriesArray = (ArrayNode) jsonNode.get("categories");
        categoriesArray.add(categoryNode);
        
        jsonNode.put("restaurantId", restaurantId);

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        response = MenuEndpoints.create(updatedPayload);
        System.out.println("Create Menu Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        int menuId = jsonPath.getInt("response.data.id");

        response = MenuEndpoints.get(restaurantId);
        System.out.println("Get POS Menu Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        List<String> menuName = jsonPath.getList("data.menu.menuName");
        List<Map<String, Object>> menuCategories = jsonPath.getList("data.menus_categories");
        Assert.assertTrue(menuName.contains(menu), "Menu Is Not Present In Menu Names");
        boolean isMenuWithCategory = false;
        for (Map<String, Object> category : menuCategories)
        {
            Object menuIdResponse = category.get("menuId");
            Object categoryIdResponse = category.get("categoryId");

            if (menuIdResponse.equals(menuId) && categoryIdResponse.equals(categoryId))
            {
                isMenuWithCategory = true;
                break;
            }
        }
        Assert.assertTrue(isMenuWithCategory, "Menu is not linked with this specific category");
    }

    @Test
    public void verifyCategoryWithItemOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));

        response = ItemEndpoints.getItemsList("dashboard", restaurantId);
        System.out.println("Get Items List Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();

        int itemId = jsonPath.getInt("response.data.items[0].id");
        String itemName = jsonPath.getString("response.data.items[0].itemName");

        System.out.println("Item ID: " + itemId);
        System.out.println("Item Name: " + itemName);

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "categoryPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String category = faker.food().dish();

        ObjectNode categoryNode = (ObjectNode) jsonNode.get("category");
        categoryNode.put("categoryName", category);
        categoryNode.put("posDisplayName", category);
        categoryNode.put("kdsDisplayName", category);
        categoryNode.put("restaurantId", restaurantId);
        jsonNode.put("restaurantId", restaurantId);

        ObjectNode categoryNameNode = mapper.createObjectNode();
        categoryNameNode.put("itemId", itemId);
        categoryNameNode.put("itemName", itemName);
        categoryNameNode.put("sortOrder", 1);
        ArrayNode categoriesArray = (ArrayNode) jsonNode.get("categoryItems");
        categoriesArray.add(categoryNameNode);

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        response = CategoryEndpoints.create(updatedPayload);
        System.out.println("Create Category Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        int categoryId = jsonPath.getInt("response.data.id");

        response = MenuEndpoints.get(restaurantId);
        System.out.println("Get POS Menu Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        List<String> categoryNames = jsonPath.getList("data.category.categoryName");
        List<Map<String, Object>> categoryItems = jsonPath.getList("data.category_items");
        Assert.assertTrue(categoryNames.contains(category), "Category is not present in category names");

        boolean categoryWithItem = false;
        for (Map<String, Object> categoryItem : categoryItems)
        {
            Object itemIdResponse = categoryItem.get("itemId");
            Object categoryIdResponse = categoryItem.get("categoryId");

            if (itemIdResponse.equals(itemId) && categoryIdResponse.equals(categoryId))
            {
                categoryWithItem = true;
                break;
            }
        }
        Assert.assertTrue(categoryWithItem, "Category is not linked with specific item");
    }

    @Test
    public void verifyItemModifiersAndGroupsOnPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));

        response = ModifierEndpoints.get(restaurantId);
        System.out.println("Get Modifier List Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        int modifierId = jsonPath.getInt("response.data.modifiers[0].id");
        String modifierName = jsonPath.getString("response.data.modifiers[0].modifierName");

        response = ModifierGroupEndpoints.get(restaurantId);
        System.out.println("Get Modifier List Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        int modifierGroupId = jsonPath.getInt("response.data.modifierGroup[0].id");
        String modifierGroupName = jsonPath.getString("response.data.modifierGroup[0].groupName");

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "itemPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String item = faker.food().dish();
        ObjectNode itemNode = (ObjectNode) jsonNode.get("item");
        itemNode.put("itemName", item);
        itemNode.put("posDisplayName", item);
        itemNode.put("kdsName", item);
        itemNode.put("restaurantId", restaurantId);

        ObjectNode itemModNode = mapper.createObjectNode();
        itemModNode.put("id", modifierId);
        itemModNode.put("label", modifierName);
        itemModNode.put("type", "modifier");
        itemModNode.put("sortOrder", 1);

        ObjectNode itemModGroupNode = mapper.createObjectNode();
        itemModGroupNode.put("id", modifierGroupId);
        itemModGroupNode.put("label", modifierGroupName);
        itemModGroupNode.put("type", "modifierGroup");
        itemModGroupNode.put("sortOrder", 2);

        ArrayNode itemModifiersAndGroupsArray = (ArrayNode) jsonNode.get("itemModifiersAndGroups");
        itemModifiersAndGroupsArray.add(itemModNode);
        itemModifiersAndGroupsArray.add(itemModGroupNode);
        
        jsonNode.put("restaurantId", restaurantId);
        
        System.out.println("ITEM PAYLOAD--------: " + jsonNode.toString());

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        response = ItemEndpoints.create(updatedPayload);
        System.out.println("Create Item With Modifier and Modifier Group Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        int itemId = jsonPath.getInt("id");
        String itemName = jsonPath.getString("itemName");

        response = MenuEndpoints.get(restaurantId);
        System.out.println("Get POS Menu Response: ");
        response.then().log().all();

        jsonPath = response.jsonPath();
        List<String> itemNames = jsonPath.getList("data.item.itemName");
        List<Map<String, Object>> itemModifier = jsonPath.getList("data.item_modifiers");
        List<Map<String, Object>> itemModifierGroup = jsonPath.getList("data.item_modifierGroups");
        Assert.assertTrue(itemNames.contains(itemName), "Item Name is not present in item names list");

        boolean itemWithModifier = false;
        boolean itemWithModifierGroup = false;

        for (Map<String, Object> itemModifiers : itemModifier)
        {
            Object itemIdResponse = itemModifiers.get("itemId");
            Object modifierIdResponse = itemModifiers.get("modifierId");

            if (itemIdResponse.equals(itemId) && modifierIdResponse.equals(modifierId))
            {
                itemWithModifier = true;
                break;
            }
        }

        for (Map<String, Object> itemModifierGroups : itemModifierGroup)
        {
            Object itemIdResponse = itemModifierGroups.get("itemId");
            Object modifierGroupIdResponse = itemModifierGroups.get("modifierGroupId");

            if (itemIdResponse.equals(itemId) && modifierGroupIdResponse.equals(modifierGroupId))
            {
                itemWithModifierGroup = true;
                break;
            }
        }
        Assert.assertTrue(itemWithModifier, "Modifier is not linked with item");
        Assert.assertTrue(itemWithModifierGroup, "Modifier Group is not linked with item");
    }

    @Test
    public void createEmployeeAndLoginWithPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        int businessId = Integer.parseInt(ConfigManager.getProperty("businessId"));
        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "createEmployeeWithPermissionsPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String phoneNumber = "+1212" + faker.number().digits(7);

        ObjectNode employeeNode = (ObjectNode) jsonNode.get("createEmployeeData").get("employee");
        employeeNode.put("firstName", firstName);
        employeeNode.put("lastName", lastName);
        employeeNode.put("email", email);
        employeeNode.put("phoneNumber", phoneNumber);

        jsonNode.put("restaurantId", restaurantId);
        ((ObjectNode) jsonNode.get("createEmployeeData")).put("restaurantId", restaurantId);
        ((ObjectNode) jsonNode.get("createEmployeeData")).put("businessId", businessId);


        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        response = EmployeeEndpoints.createEmployeeWithPermission(updatedPayload, "dashboard");
        response.then().log().all();

        jsonPath = response.jsonPath();
        String employeeId = jsonPath.getString("response.data.creationStatus.UserSub");

        response = EmployeeEndpoints.getEmployeeList("dashboard", restaurantId);
        response.then().log().all();

        jsonPath = response.jsonPath();
        List<Map<String, ?>> employees = jsonPath.getList("response.data.employees");

        Integer posPin = null;

        for (Map<String, ?> employee : employees)
        {
            if (employee.get("id").equals(employeeId))
            {
                posPin = (Integer) employee.get("posPin");
                break;
            }
        }

        if (posPin == null)
        {
            System.out.println("Pos Pin is not available for the employee.");
            return;
        }

        System.out.println("Employee Pin: " + posPin);

        Authentication authenticationPayload = new Authentication();
        authenticationPayload.setRestaurantId(Integer.parseInt(ConfigManager.getProperty("restaurantId")));
        authenticationPayload.setPosPin(posPin);
        authenticationPayload.setDeviceId(ConfigManager.getProperty("deviceId"));
        authenticationPayload.setLogoutNeeded(true);
        Response loginResponse = AuthenticationEndpoints.loginWithPos(authenticationPayload);
        loginResponse.then().log().all();

        jsonPath = loginResponse.jsonPath();
        String message = jsonPath.getString("message");
        Assert.assertEquals(message, "user not activated", "User is activated");
    }

    @Test
    public void createEmployeeActivateAndLoginWithPos() {
        int restaurantId = Integer.parseInt(ConfigManager.getProperty("restaurantId"));
        int businessId = Integer.parseInt(ConfigManager.getProperty("businessId"));
        String deviceId = ConfigManager.getProperty("deviceId");

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "createEmployeeWithPermissionsPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String phoneNumber = "+1212" + faker.number().digits(7);

        ObjectNode employeeNode = (ObjectNode) jsonNode.get("createEmployeeData").get("employee");
        employeeNode.put("firstName", firstName);
        employeeNode.put("lastName", lastName);
        employeeNode.put("email", email);
        employeeNode.put("phoneNumber", phoneNumber);

        jsonNode.put("restaurantId", restaurantId);
        ((ObjectNode) jsonNode.get("createEmployeeData")).put("restaurantId", restaurantId);
        ((ObjectNode) jsonNode.get("createEmployeeData")).put("businessId", businessId);


        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        System.out.println("Create Employee Response: ");
        response = EmployeeEndpoints.createEmployeeWithPermission(updatedPayload, "dashboard");
        response.then().log().all();

        jsonPath = response.jsonPath();
        String employeeId = jsonPath.getString("response.data.creationStatus.UserSub");
        String username = jsonPath.getString("response.data.username");

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "passwordLessLoginPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        jsonNode.put("username", username);

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        System.out.println("Password Less Login Response: ");
        response = AuthenticationEndpoints.passwordlessLogin(updatedPayload);
        response.then().log().all();

        jsonPath = response.jsonPath();
        String session = jsonPath.getString("data.Session");

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "passwordLessVerifyPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        jsonNode.put("username", username);
        jsonNode.put("session", session);

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        System.out.println("Password Less Verify Response: ");
        response = AuthenticationEndpoints.passwordlessVerify(updatedPayload);
        response.then().log().all();

        jsonPath = response.jsonPath();
        String accessToken = jsonPath.getString("data.authChallengeResponse.AuthenticationResult.AccessToken");
        String idToken = jsonPath.getString("data.authChallengeResponse.AuthenticationResult.IdToken");

        System.out.println("Access Token: " + accessToken);
        System.out.println("ID Token: " + idToken);

        try
        {
            jsonNode = (ObjectNode) mapper.readTree(Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "payload" + File.separator + "setUserPasswordPayload.json")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        jsonNode.put("username", username);
        System.out.println("Set User Password Payload: " + jsonNode.toString());

        try
        {
            updatedPayload = mapper.writeValueAsString(jsonNode);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }

        System.out.println("Set User Password Response: ");
        response = AuthenticationEndpoints.setUserPassword(updatedPayload, accessToken, idToken);
        response.then().log().all();

        response = EmployeeEndpoints.getEmployeeList("dashboard", restaurantId);
        response.then().log().all();

        jsonPath = response.jsonPath();
        List<Map<String, ?>> employees = jsonPath.getList("response.data.employees");

        Integer posPin = null;

        for (Map<String, ?> employee : employees)
        {
            if (employee.get("id").equals(employeeId))
            {
                posPin = (Integer) employee.get("posPin");
                break;
            }
        }

        if (posPin == null)
        {
            System.out.println("Pos Pin is not available for the employee.");
            return;
        }

        System.out.println("Employee Pin: " + posPin);

        Authentication authenticationPayload = new Authentication();
        authenticationPayload.setRestaurantId(Integer.parseInt(ConfigManager.getProperty("restaurantId")));
        authenticationPayload.setPosPin(posPin);
        authenticationPayload.setDeviceId(ConfigManager.getProperty("deviceId"));
        authenticationPayload.setLogoutNeeded(true);
        Response loginResponse = AuthenticationEndpoints.loginWithPos(authenticationPayload);
        loginResponse.then().log().all();

        jsonPath = loginResponse.jsonPath();
        String message = jsonPath.getString("message");
        //Assert.assertEquals(message, "user not activated", "User is activated");
    }
}

