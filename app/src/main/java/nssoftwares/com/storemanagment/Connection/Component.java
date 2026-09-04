package nssoftwares.com.storemanagment.Connection;

public class Component {
    private String itemName;
    private int requiredQuantity;
    private String barcode;

    public Component(String itemName, int requiredQuantity, String barcode) {
        this.itemName = itemName;
        this.requiredQuantity = requiredQuantity;
        this.barcode = barcode;
    }

    public String getItemName() {
        return itemName;
    }

    public int getRequiredQuantity() {
        return requiredQuantity;
    }

    public String getBarcode() {
        return barcode;
    }
}
