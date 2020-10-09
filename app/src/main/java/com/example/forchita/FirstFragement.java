package com.example.forchita;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.BaseHttpStack;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.android.volley.AuthFailureError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragement extends Fragment {

    ViewPager viewPager;
    ConstraintLayout layout;
    Adapt adapter;
    List<Model> models;
    Integer[] colors = null;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    Button r1,r2,all;
    public FirstFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_first_fragement, container, false);
        ((MainActivity)getActivity()).setActionBarTitle("Restaurants");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        r1 = view.findViewById(R.id.r1);
        r2 = view.findViewById(R.id.r2);
        all = view.findViewById(R.id.all);
        layout = view.findViewById(R.id.layout);
        fAuth = FirebaseAuth.getInstance();
        models = new ArrayList<>();
        models.add(new Model(R.drawable.photo1,"Hamburger","You can find the best hamburgers in this section"));
        models.add(new Model(R.drawable.photo2,"Pizzeria","You can find the best pizzas in this section"));
        models.add(new Model(R.drawable.photo3,"Tacos","You can find the best tacos in this section"));
        models.add(new Model(R.drawable.photo4,"Specialite poissons","You can find the best fish in this section"));
        adapter = new Adapt(models,getContext());
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130,0,130,0);
        if(fAuth.getCurrentUser() == null) {
            r1.setVisibility(View.INVISIBLE);
        }

            Integer[] colors_temp = {getResources().getColor(R.color.color1),getResources().getColor(R.color.color2),getResources().getColor(R.color.color3),getResources().getColor(R.color.color4)};
            colors = colors_temp;

            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if(position<(adapter.getCount()-1) && position < (colors.length -1)){
                        viewPager.setBackgroundColor((Integer)argbEvaluator.evaluate(positionOffset,colors[position],colors[position+1]));
                        layout.setBackgroundColor((Integer)argbEvaluator.evaluate(positionOffset,colors[position],colors[position+1]));
                        r1.setTextColor((Integer)argbEvaluator.evaluate(positionOffset,colors[position],colors[position+1]));
                        r2.setTextColor((Integer)argbEvaluator.evaluate(positionOffset,colors[position],colors[position+1]));
                        all.setTextColor((Integer)argbEvaluator.evaluate(positionOffset,colors[position],colors[position+1]));
                    }else {
                        viewPager.setBackgroundColor(colors[colors.length - 1]);
                        layout.setBackgroundColor(colors[colors.length - 1]);
                        r1.setTextColor(colors[colors.length - 1]);
                        r2.setTextColor(colors[colors.length - 1]);
                        all.setTextColor(colors[colors.length - 1]);
                    }
                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            r1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Recommendation1 secondFragement = new Recommendation1();
                    getFragmentManager().beginTransaction().replace(R.id.fragment, secondFragement).commit();
                }
            });

            r2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Recommendation2 secondFragement = new Recommendation2();
                    getFragmentManager().beginTransaction().replace(R.id.fragment, secondFragement).commit();
                }
            });


            all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rate secondFragement = new rate();
                    getFragmentManager().beginTransaction().replace(R.id.fragment, secondFragement).commit();

                }
            });






        return view;
    }

}
