package com.example.docksidedelight;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Booking> selectedBooking = new MutableLiveData<>();

    public SharedViewModel() {

    }

    public LiveData<Booking> getSelectedBooking() {
        return selectedBooking;
    }

    public void selectBooking(Booking booking) {
        selectedBooking.setValue(booking);
    }

}
