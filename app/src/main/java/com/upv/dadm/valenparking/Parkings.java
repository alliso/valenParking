package com.upv.dadm.valenparking;

public class Parkings {
    public String name;
    public int free;

    public Parkings(String name, int free){
        this.name = name;
        this.free = free;
    }

    public Parkings(){ }

    public String getParkingName() {
        return name;
    }

    public void setParkingName(String ParkingName) {
        this.name = ParkingName;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }
}

