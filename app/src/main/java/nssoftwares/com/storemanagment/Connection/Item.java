package nssoftwares.com.storemanagment.Connection;

public class Item {
    private String itemCode;
    private String itemName;
    private int quantity;
    private String barcode; // New field for barcode

    public Item(String itemCode, String itemName, int quantity, String barcode) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.barcode = barcode; // Initialize barcode
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getBarcode() { // New getter for barcode
        return barcode;
    }
}
