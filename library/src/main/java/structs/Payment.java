package structs;

import java.io.*;

public class Payment implements Serializable {
    private Integer value;
    private String id;

    public Integer getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Payment(String id, int value) {
        this.value = value;
        this.id = id;
    }

    /////////////////////////////////////
    // for persistent database storage
    /////////////////////////////////////

    /**
     * converts a byte array to a struct. assumes the structure of the array is such that the first byte is the value
     * of the payment, and the rest is the id. also the array should be no bigger then 11 bytes.
     * see toBytes() doc for details.
     *
     * @param paymentData the payment data in the specified format.
     */
    public Payment(byte[] paymentData) {
        if (paymentData.length > 11) {
            System.out.println("Payment got byte[] with wrong size at construction");
            return;
        }
        value = (int) paymentData[0];
        id = new String(paymentData, 1, paymentData.length - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Payment payment = (Payment) o;

        if (!getValue().equals(payment.getValue())) return false;
        return getId().equals(payment.getId());
    }

    @Override
    public int hashCode() {
        int result = getValue().hashCode();
        result = 31 * result + getId().hashCode();
        return result;
    }
}
