package structs;

import org.apache.commons.lang3.ArrayUtils;

public class Payment {
    private Integer value;
    private String id;

    public int getValue() {
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

    /**
     * returns the payment as a byte array. since the legal values of amount are from 1-10, a single byte (the first)
     * is enough, and since the id is 10 chars at most the array returned is no longer then 11 bytes.
     * @return byte[] representing the payment. first byte is amount, the rest is the id where is char is a byte.
     * i.e result is [amount, s, o, m, e, i, d] for example.
     */
    public byte[] toBytes(){
        byte[] valueByteList = new byte[]{value.byteValue()};
        return ArrayUtils.addAll(valueByteList, id.getBytes());
    }
}
