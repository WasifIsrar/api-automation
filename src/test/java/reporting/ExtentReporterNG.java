package reporting;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReporterNG {
	static ExtentReports extent;
	
	public static synchronized ExtentReports getReporterObject() {
		if(extent==null) {
		String path=System.getProperty("user.dir")+File.separator+"reports"+File.separator+"report("+getCurrentTimestamp()+")"+".html";
		ExtentSparkReporter reporter=new ExtentSparkReporter(path);
		reporter.config().setReportName("AIO Api Automated Test Results");
		reporter.config().setDocumentTitle("Test Results");
		extent=new ExtentReports();
		extent.attachReporter(reporter);
		}
		return extent;
	}
	
	 private static String getCurrentTimestamp() {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	        return LocalDateTime.now().format(formatter);
	    }
}
