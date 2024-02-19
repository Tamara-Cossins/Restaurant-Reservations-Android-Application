package com.example.docksidedelight;

import android.content.Context;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class APIController {
    // A class which handles API requests for bookings

    // URL for API
    private final static String url = "https://web.socem.plymouth.ac.uk/COMP2000/ReservationApi/api/Reservations/";


    // Method that fetches all the current bookings on the API using Volley GET
    public static void fetchAPIBookings(Context context, Response.Listener<JSONArray> onResponse, Response.ErrorListener onErrorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, onResponse, onErrorListener);
        queue.add(jsonArrayRequest);
    }

    // Method to POST a new booking to the API as a JSON object
    public static void postBooking(Context context, String customerName, String customerPhoneNumber, String meal, String seatingArea, int tableSize, String date, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorListener) {
        RequestQueue queueReq = Volley.newRequestQueue(context);

        JSONObject postData = new JSONObject();
        try {
            postData.put("customerName", customerName);
            postData.put("customerPhoneNumber", customerPhoneNumber);
            postData.put("meal", meal);
            postData.put("seatingArea", seatingArea);
            postData.put("tableSize", tableSize);
            postData.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postData, onResponse, onErrorListener);
        queueReq.add(jsonObjectRequest);
    }

    // Method to PUT updated details into a pre-existing API booking using id from booking as identifier - edits a pre-existing booking
    public static void updateBooking(Context context, Booking booking, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorListener) {
        String updateURL = url + booking.getId();
        RequestQueue queueReq = Volley.newRequestQueue(context);

        JSONObject postData = new JSONObject();
        try {
            postData.put("id", booking.getId());
            postData.put("customerName", booking.getCustomerName());
            postData.put("customerPhoneNumber", booking.getCustomerPhoneNumber());
            postData.put("meal", booking.getMeal());
            postData.put("seatingArea", booking.getSeatingArea());
            postData.put("tableSize", booking.getTableSize());
            postData.put("date", booking.getDate());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, updateURL, postData, onResponse, onErrorListener);
        queueReq.add(jsonObjectRequest);
    }

    // Method to DELETE a booking on the API using ID from booking as identifier
    public static void cancelBooking(Context context, int bookingId, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorListener) {
        String bookingURL = url + bookingId;
        RequestQueue queueReq = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, bookingURL, null, onResponse, onErrorListener);
        queueReq.add(jsonObjectRequest);
    }
}