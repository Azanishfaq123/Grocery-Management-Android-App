package nssoftwares.com.storemanagment.Connection;

public class Ingredient {
    private String itemName;
    private int quantity;
    private String barcode;

    public Ingredient(String itemName, int quantity, String barcode) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.barcode = barcode;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getBarcode() {
        return barcode;
    }
}
