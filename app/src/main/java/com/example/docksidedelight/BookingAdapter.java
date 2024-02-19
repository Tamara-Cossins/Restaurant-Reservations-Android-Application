package com.example.docksidedelight;

import android.view.ViewGroup;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;


public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    // Adapter for the booking recyclerview to display any API upcoming bookings
    // Used in upcoming booking recyclerview and also past booking recyclerview

    // List of the upcoming booking objects to be displayed in recyclerview
    private List<Booking> bookingList;
    // Callback for handling user actions
    private BookingAdapterCallback callback;

    // Variable to identify whether booking is a past booking or upcoming booking
    private boolean isPastBooking = false;



    // Constructor to initialise adapter with the list and callback
    public BookingAdapter(List<Booking> bookingList, BookingAdapterCallback callback) {
        this.bookingList = bookingList;
        this.callback = callback;
    }

    // Sets if past booking or not specifically when a past booking is used
    public void setPastBooking(boolean isPastBooking) {
        this.isPastBooking = isPastBooking;
    }


    // Inflates the booking item layout from xml when needed - creates viewholder objects for each booking item in recycler
    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // If is a pastbooking, it uses past_booking_item
        if (isPastBooking) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_booking_item, parent, false);
            // If is an upcoming booking, it uses upcoming_booking_item
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_booking_item, parent, false);
        }
        return new ViewHolder(view);
    }

    // Binds the booking data to the corresponding viewholder object
    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.bind(booking, callback);
    }

    // Total number of bookings
    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // Class for viewholder pattern
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView bookingDetails;
        private Button editButton;

        // Constructor for viewholder
        public ViewHolder(View itemView) {
            super(itemView);
            bookingDetails = itemView.findViewById(R.id.bookingDetails);
            editButton = itemView.findViewById(R.id.edit_button);
        }

        // Method to bind booking data to the viewholder
        public void bind(final Booking booking, final BookingAdapterCallback callback) {
            // Format booking details
            String details = "Date: " + booking.getDate() +
                    "\nMeal: " + booking.getMeal() +
                    "\nArea: " + booking.getSeatingArea() +
                    "\nSize: " + booking.getTableSize();
            bookingDetails.setText(details);

            // callback is invoked upon clicking editButton - only if editButton is present (in upcoming booking items)
            if (editButton != null) {
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callback != null) {
                            callback.onEditorDeleteClicked(booking);
                        }
                    }
                });
            }
        }
    }
}
