package com.example.forchita;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter1 extends RecyclerView.Adapter<Adapter1.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    List<Restaurant> restaurants;
    List<Restaurant> restaurantsAll;
    OnNoteListener mOnNoteListener;

    public Adapter1(Context ctx, List<Restaurant> restaurants, OnNoteListener onNoteListner){
        this.inflater= LayoutInflater.from(ctx);
        this.restaurants = restaurants;
        this.mOnNoteListener= onNoteListner;
        this.restaurantsAll = restaurants;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_list_layout,parent,false);
        return new ViewHolder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.restauTitle.setText(restaurants.get(position).getRestName());
        holder.restauCat.setText(restaurants.get(position).getRestCat());
        Picasso.get().load(restaurants.get(position).getRestImage()).into(holder.coverImage);
        holder.ratingBar.setRating(Float.parseFloat(restaurants.get(position).getRatingBar()));
    }


    @Override
    public int getItemCount() {
        return restaurants.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView restauTitle, restauCat;
        ImageView coverImage;
        RatingBar ratingBar;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);

            restauTitle = itemView.findViewById(R.id.restauTitle);
            restauCat = itemView.findViewById(R.id.restauCat );
            coverImage = itemView.findViewById(R.id.coverImage);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            this.onNoteListener = onNoteListener;

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());

        }
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }



    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Restaurant> filtredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                filtredList.addAll(restaurants);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for(Restaurant item : restaurantsAll){
                    if(item.getRestName().toLowerCase().contains(filterPattern)){
                        filtredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            if(charSequence == null || charSequence.length() == 0){
                restaurants.clear();
                restaurants.addAll(restaurants);
            }
            else {
                restaurants.clear();
                restaurants.addAll((List)filterResults.values);
                notifyDataSetChanged();
            }

        }
    };
}
