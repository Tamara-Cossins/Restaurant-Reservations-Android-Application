package com.example.docksidedelight;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BookingUtilities {
    // Class to handle repetitive methods for handling bookings, i.e volley requests


    // Method to retrieve the logged-in user's details through their unique email - all in sharedPreferences
    public static String[] customerDetailsFromLocalStorage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        // This retrieves the user email from local storage by using the shared preferences identifier
        String currentUserEmail = sharedPreferences.getString("loggedUserEmail", "");
        String userDetails = sharedPreferences.getString(currentUserEmail, "");

        if (!userDetails.isEmpty()) {
            Gson gson = new Gson();
            User user = gson.fromJson(userDetails, User.class);
            return new String[]{user.getCustomerName(), user.getCustomerPhoneNumber()};
        } else {
            return new String[]{"", ""};
        }
    }


    // Method for datepicker- displays calendar for user selection of the date. Only allows a selection made at least 7 days in advanced
    public static void openDialog(Context context, Button dateButton, SimpleDateFormat dateFormat) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7); // Restrict bookings to a minimum of one week in advance

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                Calendar sevenDaysAhead = Calendar.getInstance();
                sevenDaysAhead.add(Calendar.DAY_OF_MONTH, 7);

                if (selectedDate.before(sevenDaysAhead)) {
                    // Notify user if the selected date is less than 7 days ahead
                    Toast.makeText(context, "Cannot book less than 7 days in advance", Toast.LENGTH_SHORT).show();
                } else {
                    String formattedDate = dateFormat.format(selectedDate.getTime());
                    dateButton.setText(formattedDate); // Update the button text with the selected date
                }
            }
        }, currentYear, currentMonth, currentDay);

        dialog.getDatePicker().setMinDate(calendar.getTimeInMillis()); // Set minimum date in the DatePicker
        dialog.show();
    }

    // Method to initialise the values of tableSize spinner
    public static void initialiseTableSizeSpinner(Context context, Spinner spinnerTableSize) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.tableSize_values,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTableSize.setAdapter(adapter);
    }

    // Method to initialise mealTime spinner
    public static void initialiseMealSpinner(Context context, Spinner spinnerMeal) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.mealTime_values,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeal.setAdapter(adapter);
    }

    // Method to initialise seatingArea spinner
    public static void initialiseSeatingAreaSpinner(Context context, Spinner spinnerSeatingArea, ImageView seatingLayout) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.seatingArea_values,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeatingArea.setAdapter(adapter);

        // Once a new item is selected in the spinner, it calls the updateSeatingAreaImage method to show corresponding seating layout img
        spinnerSeatingArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSeatingArea = parent.getItemAtPosition(position).toString();
            }

            // If no item is selected in seatingArea spinner, the img remains invisible
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                seatingLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
}
