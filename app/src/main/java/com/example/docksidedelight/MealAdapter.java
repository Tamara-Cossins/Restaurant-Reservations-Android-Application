package com.example.docksidedelight;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.docksidedelight.Meal;
import com.example.docksidedelight.R;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    // Adapter for the menu and favourites recyclerviews

    private List<Meal> mealsList;
    private Context context;
    private OnFavoriteClickListener favoriteClickListener;
    private OnDeleteClickListener deleteClickListener;

    // Constructor to initialise adapter with the list
    public MealAdapter(Context context, List<Meal> mealsList, OnFavoriteClickListener favoriteListener, OnDeleteClickListener deleteListener) {
        this.context = context;
        this.mealsList = mealsList;
        this.favoriteClickListener = favoriteListener;
        this.deleteClickListener = deleteListener;
    }

    // Inflates the booking item layout from xml when needed - creates viewholder objects for each booking item in recycler
    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_item, parent, false);
        return new MealViewHolder(view);
    }

    // Binds the booking data to the corresponding viewholder object
    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealsList.get(position);
        holder.mealName.setText(meal.getName());
        holder.mealImage.setImageResource(meal.getImageResID()); // Assuming you have image IDs

        holder.favoriteButton.setOnClickListener(view -> {
            if (favoriteClickListener != null) {
                favoriteClickListener.onFavoriteClicked(meal, position);
            }
        });

        holder.deleteButton.setOnClickListener(view -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClicked(meal, position);
            }
        });
    }

    // Total number of meal items
    @Override
    public int getItemCount() {
        return mealsList.size();
    }


    // Class for viewholder pattern
    public static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView mealImage;
        TextView mealName;
        Button favoriteButton;
        Button deleteButton;

        // Constructor for viewholder
        public MealViewHolder(View itemView) {
            super(itemView);
            mealImage = itemView.findViewById(R.id.meal_image);
            mealName = itemView.findViewById(R.id.meal_name);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    // If the favourite button is clicked from item
    public interface OnFavoriteClickListener {
        void onFavoriteClicked(Meal meal, int position);
    }

    // If the delete button is clicked from item
    public interface OnDeleteClickListener {
        void onDeleteClicked(Meal meal, int position);
    }
}
