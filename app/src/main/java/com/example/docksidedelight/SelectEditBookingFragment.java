package com.example.docksidedelight;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class SelectEditBookingFragment extends Fragment implements BookingAdapterCallback {
    // Fragment for the selection of which upcoming booking to edit or cancel

    private SharedViewModel sharedViewModel;
    private RecyclerView upcoming_bookings_recycler;
    private TextView select_booking_text;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fragment inflation
        View view = inflater.inflate(R.layout.fragment_select_edit_booking, container, false);

        select_booking_text = view.findViewById(R.id.select_booking_text);
        upcoming_bookings_recycler = view.findViewById(R.id.upcoming_bookings_recycler);
        upcoming_bookings_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch bookings asynchronously and set up recyclerview in callback
        fetchUserBookings();
        return view;
    }

    // When the edit/delete button is clicked, it sets the selected booking in sharedviewmodel
    // then navigates to the editbooking fragment
    @Override
    public void onEditorDeleteClicked(Booking booking) {
        sharedViewModel.selectBooking(booking);
        Fragment editFragment = new EditBookingFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, editFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    // Method that utilises BookingUtilities methods and APIController to fetch all API bookings and identifies those belonging to logged in user
    private void fetchUserBookings() {
        Response.Listener<JSONArray> onResponse = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Booking> filteredBookings = new ArrayList<>();
                String[] userDetails = BookingUtilities.customerDetailsFromLocalStorage(getActivity());
                String loggedCustomerName = userDetails[0];
                String loggedCustomerPhoneNumber = userDetails[1];

                // Method for filtering the bookings to only those for user
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject bookingJson = response.getJSONObject(i);

                        int id = bookingJson.getInt("id");
                        String customerName = bookingJson.getString("customerName");
                        String customerPhoneNumber = bookingJson.getString("customerPhoneNumber");
                        String meal = bookingJson.getString("meal");
                        String seatingArea = bookingJson.getString("seatingArea");
                        int tableSize = bookingJson.getInt("tableSize");
                        String date = bookingJson.getString("date");

                        if (customerName.equals(loggedCustomerName) && customerPhoneNumber.equals(loggedCustomerPhoneNumber)) {
                            Booking booking = new Booking(id, customerName, customerPhoneNumber, meal, seatingArea, tableSize, date);
                            filteredBookings.add(booking);
                        }
                    }

                    if (filteredBookings.isEmpty()) {
                        // Update TextView to show "No upcoming bookings."
                        select_booking_text.setText("No upcoming bookings.");
                    }
                    else {
                        // Update RecyclerView with user's upcoming bookings
                        BookingAdapter adapter = new BookingAdapter(filteredBookings, SelectEditBookingFragment.this);
                        upcoming_bookings_recycler.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener onErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error response
            }
        };

        // Call APIController's method
        APIController.fetchAPIBookings(getActivity(), onResponse, onErrorListener);
    }

}
