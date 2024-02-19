package com.example.docksidedelight;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.Gson;

public class SettingsFragment extends Fragment {
    // Fragment that holds notification prefs, account details, past bookings, restaurant submit review, contact data

    Button feedback_btn, delete_acc_button, past_bookings_btn;
    RatingBar ratingBar;
    EditText reviewInput;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        feedback_btn = view.findViewById(R.id.feedback_btn);
        delete_acc_button = view.findViewById(R.id.delete_acc_btn);
        past_bookings_btn = view.findViewById(R.id.history_btn);

        // Alert dialog used to allow user to submit app feedback
        feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFeedbackDialog();
            }
        });


        // Alert dialog to ensure user wants to delete account - no accidental misclick
        delete_acc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAccountDialog();
            }
        });

        // Navigates to the historyFragment where previous bookings of that user are displayed in a recyclerview
        past_bookings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment historyFragment = new HistoryFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, historyFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }


    // Alert dialog for deleting account
    private void showDeleteAccountDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Account");
        // Inflate the custom layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_delete_acc, null);
        builder.setView(dialogView);

        final EditText inputEmail = getActivity().findViewById(R.id.login_email);
        final EditText inputPassword = getActivity().findViewById(R.id.login_password);

        // If delete is chosen, deleteAccount method is called
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                deleteAccount(email, password);
                dialog.cancel();
            }
        });
        // If cancel button is chosen, the alert dialog closes
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // Deletes account from shared prefs
    private void deleteAccount(String email, String password) {
        User currentUser = User.getInstance();
        if (currentUser.getEmail().equals(email) && currentUser.getPassword().equals(password)) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", MODE_PRIVATE);

            // Debugging: Check if the email key exists
            if (sharedPreferences.contains(email)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(email);
                editor.apply();

                Log.d("DeleteAccount", "Account deleted successfully.");

                // Reset the User singleton instance
                User.resetInstance();

                Toast.makeText(getActivity(), "Account Successfully Deleted", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("DeleteAccount", "Account not found.");
                Toast.makeText(getActivity(), "Account not found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Error deleting account", Toast.LENGTH_SHORT).show();
        }
    }


    // Editext for user input of the app itself
    private void displayFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Submit Feedback:");
        // Inflate the custom layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alertdialog_feedback, null);
        builder.setView(dialogView);

        reviewInput = dialogView.findViewById(R.id.review_text);

        // Submit closes the alert dialog and submits feedback
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float rating = ratingBar.getRating();
                String review = reviewInput.getText().toString();

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