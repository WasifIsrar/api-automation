package reporting;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class Listener implements ITestListener{
	private static final ExtentReports extent=ExtentReporterNG.getReporterObject();
    private ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

	@Override
	public void onTestStart(ITestResult result) {
		ExtentTest test=extent.createTest(result.getMethod().getMethodName());
		testThreadLocal.set(test);
	}
		
	@Override
	public void onTestSuccess(ITestResult result) {
		testThreadLocal.get().log(Status.PASS, "Test Passed");
	}
	
	@Override
	public void onTestFailure(ITestResult result) {
		testThreadLocal.get().fail(result.getThrowable());
	}
	
	@Override
	public void onTestSkipped(ITestResult result) {
		testThreadLocal.get().skip(result.getThrowable());
	}
	
	@Override
	public void onFinish(ITestContext context) {
		extent.flush();
	}
}
