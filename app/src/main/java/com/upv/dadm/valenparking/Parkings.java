package com.upv.dadm.valenparking;

public class Parkings {
    public String name;
    public String calle;

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
}

