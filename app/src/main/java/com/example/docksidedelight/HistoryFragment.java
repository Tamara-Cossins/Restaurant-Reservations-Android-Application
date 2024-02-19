package com.example.docksidedelight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RatingBar ratingBar;
    private EditText reviewInput;
    private BookingAdapter adapter;
    private Button reviewButton;
    private List<Booking> pastBookings;

    public HistoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fragment inflation
        View view = inflater.inflate(R.layout.fragment_history, container, false);


        reviewButton = view.findViewById(R.id.review_btn);
        recyclerView = view.findViewById(R.id.past_bookings_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        pastBookings = PastBookingsData();

        // Setting up the adapter
        adapter = new BookingAdapter(pastBookings, null);
        // Indicates that it is a past booking, so correct item xml is used
        adapter.setPastBooking(true);
        recyclerView.setAdapter(adapter);

        // Alert dialog to allow users to submit a review of their restaurant experience
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayReviewDialog();
            }
        });

        return view;
    }

    // Currently uses dummy data as API is wiped frequently- cannot store past data
    private List<Booking> PastBookingsData() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking(1, "John Doe", "123456789", "Dinner", "Indoors: Seaside", 4, "2020-12-01"));
        bookings.add(new Booking(2, "Jane Doe", "987654321", "Lunch", "Outdoors: Garden", 2, "2018-11-30"));
        return bookings;
    }

    // Star rating bar and edittext in review alert dialog for user input of the restaurant
    private void displayReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Submit Feedback:");
        // Inflate the custom layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_review, null);
        builder.setView(dialogView);

        reviewInput = dialogView.findViewById(R.id.review_text);

        // Submit closes the alert dialog and submits review
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float rating = ratingBar.getRating();
                String review = reviewInput.getText().toString();
                dialog.cancel();
            }
        });
        // Cancel button cancels the alert dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
