package com.upv.dadm.valenparking;

public class Parkings {
    public String name;
    public String calle;
    private boolean isSelected = false;

    public Parkings(String name, String calle){
        this.name = name;
        this.calle = calle;
    }

    public Parkings(){ }

    public String getParkingName() {
        return name;
    }

    public void setParkingName(String ParkingName) {
        this.name = ParkingName;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }
}

