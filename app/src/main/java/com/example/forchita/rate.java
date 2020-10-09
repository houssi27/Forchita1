package com.example.forchita;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class rate extends Fragment implements Adapter.OnNoteListener {
    RecyclerView recyclerView;
    ListView listee;
    SearchView search_bar;
    TextView textViewResult;
    List<Restaurant> restaurants;
    List<String> restaurants1,restaurants2;
    List<Integer> restaurants3;
    Adapter adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    TextView show;
    String id;
    private static String JSON_URL2 = "https://firebasestorage.googleapis.com/v0/b/essaie1-8f6f2.appspot.com/o/restaurants.json?alt=media&token=e464d0fa-a6cf-48d5-b990-3da709d2e993";
    private static String JSON_URL = "https://forchita2020.herokuapp.com/ratings";
    public rate() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rate, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ((MainActivity)getActivity()).setActionBarTitle("All restaurants");

        recyclerView = view.findViewById(R.id.restauList);
        listee = view.findViewById(R.id.listee);
        restaurants = new ArrayList<>();
        restaurants1 = new ArrayList<>();
        restaurants2 = new ArrayList<>();
        restaurants3 = new ArrayList<Integer>();
        extactRestaurant1();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter(getContext(),restaurants,this);
        recyclerView.setAdapter(adapter);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        show = view.findViewById(R.id.show);
        search_bar = view.findViewById(R.id.search_bar);
        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
            }
        },3500);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstFragement secondFragement = new FirstFragement();
                getFragmentManager().beginTransaction().replace(R.id.fragment, secondFragement).commit();
            }
        });
        search_bar.setImeOptions(EditorInfo.IME_ACTION_DONE);
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        return view;
    }

    private void extactRestaurant(List restaurants1,List restaurants2) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int j = 0; j < response.length(); j++) {

                    try {
                        JSONObject restauObject = response.getJSONObject(j);
                        Restaurant restaurant = new Restaurant();
                        id = restauObject.getString("restaurant_id");
                        //extactRestaurant1(id);
                        restaurant.setRestId(restauObject.getString("restaurant_id"));
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
                        restaurant.setRestImage("https://firebasestorage.googleapis.com/v0/b/essaie1-8f6f2.appspot.com/o/food.png?alt=media&token=b7fc1973-b86c-4707-ad95-c23ca52f4776");
                        restaurant.setRestAdresse(restauObject.getString("address"));
                        restaurant.setRestLatitude(restauObject.getString("latitude"));
                        restaurant.setRestLongitude(restauObject.getString("longitude"));
                        restaurants.add(restaurant);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                /*final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                },1000);*/
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
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
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
        bundle.putString("key0",String.valueOf(restaurants.get(position).getRestId()));
        bundle.putString("key1", String.valueOf(restaurants.get(position).getRestName()));
        bundle.putString("key2", String.valueOf(restaurants.get(position).getRestCat()));
        bundle.putString("key3", String.valueOf(restaurants.get(position).getRestImage()));
        bundle.putString("key4", String.valueOf(restaurants.get(position).getRatingBar()));
        bundle.putString("key5", String.valueOf(restaurants.get(position).getRestAdresse()));
        bundle.putString("key6", String.valueOf(restaurants.get(position).getRestLatitude()));
        bundle.putString("key7", String.valueOf(restaurants.get(position).getRestLongitude()));
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        Log.d("TAG","Clicked : " + restaurants.get(position));
    }

}
