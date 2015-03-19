package mortgage.domain;

/**
 * Created by vargar2 on 14-Mar-15.
 */
public enum ExtraPaymentOptions {
    LOWER_PAYMENT_KEEP_YEARS(0),
    LOWER_YEARS_KEEP_PAYMENT(1);

    private int numRepresentation;

    private ExtraPaymentOptions(int num){
        this.numRepresentation = num;
    }

    public static ExtraPaymentOptions fromInteger(int x) {
        switch(x) {
            case 0:
                return ExtraPaymentOptions.LOWER_PAYMENT_KEEP_YEARS;
            case 1:
                return ExtraPaymentOptions.LOWER_YEARS_KEEP_PAYMENT;
        }
        return null;
    }

}
