package mortgage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by vargar2 on 14-Mar-15.
 */
//@Data
public class PaymentStructure implements Serializable {

    @Transient
    private static final DecimalFormat df = new DecimalFormat("#.00");

    private double interestPayment;
    private double principalPayment;
    private double monthlyPayment;
    private double extraPayment;

    public PaymentStructure(double interestPayment, double realPayment, double monthlyPayment) {
        this.interestPayment = interestPayment;
        this.principalPayment = realPayment;
        this.monthlyPayment = monthlyPayment;
    }

    public PaymentStructure() {
    }

    public String getInterestPayment() {
        return df.format(interestPayment);
    }

    public String getPrincipalPayment() {
        return df.format(principalPayment);
    }

    public String getMonthlyPayment() {
        return df.format(monthlyPayment);
    }

    public String getExtraPayment() {
        return df.format(extraPayment);
    }

    @JsonIgnore
    public Double getInterestPaymentFull() {
        return interestPayment;
    }

    @JsonIgnore
    public Double getPrincipalPaymentFull() {
        return principalPayment;
    }

    @JsonIgnore
    public Double getMonthlyPaymentFull() {
        return monthlyPayment;
    }

    @JsonIgnore
    public Double getExtraPaymentFull() {
        return extraPayment;
    }

    public void setExtraPayment(double extraPayment) {
        this.extraPayment = extraPayment;
    }

    public void setInterestPayment(double interestPayment) {
        this.interestPayment = interestPayment;
    }

    public void setPrincipalPayment(double principalPayment) {
        this.principalPayment = principalPayment;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }
}
