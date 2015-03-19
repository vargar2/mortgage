package mortgage.service;

import mortgage.domain.ExtraPaymentOptions;
import mortgage.domain.MonthlyData;
import mortgage.domain.PaymentStructure;
import mortgage.util.MortgageUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.*;

import static mortgage.util.MortgageUtils.*;

/**
 * Created by vargar2 on 29-Dec-14.
 */

@RestController
@RequestMapping(value="/REST")

public class MortgageService {

    private static final Logger logger =  LoggerFactory.getLogger(MortgageService.class);

    private Double getMonthlyPaymentAmount(double interest, int years, double money, Integer minusPayedMonths){

        interest = MortgageUtils.monthlyRateForYearly(interest) ;
        int months = (years * 12) - minusPayedMonths;

        return money * ((interest * Math.pow((1+interest),months)) / (Math.pow (1+interest, months ) - 1));

    }
    public double logOfBase(double base, double num) {
        return Math.log(num) / Math.log(base);
    }

    private Double getMonthCount(double interest, int years, double money, Double moneyLeftToPay){
        double M = getMonthlyPaymentAmount(interest,years,money,0);
        double P = moneyLeftToPay;
        double i = MortgageUtils.monthlyRateForYearly(interest) ;

        double num = (-M)/(P*i - M);
        double base = 1+i;

        double monthCount = logOfBase(base, num);

        return monthCount;

    }

    private double calculateInterestPaymentMonthly(double interest, double moneyLeftToPay){
        interest = MortgageUtils.monthlyRateForYearly(interest);
        return moneyLeftToPay * interest;
    }

    private PaymentStructure getPaymentStructure(Double monthlyPayment, Double moneyLeftToPay, double interestRate){
        double interestPaymentMonthly = calculateInterestPaymentMonthly(interestRate, moneyLeftToPay);
        double principalPayment = monthlyPayment - interestPaymentMonthly;
        return new PaymentStructure(interestPaymentMonthly, principalPayment, monthlyPayment);
    }


    @RequestMapping(value="/calculateMortgage")
    public Set<MonthlyData> calculateMortgage(@RequestParam double interest,
                                              @RequestParam(value="money") double currentPrincipal,
                                              @RequestParam(value="originalMoney") double originalPrincipal,
                                              @RequestParam(value="years") int yearsToPay,
                                              @RequestParam(value="originalYears") int originalYearsToPay,
                                              @RequestParam(required=false, value="startYear") Integer startAtYear,
                                              @RequestParam(required=false, value="startMonth") Integer startAtMonth,
                                              @RequestParam(required=false, defaultValue = "0") Integer alreadyPayedMonths,
                                              @RequestParam(required=false, defaultValue = "0") Double interestAlreadyPayed,
                                              @RequestParam(required = false, defaultValue = "0") Integer extraPaymentOption){
        DateTime today = new DateTime();
        if (StringUtils.isEmpty(startAtYear)){
            startAtYear = today.getYear();
            logger.info("Current year " + startAtYear);
        }
        if (StringUtils.isEmpty(startAtMonth)){
            startAtMonth = today.getMonthOfYear();
            logger.info("Current month " + startAtMonth);
        }

        logger.info("Interest rate:{}, borrowed originalPrincipal:{}, years:{}",interest,currentPrincipal,yearsToPay);
        Set<MonthlyData> result = new TreeSet<MonthlyData>();
        if (extraPaymentOption == 0) {
            Double monthlyPayment = getMonthlyPaymentAmount(interest, yearsToPay, currentPrincipal, alreadyPayedMonths);
            Double moneyLeftToPay = currentPrincipal;
            double interestTotal = interestAlreadyPayed;
            MonthlyData data = null;
            for (Integer year = startAtYear; year <= startAtYear + yearsToPay; year++) {
                for (int month = 1; month <= 12; month++) {
                    if ((month < startAtMonth && year == startAtYear) || (month >= startAtMonth - alreadyPayedMonths && year == startAtYear + yearsToPay)) {
                        continue;
                    }

                    data = new MonthlyData(month, year);
                    if (month == startAtMonth && year != startAtYear) {
                        data.setAnniversary(true);
                    }

                    data.setPaymentStructure(getPaymentStructure(monthlyPayment, moneyLeftToPay, interest));
                    moneyLeftToPay = moneyLeftToPay - data.getPaymentStructure().getPrincipalPaymentFull();
                    data.setLeftToPay(moneyLeftToPay);
                    interestTotal += data.getPaymentStructure().getInterestPaymentFull();
                    data.setInterestTotal(interestTotal);
                    result.add(data);
                }
            }
            data.setLeftToPay(0.0);
        }else{//lower years, keep payment
            Double monthlyPayment = getMonthlyPaymentAmount(interest, originalYearsToPay, originalPrincipal, 0);
            Double moneyLeftToPay = currentPrincipal;
//            int monthsToPay = (int)Math.ceil(currentPrincipal / monthlyPayment);
            int monthsToPay = (int)Math.ceil(getMonthCount(interest,originalYearsToPay,originalPrincipal, moneyLeftToPay));
            int totalYears = (int) (monthsToPay/12);
            int additionalMonths = monthsToPay - (totalYears*12);
            double interestTotal = interestAlreadyPayed;
            MonthlyData data = null;
            int counter = 0;
            for (Integer year = startAtYear; year <= startAtYear + yearsToPay; year++) {
                for (int month = 1; month <= 12; month++) {
                    if ((month < startAtMonth && year == startAtYear) || (counter >= monthsToPay)) {
                        continue;
                    }
                    ++counter;
                    data = new MonthlyData(month, year);
                    if (month == startAtMonth && year != startAtYear) {
                        data.setAnniversary(true);
                    }

                    data.setPaymentStructure(getPaymentStructure(monthlyPayment, moneyLeftToPay, interest));
                    moneyLeftToPay = moneyLeftToPay - data.getPaymentStructure().getPrincipalPaymentFull();
                    data.setLeftToPay(moneyLeftToPay);
                    interestTotal += data.getPaymentStructure().getInterestPaymentFull();
                    data.setInterestTotal(interestTotal);
                    result.add(data);
                }
            }
            if (data!= null){
                data.setLeftToPay(0.0);
            }
        }
        return result;
    }

    @RequestMapping(value="/calculateMortgageWithBonus")
    public Set<MonthlyData> makeExtraPayment(@RequestParam( required=false) Set<MonthlyData> currentStatus,
                                             int yearOfExtraPayment, int monthOfExtraPayment, double extraPaymentAmount,
                                             double originalInterest, int originalYearsCount,
                                             int originalMoneyBorrowed, int originalYearStart, int originalMonthStart,
                                             @RequestParam(required = false, defaultValue = "0") Integer extraPaymentOption){
        int monthDifference = 0;
        monthDifference = Math.abs(monthOfExtraPayment - originalMonthStart) +1;
        currentStatus = calculateMortgage(originalInterest, originalMoneyBorrowed, originalMoneyBorrowed, originalYearsCount,originalYearsCount, originalYearStart, originalMonthStart, 0, 0.0, 0);

        Set<MonthlyData> result = new TreeSet<>();
        MonthlyData lastData = null;
        int nextYear = 0;
        int nextMonth = 0;
        for (MonthlyData currentData : currentStatus){
            result.add(currentData);
            lastData = currentData;
            if (lastData.getYear()==yearOfExtraPayment && lastData.getMonth()== monthOfExtraPayment){
                lastData.getPaymentStructure().setExtraPayment(extraPaymentAmount);
                lastData.setLeftToPay(lastData.getLeftToPay() - extraPaymentAmount);
                break;
            }
        }
        if (lastData.getMonth()==12){
            nextYear = lastData.getYear()+1;
            nextMonth = 1;
        }else{
            nextYear = lastData.getYear();
            nextMonth = lastData.getMonth() + 1;
        }
        Set<MonthlyData> monthlyDatasUpdated = null;
        ExtraPaymentOptions extraPaymentType = ExtraPaymentOptions.fromInteger(extraPaymentOption);
        if (ExtraPaymentOptions.LOWER_PAYMENT_KEEP_YEARS.equals(extraPaymentType)){
            monthlyDatasUpdated = calculateMortgage(originalInterest, lastData.getLeftToPay(), originalMoneyBorrowed, originalYearsCount - (nextYear - originalYearStart), originalYearsCount, nextYear, nextMonth, monthDifference, lastData.getInterestTotal(), extraPaymentOption);
        }else{
            monthlyDatasUpdated = calculateMortgage(originalInterest, lastData.getLeftToPay(), originalMoneyBorrowed, originalYearsCount - (nextYear - originalYearStart), originalYearsCount, nextYear, nextMonth, monthDifference, lastData.getInterestTotal(), extraPaymentOption);
        }
        result.addAll(monthlyDatasUpdated);
        return result;
    }

    @RequestMapping(value="/calculatePayment")
    public String showPayment(@RequestParam double interest, @RequestParam int years, @RequestParam double money){
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(getMonthlyPaymentAmount(interest, years, money, 0));
    }

    @RequestMapping(value="/showPaymentStructure")
    public PaymentStructure showPaymentStructure(@RequestParam double interest, @RequestParam double money, @RequestParam int years, @RequestParam(required = false) Double moneyLeftToPay){
        if (moneyLeftToPay == null){
            moneyLeftToPay = money;
        }
        PaymentStructure paymentStructure = getPaymentStructure(getMonthlyPaymentAmount(interest, years, money, 0), moneyLeftToPay, interest);
        return paymentStructure;
    }

    @RequestMapping(value="/interestRate")
    public Map<String,String> showInterestRates(@RequestParam double yearlyInterestRate){
        Map<String,String> rates = new HashMap<String,String>();

        Double monthlyAdjusted = monthlyRateForYearly(yearlyInterestRate);
        rates.put("monthly, p.m", (monthlyAdjusted * 100) + "%");

        Double quarterlyAdjusted = quarterlyRateForYearly(yearlyInterestRate);
        rates.put("quarterly", (quarterlyAdjusted * 100) + "%");

        yearlyInterestRate = MortgageUtils.yearlyRate(yearlyInterestRate);
        rates.put("yearly, p.a.", (yearlyInterestRate * 100) + "%");

        return rates;
    }
}
