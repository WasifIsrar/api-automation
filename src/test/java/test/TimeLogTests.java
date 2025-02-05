package test;

import org.testng.annotations.Test;
import org.testng.Assert;

import endpoints.Routes;
import endpoints.TimelogEndpoints;
import io.restassured.response.Response;
import payload.TimeLog;

public class TimeLogTests extends Routes{
	
	@Test
	public void verifyClockIn() {
		TimeLog timelogPayload=new TimeLog();
		timelogPayload.setRoleId(6);
		Response response=TimelogEndpoints.clockin(timelogPayload);
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 201);
	}
}
