package mortgage.service;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vargar2 on 29-Dec-14.
 */
@Component
public class CalculationCache {
    private Map<Double, Double> monthlyRates = new HashMap<>();
    private Map<Double, Double> quarterlyRates = new HashMap<>();

    public boolean monthlyContains(Double rate){
        return monthlyRates.containsKey(rate);
    }

    public boolean quarterlyContains(Double rate){
        return quarterlyRates.containsKey(rate);
    }

    public void addMonthly(Double rate, Double monthly){
        if (!monthlyContains(rate)){
            monthlyRates.put(rate,monthly);
        }
    }

    public void addQuarterly(Double rate, Double monthly){
        if (!quarterlyContains(rate)){
            quarterlyRates.put(rate,monthly);
        }
    }

    public Double getMonthlyFor(Double rate){
        return monthlyRates.get(rate);
    }

    public Double getQuarterlyFor(Double rate){
        return quarterlyRates.get(rate);
    }

}
