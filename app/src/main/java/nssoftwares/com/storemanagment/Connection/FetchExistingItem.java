package nssoftwares.com.storemanagment.Connection;

public class FetchExistingItem {

    private String itemCode;
    private String itemName;
    private int minQty;
    private String barcode;

    // Constructor
    public FetchExistingItem(String itemCode, String itemName, int minQty,String barcode) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.minQty = minQty;
        this.barcode = barcode;
    }

    // Getters
    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }
    public String getBarcode() {
        return barcode;
    }
    public int getMinQty() {
        return minQty;


    }
}
