package com.upv.dadm.valenparking;

public class Parkings {
    public String name;
    public String calle;
    private boolean isSelected = false;
    public float lat;
    public float lon;

    public Parkings(String name, String calle, float lat, float lon){
        this.name = name;
        this.calle = calle;
        this.lat = lat;
        this.lon = lon;
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

    public void setLat(float latitud){ lat = latitud; }

    public void setLon(float longitud){ lon = longitud; }

    public float getLat(){ return lat;}

    public float getLon(){ return lon;}

    public boolean isSelected() {
        return isSelected;
    }
}

