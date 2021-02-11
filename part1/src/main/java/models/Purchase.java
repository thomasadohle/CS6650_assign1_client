package models;

import java.util.List;

public class Purchase {
    private List<PurchaseItem> items;

    public void setItems(List<PurchaseItem> items){
        this.items = items;
    }

    public List<PurchaseItem> getItems(){return this.items;}
}
