package com.example.docksidedelight;

public class Booking {
    // Class for holding and organising Booking details, including getters and setters
    // Handles both upcoming bookings and past bookings

    // Variables of booking class
    private int id, tableSize;
    private String customerName, customerPhoneNumber, meal, seatingArea, date;

    // Constructor to initialise booking objects
    public Booking (int id, String customerName, String customerPhoneNumber, String meal, String seatingArea, int tableSize, String date) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.meal = meal;
        this.seatingArea = seatingArea;
        this.tableSize = tableSize;
        this.date = date;
    }


    // Getter and setter methods for each booking detail variable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableSize() {
        return tableSize;
    }

    public void setTableSize(int tableSize) {
        this.tableSize = tableSize;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getSeatingArea() {
        return seatingArea;
    }

    public void setSeatingArea(String seatingArea) {
        this.seatingArea = seatingArea;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}

