package com.example.docksidedelight;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import com.google.gson.Gson;

public class BookingFragment extends Fragment {
    // Fragment which handles the creation of new bookings

    // XML components:
    private Button date_button, create_booking_btn;
    private Spinner spinnerTableSize, spinnerSeatingArea, spinnerMeal;
    private TextView availableSeats;
    private ImageView seatingLayout;

    // User's selected options from the spinners. Used to calculate available seats with these preferences
    private String userSelectedDate, selectedMealtime, selectedCapacity;

    // Variables used for calculating available seats, and if enough seats are available compared to tablesizespinner
    private int totalAvailableSeats = 0, selectedCapacityInt = 0;
    private String selectedSeatingArea;



    // Current date
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating the layout
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        // Find IDs of XML components
        date_button = view.findViewById(R.id.date_picker_button);
        create_booking_btn = view.findViewById(R.id.create_booking_button);
        spinnerTableSize = view.findViewById(R.id.tableSize_spinner);
        spinnerSeatingArea = view.findViewById(R.id.seatingArea_spinner);
        spinnerMeal = view.findViewById(R.id.meal_spinner);
        seatingLayout = view.findViewById(R.id.seatingLayout);
        availableSeats = view.findViewById(R.id.available_seats);

        // Initialise the spinners with their selectable values
        BookingUtilities.initialiseTableSizeSpinner(getActivity(), spinnerTableSize);
        BookingUtilities.initialiseMealSpinner(getActivity(), spinnerMeal);
        BookingUtilities.initialiseSeatingAreaSpinner(getActivity(), spinnerSeatingArea, seatingLayout);

        // When item is selected on spinnerTableSize, the string is passed into selectedCapacity variable
        spinnerTableSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCapacity = parent.getItemAtPosition(position).toString().split(" ")[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // When item is selected on spinnerMeal, the string is passed into selectedMealtime
        spinnerMeal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMealtime = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // When item is selected on spinnerSeatingArea, the string is passed into selectedSeatingArea and updateSeatingAreaImage is invoked
        spinnerSeatingArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSeatingArea = parent.getItemAtPosition(position).toString();
                updateSeatingAreaImage(selectedSeatingArea);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                seatingLayout.setVisibility(View.INVISIBLE);
            }
        });

        // Date button calls openDialog method for datePicker calendar
        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookingUtilities.openDialog(getActivity(), date_button, dateFormat);
            }
        });

        // Checks notification push permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }



        // "Create booking" button POSTs the selected data to the API by calling postResToAPI() method if enough seats available
        create_booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNotification();
                postReservationToApi();
            }
        });
        return view;
    }

    // Displays corresponding seatingArea image and calculates how many seats available
    private void updateSeatingAreaImage(String seatingArea) {

        // Selected values in spinners are collected to calculate bookings w/ same preferences in fetchAllBookings()
        selectedCapacity = spinnerTableSize.getSelectedItem().toString().split(" ")[0];
        selectedSeatingArea = spinnerSeatingArea.getSelectedItem().toString();
        selectedMealtime = spinnerMeal.getSelectedItem().toString();
        userSelectedDate = date_button.getText().toString();
        if (!selectedCapacity.equals("Select Table Size") || !selectedSeatingArea.equals("Select Seating Area") || !selectedMealtime.equals("Select Mealtime") || userSelectedDate.equals("Select Date")) {
            // Depending on what seatingArea is chosen, the corresponding img is displayed in the imageview. fetchFilteredBookings() is called
            switch (seatingArea) {
                case "Indoors: Seaside":
                    seatingLayout.setImageResource(R.drawable.indoorseaside_seating);
                    seatingLayout.setVisibility(View.VISIBLE);
                    fetchFilteredBookings();
                    break;
                case "Indoors: Garden-side":
                    seatingLayout.setImageResource(R.drawable.indoorgardenside_seating);
                    seatingLayout.setVisibility(View.VISIBLE);
                    fetchFilteredBookings();
                    break;
                case "Outdoors: Seaside":
                    seatingLayout.setImageResource(R.drawable.seaside_seating);
                    seatingLayout.setVisibility(View.VISIBLE);
                    fetchFilteredBookings();
                    break;
                case "Outdoors: Garden-side":
                    seatingLayout.setImageResource(R.drawable.gardenside_seating);
                    seatingLayout.setVisibility(View.VISIBLE);
                    fetchFilteredBookings();
                    break;
            }
        }
    }

        // Method to calculate the available seats w/ preferences selected and display this data in a textview
        public void fetchFilteredBookings() {

            // Only continues if all spinners have selected data
            if (!selectedCapacity.equals("Select Table Size") || !selectedSeatingArea.equals("Select Seating Area") || !selectedMealtime.equals("Select Mealtime") || !userSelectedDate.equals("Select Date")) {

                // Assigns selected data into variables
                // Splits the selected value for spinnerTableSize into just int (2 people = 2)
                selectedCapacity = spinnerTableSize.getSelectedItem().toString().split(" ")[0];
                selectedSeatingArea = spinnerSeatingArea.getSelectedItem().toString();
                selectedMealtime = spinnerMeal.getSelectedItem().toString();
                userSelectedDate = date_button.getText().toString();

                // Loops through bookings, utilises APIController fetch bookings and filters though who have same date and time
                Response.Listener<JSONArray> onResponse = new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int bookedSeatsTotal = 0;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject booking = response.getJSONObject(i);
                                String date = booking.getString("date");
                                String mealtime = booking.getString("meal");
                                String area = booking.getString("seatingArea");
                                if (userSelectedDate.equals(date) && selectedMealtime.equals(mealtime) && selectedSeatingArea.equals(area)) {
                                    String tableSizeString = booking.getString("tableSize");
                                    int tableSize = Integer.parseInt(tableSizeString.split(" ")[0]);
                                    bookedSeatsTotal = bookedSeatsTotal + tableSize;
                                }
                            }
                            // Process the total booked seats for this date and time, and update UI
                            totalAvailableSeats = 15 - bookedSeatsTotal;
                            selectedCapacityInt = Integer.parseInt(selectedCapacity);
                            if (totalAvailableSeats > 0) {
                                CharSequence text = "Seating availability confirmed!";
                                create_booking_btn.setVisibility(View.VISIBLE);
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(getActivity(), text, duration);
                                toast.show();
                            }
                            availableSeats.setText(totalAvailableSeats + " available seats");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("edit", "cannot process fetch");
                        }
                    }
                };

                Response.ErrorListener onErrorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CharSequence text = "Error fetching bookings";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getActivity(), text, duration);
                        toast.show();
                    }
                };

                // Call APIController's method
                APIController.fetchAPIBookings(getActivity(), onResponse, onErrorListener);
            }

            // If not all data is selected, toast indicates to comply
            else {
                CharSequence text = "Please input all mandatory booking details";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), text, duration);
                toast.show();
            }
        }


        // Method to POST localStorage details and user input of fragment_booking.xml
        private void postReservationToApi () {
            // Uses customerDetailsFromLocalStorage from APIController
            String[] customerDetails = BookingUtilities.customerDetailsFromLocalStorage(getActivity());
            String customerName = customerDetails[0];
            String customerPhoneNumber = customerDetails[1];

            // Assigns selected data into variables
            // Splits the selected value for spinnerTableSize into just int (2 people = 2)
            String tableSizeString = spinnerTableSize.getSelectedItem().toString().split(" ")[0];
            String seatingArea = spinnerSeatingArea.getSelectedItem().toString();
            String mealTime = spinnerMeal.getSelectedItem().toString();
            String date = date_button.getText().toString();

            // Ensures each spinner and calendar has selected values
            if (tableSizeString.equals("Select Table Size") || seatingArea.equals("Select Seating Area") || mealTime.equals("Select Mealtime") || date.equals("Select Date")) {
                CharSequence text = "Please input all mandatory booking details";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), text, duration);
                toast.show();
                return;
            }

            // Ensures enough seats are available for booking, if so continues to POST to API using postBooking method in APIController
            if (totalAvailableSeats >= selectedCapacityInt) {
                int tableSize = Integer.parseInt(tableSizeString);
                APIController.postBooking(getActivity(), customerName, customerPhoneNumber, mealTime, seatingArea, tableSize, date, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        CharSequence text = "Booking reservation complete!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getActivity(), text, duration);
                        toast.show();
                    }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CharSequence text = "Booking reservation unsuccessful. Please try again.";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getActivity(), text, duration);
                        toast.show();
                    }
                });
            }

            // If not enough seats available for booking, toast notifies user of this to elicit a change in date/time/tableSize
            else {
                CharSequence text = "Not enough seats are available for this date and time. Please choose another date/time.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), text, duration);
                toast.show();
            }
        }

    public void makeNotification() {
        String channelID = "CHANNEL_ID_NOTIFICATION";

        // Create a NotificationChannel, required for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), channelID)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Booking Confirmed")
                .setContentText("Booking details have been sent your way.")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(getContext(), NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("data", "Value to be passed");

        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelID, "CHANNEL_ID_NOTIFICATION", importance);
                notificationChannel.enableVibration(true);
                notificationChannel.setLightColor(Color.WHITE);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        notificationManager.notify(0, builder.build());
    }

}