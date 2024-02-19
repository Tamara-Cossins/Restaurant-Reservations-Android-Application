package com.example.docksidedelight;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    // Fragment to display menu items in a recyclerview - user is able to favourite menu items
    private List<Meal> mealsList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        populateMealsList();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.upcoming_bookings_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        populateMealsList();

        MealAdapter adapter = new MealAdapter(getContext(), mealsList, new MealAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClicked(Meal meal, int position) {
                // Add meal to user's favorites when favourite button clicked
                User.getInstance().addFavourite(meal);
                CharSequence text = "Favourite added.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), text, duration);
                toast.show();
            }
        }, new MealAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClicked(Meal meal, int position) {
                // Delete meal from user's favourites when delete button clicked
                User.getInstance().removeFavourite(meal);
                CharSequence text = "Favourite deleted.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), text, duration);
                toast.show();
            }
        });
        recyclerView.setAdapter(adapter);
    }


    // Populates the recyclerview with the menu items
    private void populateMealsList() {

        // Adding meals to the list
        mealsList.add(new Meal("Chicken and Broccoli pizza", R.drawable.pizza_creamy));
        mealsList.add(new Meal("Crispy Fish 'n' Chips", R.drawable.fishnchips));
        mealsList.add(new Meal("Creamy Carbonara with Bacon Rashers", R.drawable.carbonara));
        mealsList.add(new Meal("Chicken Katsu Curry with Rice", R.drawable.chicken_katsu));
        mealsList.add(new Meal("BBQ Ribs in sweet sticky sauce", R.drawable.bbq_ribs));
    }
}