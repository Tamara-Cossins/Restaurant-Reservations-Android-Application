package com.example.docksidedelight;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditBookingFragment extends Fragment {

    private Button date_button, update_booking_btn, delete_booking_btn;
    private Spinner spinnerTableSize, spinnerSeatingArea, spinnerMeal;

    private String selectedDate, selectedTableSize, selectedMealtime, selectedSeatingArea;
    private ImageView seatingLayout;
    private TextView availableSeats, available_seats_text;

    private SharedViewModel shared_model;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    // Initialises shareviewmodel for the fragment and observe changes to the selectedBooking
    // Automatically updates editbooking UI by updateUIwithbooking when booking data changes
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {super.onActivityCreated(savedInstanceState);
        shared_model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        shared_model.getSelectedBooking().observe(getViewLifecycleOwner(), this::updateUIWithBooking);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fragment inflation
        View view = inflater.inflate(R.layout.fragment_edit_booking, container, false);

        date_button = view.findViewById(R.id.date_picker_button);
        update_booking_btn = view.findViewById(R.id.update_booking_button);
        delete_booking_btn = view.findViewById(R.id.delete_booking_button);
        spinnerTableSize = view.findViewById(R.id.tableSize_spinner);
        spinnerMeal = view.findViewById(R.id.meal_spinner);
        spinnerSeatingArea = view.findViewById(R.id.seatingArea_spinner);
        seatingLayout = view.findViewById(R.id.seatingLayout);
        availableSeats = view.findViewById(R.id.available_seats);
        available_seats_text = view.findViewById(R.id.available_seats_text);


        // Checks push notif preferences
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Calls spinner initialiser methods from BookingUtilities
        BookingUtilities.initialiseMealSpinner(getActivity(), spinnerMeal);
        BookingUtilities.initialiseTableSizeSpinner(getActivity(), spinnerTableSize);
        BookingUtilities.initialiseSeatingAreaSpinner(getActivity(), spinnerSeatingArea, seatingLayout);

        // Method for when a spinner has a selected value- the value is passed into corresponding variable
        spinnerTableSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTableSize = parent.getItemAtPosition(position).toString().split(" ")[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerMeal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMealtime = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


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

        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingUtilities.openDialog(getActivity(), date_button, dateFormat);
            }
        });

        // When delete booking is selected, alert dialog is invoked to get user to confirm their decision
        delete_booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlertDialog();
            }
        });


        update_booking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReservationToApi();
            }
        });

        return view;
    }

    private void updateUIWithBooking(Booking booking) {
        // Populates date spinner with chosen date
        selectedDate = booking.getDate();
        date_button.setText(selectedDate);

        // Populate spinners with selected booking details
        updateSpinnerSelection(spinnerTableSize, R.array.tableSize_values, String.valueOf(booking.getTableSize()));
        updateSpinnerSelection(spinnerMeal, R.array.mealTime_values, booking.getMeal());
        updateSpinnerSelection(spinnerSeatingArea, R.array.seatingArea_values, booking.getSeatingArea());
    }


    private void updateSpinnerSelection(Spinner spinner, int arrayResId, String valueToSelect) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int position = adapter.getPosition(valueToSelect);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }


    // Method to select the corresponding drawable seating layout image depending on what is selected in seating area spinner
    // Calls fetchAllBookings() method which loops through all bookings on API, identifies those on the same selected day and thus calculates available seats
    private void updateSeatingAreaImage(String seatingArea) {

        // Selected values in spinners are collected to calculate bookings w/ same preferences in fetchAllBookings()
        selectedTableSize = spinnerTableSize.getSelectedItem().toString().split(" ")[0];
        selectedSeatingArea = spinnerSeatingArea.getSelectedItem().toString();
        selectedMealtime = spinnerMeal.getSelectedItem().toString();
        selectedDate = date_button.getText().toString();

        // Depending on what seatingArea is chosen, the corresponding img is displayed in the imageview.
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

    // Method to calculate the available seats w/ preferences selected
    public void fetchFilteredBookings() {

        if (!selectedTableSize.equals("Select Table Size") || !selectedSeatingArea.equals("Select Seating Area") || !selectedMealtime.equals("Select Mealtime") || selectedDate.equals("Select Date")) {

            selectedTableSize = spinnerTableSize.getSelectedItem().toString().split(" ")[0];
            selectedSeatingArea = spinnerSeatingArea.getSelectedItem().toString();
            selectedMealtime = spinnerMeal.getSelectedItem().toString();
            selectedDate = date_button.getText().toString();

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
                            if (selectedDate.equals(date) && selectedMealtime.equals(mealtime) && selectedSeatingArea.equals(area)) {
                                String tableSizeString = booking.getString("tableSize");
                                int tableSize = Integer.parseInt(tableSizeString.split(" ")[0]);
                                bookedSeatsTotal += tableSize;
                            }
                        }
                        // Process the total booked seats and update UI
                        int totalAvailableSeats = 15 - bookedSeatsTotal;
                        available_seats_text.setVisibility(View.VISIBLE);
                        availableSeats.setText(totalAvailableSeats + " available seats");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Handle JSON parsing error
                    }
                }
            };

            Response.ErrorListener onErrorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error response
                    Toast.makeText(getActivity(), "Error fetching bookings", Toast.LENGTH_SHORT).show();
                }
            };

            // Call APIController's method
            APIController.fetchAPIBookings(getActivity(), onResponse, onErrorListener);
        }
    }

    // Alert dialog to confirm users decision of cancelling a booking
    private void cancelAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requestToCancel();
                    }
                })
                .setNegativeButton(android.R.string.no, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // Cancelling can only occur if booking is at least 24 hours away
    private void requestToCancel() {
        Booking selectedBooking = shared_model.getSelectedBooking().getValue();

        try {
            String bookingDateStr = selectedBooking.getDate();
            Date bookingDate = dateFormat.parse(bookingDateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date oneDayAhead = calendar.getTime();

            if (bookingDate.after(oneDayAhead)) {
                // Booking is more than 24 hours away, proceed to cancel
                APIController.cancelBooking(getActivity(), selectedBooking.getId(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getActivity(), "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                        makeNotification();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                        makeNotification();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Booking cannot be cancelled within 24 hours", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error parsing booking date", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateReservationToApi() {
        // Get the selected booking from the ViewModel
        Booking selectedBooking = shared_model.getSelectedBooking().getValue();

        // Extract data from spinners and other inputs
        selectedTableSize = spinnerTableSize.getSelectedItem().toString();
        selectedMealtime = spinnerMeal.getSelectedItem().toString();
        selectedSeatingArea = spinnerSeatingArea.getSelectedItem().toString();
        selectedDate = date_button.getText().toString();

        // Call APIController's update method
        // Validate input data
        if (selectedDate == null || selectedTableSize.equals("Select Table Size") ||
                selectedMealtime.equals("Select Mealtime") || selectedSeatingArea.equals("Select Seating Area")) {
            int duration = Toast.LENGTH_SHORT;
            CharSequence text = "Please input all mandatory booking details";
            Toast toast = Toast.makeText(getActivity(), text, duration);
            toast.show();
            return;
        }

        Booking updatedBooking = new Booking(
                selectedBooking.getId(),
                selectedBooking.getCustomerName(),
                selectedBooking.getCustomerPhoneNumber(),
                selectedMealtime,
                selectedSeatingArea,
                Integer.parseInt(selectedTableSize.split(" ")[0]),
                selectedDate
        );

        Response.Listener<JSONObject> onResponse = response -> {
            Toast.makeText(getActivity(), "Booking update complete!", Toast.LENGTH_SHORT).show();
            makeNotification();
        };

        Response.ErrorListener onErrorListener = error -> {
            Toast.makeText(getActivity(), "Booking update unsuccessful. Please try again.", Toast.LENGTH_SHORT).show();
        };

        // Call APIController's update method
        APIController.updateBooking(getActivity(), updatedBooking, onResponse, onErrorListener);
    }

    // Push notification for when booking is updated
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
                .setContentTitle("Booking Cancelled")
                .setContentText("The confirmation of cancellation has been sent your way.")
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