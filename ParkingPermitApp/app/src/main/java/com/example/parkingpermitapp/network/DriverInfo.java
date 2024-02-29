package com.example.parkingpermitapp.network;

import com.google.gson.annotations.SerializedName;

public class DriverInfo {
    @SerializedName("IDnum")
    private int idNum;
    @SerializedName("Name")
    private String name;
    @SerializedName("PlateNum")
    private String plateNum;
    @SerializedName("PlateState")
    private String plateState;
    @SerializedName("Make")
    private String make;
    @SerializedName("Model")
    private String model;
    @SerializedName("PersonType")
    private String personType;
    @SerializedName("Email")
    private String email;

    public int getIdnum(){ return idNum; }
    public String getPlateNum(){ return plateNum; }

    public String toString(){
        return "PSU ID: " + idNum + "\n" +
                     "Name: " + name + "\n" +
                     "Type: " + personType + "\n" +
                     "License Plate: " + plateNum + "\n" +
                     "State: " +  plateState + "\n" +
                     "Make: " + make + "\n" +
                     "Model: " + model + "\n" +
                     "PSU Email: " + email;

    }
}
