package com.application.recommend.recommendplaces;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Akarsh on 11-01-2018.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.Items> implements Serializable {

    private Context context;
    private ArrayList<PlacesModel> placesModels = new ArrayList<>();

    public PlacesAdapter(Context context, ArrayList<PlacesModel> placesModels) {
        this.context = context;
        this.placesModels = placesModels;
    }

    @Override
    public PlacesAdapter.Items onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.places_item, parent, false);
        return new Items(view);
    }


    @Override
    public void onBindViewHolder(final PlacesAdapter.Items holder, int position) {
        holder.tvName.setText(placesModels.get(position).getName());
        holder.tvAdd1.setText(placesModels.get(position).getAdd1());
        if(placesModels.get(position).getRating().equals("0"))
            holder.ratingBar.setVisibility(View.GONE);
        else
            holder.ratingBar.setRating(Float.parseFloat(placesModels.get(position).getRating())/2);
        holder.myView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, PlaceDetails.class);
                        intent.putExtra("BUNDLE", placesModels);
                        intent.putExtra("position", holder.getAdapterPosition());
                        context.startActivity(intent);
                    }
                }
        );
    }

    public ArrayList<PlacesModel> getPlacesModels() {
        return placesModels;
    }

    @Override
    public int getItemCount() {
        return placesModels.size();
    }

    public class Items extends RecyclerView.ViewHolder implements Serializable {

        TextView tvName, tvAdd1;
        RatingBar ratingBar;
        View myView;

        public Items(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvAdd1 = itemView.findViewById(R.id.tvAdd1);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            myView = itemView.findViewById(R.id.myView);

        }
    }
}
