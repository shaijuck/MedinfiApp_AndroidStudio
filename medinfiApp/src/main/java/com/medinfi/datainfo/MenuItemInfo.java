package com.medinfi.datainfo;

public class MenuItemInfo {
    String itemName;
    private int menuIcon;

    public MenuItemInfo(String itemName){
	this.itemName = itemName;
    }
    public MenuItemInfo(String itemName,int menuIcon){
	this.itemName = itemName;
	this.menuIcon = menuIcon;
    }
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public int getMenuIcon() {
        return menuIcon;
    }
    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }
}
