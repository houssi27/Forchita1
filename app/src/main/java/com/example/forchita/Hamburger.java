package com.example.forchita;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Hamburger extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Restaurant> restaurants;
    List<String> restaurants1,restaurants2;
    Adapter adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String s;
    DatabaseReference storageReference;
    private static String JSON_URL2 = "https://firebasestorage.googleapis.com/v0/b/essaie1-8f6f2.appspot.com/o/restaurants.json?alt=media&token=e464d0fa-a6cf-48d5-b990-3da709d2e993";
    private static String JSON_URL = "https://forchita2020.herokuapp.com/ratings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hamburger);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.restauList);
        restaurants = new ArrayList<>();
        restaurants1 = new ArrayList<>();
        restaurants2 = new ArrayList<>();
        extactRestaurant1();
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        adapter = new Adapter(this,restaurants,this::onNoteClick);
        recyclerView.setAdapter(adapter);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        s = getIntent().getStringExtra("key").toString();


    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    private void extactRestaurant(List restaurants1,List restaurants2) {
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int j = 0; j < response.length(); j++) {
                    try {
                        JSONObject restauObject = response.getJSONObject(j);
                        Restaurant restaurant = new Restaurant();
                        String a = restauObject.getString("category").toLowerCase();
                        if (a.contains(s.toLowerCase())) {
                            restaurant.setRestName(restauObject.getString("title").toString());
                            restaurant.setRestCat(restauObject.getString("category").toString());
                            //restaurant.setRestImage(restauObject.getString("imgUrl"));
                            //restaurant.setRatingBar(restauObject.getString("rating"));
                            restaurant.setRatingBar(String.valueOf(restaurants1.get(j)));
                            if(restaurants2.get(j).equals("0")){
                                restaurant.setRestNombre("(No rate)");
                            }
                            else if(restaurants2.get(j).equals("1")){
                                restaurant.setRestNombre("("+String.valueOf(restaurants2.get(j))+" rate)");
                            }
                            else{
                                restaurant.setRestNombre("("+String.valueOf(restaurants2.get(j))+" rates)");
                            }
                            if(s.equals("Hamburger")){
                                restaurant.setRestImage("https://firebasestorage.googleapis.com/v0/b/essaie1-8f6f2.appspot.com/o/Hamburger.png?alt=media&token=72beb89d-db93-457a-9e80-477833f08364");
                            }
                            if(s.equals("Pizzeria")){
                                restaurant.setRestImage("https://firebasestorage.googleapis.com/v0/b/essaie1-8f6f2.appspot.com/o/Pizzeria.png?alt=media&token=f37869b3-66b1-4882-bf38-683593042842");
                            }
                            if(s.equals("Tacos")){
                                restaurant.setRestImage("https://firebasestorage.googleapis.com/v0/b/essaie1-8f6f2.appspot.com/o/Tacos.png?alt=media&token=f3029945-8bfe-4af2-acab-067f3ec96ac3");
                            }
                            if(s.equals("Specialite poissons")){
                                restaurant.setRestImage("https://firebasestorage.googleapis.com/v0/b/essaie1-8f6f2.appspot.com/o/Fish.png?alt=media&token=f87f9079-1ecf-4e28-a0ae-9d433fcc1d04");
                            }
                            restaurant.setRestAdresse(restauObject.getString("address"));
                            restaurant.setRestLatitude(restauObject.getString("latitude"));
                            restaurant.setRestLongitude(restauObject.getString("longitude"));
                            restaurants.add(restaurant);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag","onErrorResponse"+ error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);

    }
    private void extactRestaurant1() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONArray response) {
                for (int j = 0; j < response.length(); j++) {
                    try {
                        JSONObject restauObject = response.getJSONObject(j);
                        Restaurant restaurant = new Restaurant();
                        String idA = restauObject.getString("Notes");
                        String idB = restauObject.getString("Nombre");
                        restaurant.setRatingBar(restauObject.getString("Notes"));
                        //Log.d("tag", idA);
                        restaurants1.add(idA);
                        restaurants2.add(idB);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                //restaurants3
                extactRestaurant(restaurants1,restaurants2);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag","onErrorResponse"+ error.getMessage());
            }
        });
        queue.add(jsonArrayRequest);
    }
    public void onNoteClick(int position) {
        resto fragment = new resto();
        Bundle bundle = new Bundle();
        bundle.putString("key1", String.valueOf(restaurants.get(position).getRestName()));
        bundle.putString("key2", String.valueOf(restaurants.get(position).getRestCat()));
        bundle.putString("key3", String.valueOf(restaurants.get(position).getRestImage()));
        bundle.putString("key4", String.valueOf(restaurants.get(position).getRatingBar()));
        bundle.putString("key5", String.valueOf(restaurants.get(position).getRestAdresse()));
        bundle.putString("key6", String.valueOf(restaurants.get(position).getRestLatitude()));
        bundle.putString("key7", String.valueOf(restaurants.get(position).getRestLongitude()));
        fragment.setArguments(bundle);
        //getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        Log.d("TAG","Clicked : " + restaurants.get(position));
    }
}
