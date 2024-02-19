package com.example.docksidedelight;

public interface BookingAdapterCallback {
    // Callback to be invoked when an action is performed within an upcoming_booking_item


    // Method that is called when the edit / delete button is clicked in an upcoming booking item of recyclerview
    void onEditorDeleteClicked(Booking booking);
}
