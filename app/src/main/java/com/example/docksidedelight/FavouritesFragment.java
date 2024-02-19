package com.example.docksidedelight;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

public class FavouritesFragment extends Fragment {

    // List for the users favourite meals
    private List<Meal> favouriteMeals;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.favourites_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        populateFavourites();

        // Recyclerview which is populated with the users chosen favourites
        MealAdapter adapter = new MealAdapter(getContext(), favouriteMeals, null, new MealAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClicked(Meal meal, int position) {
                // Remove meal from user's favorites list and update RecyclerView for instant UI removal
                User.getInstance().removeFavourite(meal);
                Fragment favFragment = new FavouritesFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, favFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                CharSequence text = "Favourite deleted.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), text, duration);
                toast.show();
            }
        });
        recyclerView.setAdapter(adapter);
    }

    // Utilises singleton user class to obtain user favourites list
    private void populateFavourites() {
        favouriteMeals = User.getInstance().getFavouriteMeals();
    }
}
