package com.example.docksidedelight;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class DashboardFragment extends Fragment {
    // First page user sees when logging in, contains restaurant information

    Button menu_btn;
    TextView welcomeName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fragment inflation
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        welcomeName = view.findViewById(R.id.name_text);
        menu_btn = view.findViewById(R.id.menu_btn);

        welcomeName.setText("Welcome, " + User.getInstance().getCustomerName());

        // When "Menu" button clicked, it leads to the menu xml with recyclerview
        menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment menuFragment = new MenuFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, menuFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

}
