package com.example.forchita;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class Adapt extends PagerAdapter  {

    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapt(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position ) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item,container,false);
        ImageView imageView;
        TextView textView,textView1;
        imageView = view.findViewById(R.id.image);
        textView = view.findViewById(R.id.title);
        textView1 = view.findViewById(R.id.title2);

        imageView.setImageResource(models.get(position).getImage());
        textView.setText(models.get(position).getTitle());
        textView1.setText(models.get(position).getTitle2());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Hamburger.class);
                intent.putExtra("key", models.get(position).getTitle());
                context.startActivity(intent);
            }
        });

        container.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
