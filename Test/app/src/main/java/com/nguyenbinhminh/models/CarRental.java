package com.nguyenbinhminh.models;

public class CarRental {
    int rentalCode;
    String carName;
    double rentalPrice;
    byte[] carImv;
    String rentalLocation;
    String carType;

    public CarRental(int rentalCode, String carName, double rentalPrice, byte[] carImv, String rentalLocation, String carType) {
        this.rentalCode = rentalCode;
        this.carName = carName;
        this.rentalPrice = rentalPrice;
        this.carImv = carImv;
        this.rentalLocation = rentalLocation;
        this.carType = carType;
    }

    public int getRentalCode() {
        return rentalCode;
    }

    public void setRentalCode(int rentalCode) {
        this.rentalCode = rentalCode;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(double rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public byte[] getCarImv() {
        return carImv;
    }

    public void setCarImv(byte[] carImv) {
        this.carImv = carImv;
    }

    public String getRentalLocation() {
        return rentalLocation;
    }

    public void setRentalLocation(String rentalLocation) {
        this.rentalLocation = rentalLocation;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }
}
