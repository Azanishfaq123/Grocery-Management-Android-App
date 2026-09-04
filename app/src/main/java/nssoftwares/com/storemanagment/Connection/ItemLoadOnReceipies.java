package nssoftwares.com.storemanagment.Connection;

public class ItemLoadOnReceipies {
    private String itemCode;
    private String itemName;
    private String barcode;

    public ItemLoadOnReceipies(String itemCode, String itemName, String barcode) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.barcode = barcode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getBarcode() {
        return barcode;
    }

    @Override
    public String toString() {
        return itemName;  // This will be shown in the spinner
    }
}
