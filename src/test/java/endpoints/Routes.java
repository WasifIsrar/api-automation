package endpoints;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

public class Routes {

    private static String baseUrl;

    @Parameters({"environment", "restaurantId", "businessId", "posPin", "deviceId", "employeeId", "username"})
    @BeforeTest
    public void setBasePath(String environment, String restaurantId, String businessId, String pin, String deviceId, String employeeId, String username) {
        Properties properties = new Properties();
        try {
            FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "java" + File.separator + "resources" + File.separator + "config.properties");
            properties.setProperty("restaurantId", restaurantId);
            properties.setProperty("businessId", businessId);
            properties.setProperty("pin", pin);
            properties.setProperty("deviceId", deviceId);
            properties.setProperty("employeeId", employeeId);
            properties.setProperty("username", username);
            properties.store(fos, null);
            fos.close();
        } catch (Exception e) {
        }


        switch (environment) {
            case "internal":
                baseUrl = "https://..../api";
                break;
            case "qa":
                baseUrl = "https://..../api";
                break;
            case "uatv2":
                baseUrl = "https://..../api";
                break;
        }
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        System.out.println("Starting test: " + method.getName());
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        System.out.println("Finished test: " + result.getMethod().getMethodName());
    }

        public static class AuthenticationRoutes {
            public static final String loginPosUrl = baseUrl + "/authentication/login-with-pos";
            public static final String loginmPosUrl = baseUrl + "/authentication/login-with-mpos";
            public static final String userLoginUrl = baseUrl + "/authentication/user-login";
            public static final String passwordlessLoginUrl = baseUrl + "/authentication/passwordless-login";
            public static final String passwordlessVerifyUrl = baseUrl + "/authentication/passwordless-verify";
            public static final String setUserPasswordUrl = baseUrl + "/authentication/set-user-pwd";
        }

        public static class TimelogRoutes {
            public static final String clockinUrl = baseUrl + "/employee/timeLog/clock-in";
        }

        public static class TableRoutes {
            public static final String allTablesUrl = baseUrl + "/table/all-tables";
        }

        public static class TicketRoutes {
            public static final String createIdUrl = baseUrl + "/ticket/createId";
            public static final String updateUrl = baseUrl + "/ticket/update/{ticketId}";
            public static final String updateStatusUrl = baseUrl + "/ticket/update-status/{ticketId}";
            public static final String getAllOrdersUrl = baseUrl + "/ticket/get/allorders";
            public static final String getUrl = baseUrl + "/ticket/{ticketId}";
            public static final String voidOrderUrl = baseUrl + "/ticket/voidorder";
        }

        public static class MenuRoutes {
            public static final String createMenuUrl = baseUrl + "/menu/create";
            public static final String getMenuUrl = baseUrl + "/menu/pos/{restaurantId}";
        }

        public static class ItemRoutes {
            public static final String createItemUrl = baseUrl + "/item/create";
            public static final String getItemsUrl = baseUrl + "/item/list";
        }

        public static class CategoryRoutes {
            public static final String createCategoryUrl = baseUrl + "/category/create";
            public static final String getCategoryListUrl = baseUrl + "/category/list";
        }

        public static class ModifierOptionRoutes {
            public static final String createModifierOptionUrl = baseUrl + "/modifier-option/create";
            public static final String getModifierOptionUrl = baseUrl + "/modifier-option/list";
        }

        public static class ModifierRoutes {
            public static final String createModifierUrl = baseUrl + "/modifier/create";
            public static final String getModifierUrl = baseUrl + "/modifier/list";
        }

        public static class ModifierGroupRoutes {
            public static final String createModifierGroupUrl = baseUrl + "/modifier-group/create";
            public static final String getModifierGroupUrl = baseUrl + "/modifier-group/list";
        }

        public static class MiscRoutes {
            public static final String clearCacheUrl = baseUrl + "/clearcache";
        }

        public static class ServiceChargeRoutes {
            public static final String createServiceChargeUrl = baseUrl + "/service-charge/create";
        }

        public static class SalesTaxRoutes {
            public static final String createSalesTaxUrl = baseUrl + "/sales-tax/create";
        }

        public static class DiscountRoutes {
            public static final String createDiscountUrl = baseUrl + "/discount/create";
            public static final String getDiscountList = baseUrl + "/discount/list";
        }

        public static class Ticketv2Routes {
            public static final String getTicketsUrl = baseUrl + "/v2/ticket";
        }

        public static class PaymentRoutes {
            public static final String createCalculationUrl = baseUrl + "/payment/calculation/{ticketId}";
            public static final String getTicketUrl = baseUrl + "/payment/ticket/{ticketId}";
            public static final String createPaymentUrl = baseUrl + "/payment/{ticketId}";
            public static final String kioskCalculationUrl = baseUrl + "/payment/calculation/{id}";
        }

        public static class MomDashboardRoutes {
            public static final String getReportsUrl = baseUrl + "/mom-dashboard/get-reports-staff";
        }

        public static class ReportsRoutes {
            public static final String getCompsAndVoidsUrl = baseUrl + "/reports/overview/compsnvoids/Daily";
        }

        public static class CartRoutes {
            public static final String createKioskCartUrl = baseUrl + "/cart/createKioskCart";
        }

        public static class RestaurantRoutes {
            public static final String kioskOnboardUrl = baseUrl + "/restaurant/kiosk/onboard";
            public static final String getAllPos = baseUrl + "/restaurant/pos/getall";
            public static final String PosOnboardUrl = baseUrl + "/restaurant/pos/onboard";
        }

        public static class CashDrawerRoutes {
            public static final String createCashDrawer = baseUrl + "/cash-drawer/create";
            public static final String getAllCashDrawer = baseUrl + "/cash-drawer/all";
        }

        public static class TenderRoutes {
            public static final String createTender = baseUrl + "/tender/create";
            public static final String getActiveTenders = baseUrl + "/tender/get-active-tenders";
            public static final String getTenderList = baseUrl + "/tender/list";
        }

        public static class EmployeeRoutes {
            public static final String createEmployeeWithPermissionUrl = baseUrl + "/employee/create-employee-with-permission";
            public static final String createEmployee = baseUrl + "/employee/create";
            public static final String getEmployeeListUrl = baseUrl + "/employee/list";
        }

        public static class DeviceRoutes {
            public static final String createDeviceUrl = baseUrl + "/device/create";
            public static final String getDeviceListUrl = baseUrl + "/device/list";
        }
    }
