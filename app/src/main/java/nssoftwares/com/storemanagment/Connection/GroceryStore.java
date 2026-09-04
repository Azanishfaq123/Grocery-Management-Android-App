package nssoftwares.com.storemanagment.Connection;

public class GroceryStore {
    private String name;
    private String logoUrl; // URL of the logo
    private String websiteUrl; // URL of the grocery store website

    public GroceryStore(String name, String logoUrl, String websiteUrl) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.websiteUrl = websiteUrl;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }
}

