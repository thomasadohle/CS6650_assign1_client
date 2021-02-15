package models;

public class PurchaseItem {
    private String itemID;
    private int numberOfItems;

    public void setItemID(String itemID){
        this.itemID = itemID;
    }

    public void setNumberOfItems(int numberOfItems){
        this.numberOfItems = numberOfItems;
    }

    public String getItemID(){return this.itemID;}
    public int getNumberOfItems(){return this.numberOfItems;}
}
