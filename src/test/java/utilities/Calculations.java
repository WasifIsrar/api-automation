package utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Calculations {
	
	public static double roundHalfUp(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
 }
}
