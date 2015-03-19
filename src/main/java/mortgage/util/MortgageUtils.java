package mortgage.util;

/**
 * Created by vargar2 on 29-Dec-14.
 */
public class MortgageUtils {

    public static double monthlyRateForYearly(double rate){
        double result = Math.pow((1 + rate/100.0),(1.0 / 12.0)) - 1;
//        double result = rate/1200.0;
        return result;
    }

    public static double quarterlyRateForYearly(double rate){
//        double result = Math.pow((1 + rate/100.0),(1.0 / 3.0)) - 1;
        double result = rate/400;
        return result;
    }

    public static double yearlyRate(double rate){
        return rate/100.0;
    }
}
