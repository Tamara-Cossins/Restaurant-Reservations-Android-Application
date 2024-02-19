package com.example.docksidedelight;

import java.util.ArrayList;
import java.util.List;

public class User {
    private static User instance;
    private String email;
    private String password;
    private String customerName;
    private String customerPhoneNumber;
    private List<Meal> favouriteMeals;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void addFavourite(Meal meal){
        if (!favouriteMeals.contains(meal)) {
            favouriteMeals.add(meal);
        }
    }

    public void removeFavourite(Meal meal) {
        favouriteMeals.remove(meal);
    }

    public List<Meal> getFavouriteMeals() {
        return favouriteMeals;
    }


    private User() {
        favouriteMeals = new ArrayList<>();
    }

    public static void resetInstance() {
        instance = null;
    }

    // Lazy instantiation using double-checked locking
    public static User getInstance() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User();
                }
            }
        }
        return instance;
    }

    // Method to set user details
    public void setUserDetails(String email, String password, String customerName, String customerPhoneNumber) {
        this.email = email;
        this.password = password;
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
    }
}