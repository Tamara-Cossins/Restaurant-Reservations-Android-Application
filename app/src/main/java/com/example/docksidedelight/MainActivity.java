package com.example.docksidedelight;

import android.Manifest;


import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private Button login_button;
    private Button signup_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // sign_up button navigates to the CreateAccountActivity
        signup_button = findViewById(R.id.signup_button);
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        // The log_in button checks whether the entered credentials match any profiles in local storage
        EditText emailEditText = findViewById(R.id.login_email);
        EditText passwordEditText = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    if (validateLogIn(email, password)) {
                        // If email and password match local storage, proceed to log in to DashboardActivity
                        Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                        startActivity(intent);
                    } else {
                        // Show error message if email and password don't match
                        CharSequence text = "Invalid Email or Password";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(MainActivity.this, text, duration);
                        toast.show();
                    }
                } else {
                    // If the email or password EditText fields are empty, error message is shown
                    CharSequence text = "Please input Email and Password";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(MainActivity.this, text, duration);
                    toast.show();
                }
            }
        });
    }

    // Method to check if inputted email and password match in local storage. If so, details are stored in singleton User class
    private boolean validateLogIn(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        String userDetails = sharedPreferences.getString(email, null);

        // If the userDetails in sharedprefs associated with the logged in email
        if (userDetails != null) {
            Gson gson = new Gson();
            User user = gson.fromJson(userDetails, User.class);

            // If the user and password match what is in a sharedprefs pair, continue
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                // Set the logged-in user details in the User Singleton for global use
                User.getInstance().setUserDetails(user.getEmail(), user.getPassword(), user.getCustomerName(), user.getCustomerPhoneNumber());
                return true;
            }
        }
        return false;
    }
}