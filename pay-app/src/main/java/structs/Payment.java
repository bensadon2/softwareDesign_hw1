package structs;

public class Payment {
    private int value;
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

    ////////////////////////////////
    // for persistent database storage
    ////////////////////////////////

    public Payment(byte[] paymentData) {
        // TODO: 20-May-18 implement
        throw new UnsupportedOperationException("not implemented");
    }

    public byte[] toBytes(){
        // TODO: 20-May-18 implement
        throw new UnsupportedOperationException("not implemented");
    }
}
