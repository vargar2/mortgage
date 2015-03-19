package mortgage.domain;
import lombok.*;

/**
 * Created by vargar2 on 30-Dec-14.
 */
@Data
public class MonthlyData implements Comparable<MonthlyData>{
    private int month;
    private int year;
    private PaymentStructure paymentStructure;
    private double leftToPay;
    private boolean anniversary = false;
    private double interestTotal;

    public MonthlyData( int month, int year) {
        this.month = month;
        this.year = year;
    }


    @Override
    public int compareTo(MonthlyData otherMonthlyData) {
        if (year > otherMonthlyData.year) {
            return 1;
        }
        if (year < otherMonthlyData.year) {
            return -1;
        }
        if (year == otherMonthlyData.year) {
            if (month > otherMonthlyData.month) {
                return 1;
            }
            if (month < otherMonthlyData.month) {
                return -1;
            }
            if (month == otherMonthlyData.month) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object otherMonthlyData) {
        if (this == otherMonthlyData) return true;
        if (otherMonthlyData == null || getClass() != otherMonthlyData.getClass()) return false;

        MonthlyData that = (MonthlyData) otherMonthlyData;

        if (month != that.month) return false;
        if (year != that.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = month;
        result = 31 * result + year;
        return result;
    }
}
