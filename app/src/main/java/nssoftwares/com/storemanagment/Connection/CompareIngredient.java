package nssoftwares.com.storemanagment.Connection;

public class CompareIngredient {
    private String itemName;
    private int quantity;
    private String barcode;
    private int availQty;

    public CompareIngredient(String itemName, int quantity, String barcode, int availQty) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.barcode = barcode;
        this.availQty =availQty;
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

    public int getAvailQty() {
        return availQty;
    }

}
