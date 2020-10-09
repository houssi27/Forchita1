package com.example.forchita;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class Recommendation2 extends Fragment implements Adapter.OnNoteListener {

    RecyclerView recyclerView;
    List<Restaurant> restaurants;
    List<String> restaurants1,restaurants2;
    List<Integer> restaurants3;
    Adapter adapter;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    TextView show;
    Location location;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<Integer> recommendations;
    private static String JSON_URL2 = "https://firebasestorage.googleapis.com/v0/b/essaie1-8f6f2.appspot.com/o/restaurants.json?alt=media&token=e464d0fa-a6cf-48d5-b990-3da709d2e993";
    private static String JSON_URL = "https://forchita2020.herokuapp.com/ratings";

    public Recommendation2() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recommandation2, container, false);


        recyclerView = view.findViewById(R.id.restauList);
        restaurants = new ArrayList<>();
        restaurants1 = new ArrayList<>();
        restaurants2 = new ArrayList<>();
        restaurants3 = new ArrayList<Integer>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter(getContext(), restaurants, this);
        recyclerView.setAdapter(adapter);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        show = view.findViewById(R.id.show);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        location = getLastKnownLocation();

        final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.startLoadingDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
            }
        },6000);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstFragement secondFragement = new FirstFragement();
                getFragmentManager().beginTransaction().replace(R.id.fragment, secondFragement).commit();
            }
        });
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .callTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(40, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        Api api=retrofit.create(Api.class);


        Call<Near> callnearby= api.getNearby(location.getLatitude(), location.getLongitude());

        // Restaurants à proximité
        callnearby.enqueue(new Callback<Near>() {
            @Override
            public void onResponse(Call<Near> call, retrofit2.Response<Near> response) {
                Near near=response.body();
                recommendations= near.getOutput();
                //Log.d("List : ", recommendations.toString());
                extactRestaurant1();

            }

            @Override
            public void onFailure(Call<Near> call, Throwable t) {

            }
        });


        return view;
    }

    private void extactRestaurant(List restaurants1,List restaurants2) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, JSON_URL2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<recommendations.size();i++) {
                    try {
                        for (int j = 0; j < response.length(); j++) {
                            JSONObject restauObject = response.getJSONObject(j);
                            Restaurant restaurant = new Restaurant();
                            int a = Integer.parseInt(restauObject.getString("restaurant_id"));
                            if (a == recommendations.get(i)) {
                                Log.d("List : ", "done");
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
                            }
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
                Log.d("TAG",restaurants1.toString());
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

    @Override
    public void onNoteClick(int position) {
        resto fragment = new resto();
        Bundle bundle = new Bundle();
        bundle.putString("key0", String.valueOf(restaurants.get(position).getRestId()));
        bundle.putString("key1", String.valueOf(restaurants.get(position).getRestName()));
        bundle.putString("key2", String.valueOf(restaurants.get(position).getRestCat()));
        bundle.putString("key3", String.valueOf(restaurants.get(position).getRestImage()));
        bundle.putString("key4", String.valueOf(restaurants.get(position).getRatingBar()));
        bundle.putString("key5", String.valueOf(restaurants.get(position).getRestAdresse()));
        bundle.putString("key6", String.valueOf(restaurants.get(position).getRestLatitude()));
        bundle.putString("key7", String.valueOf(restaurants.get(position).getRestLongitude()));
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
    }

    private Location getLastKnownLocation() {
        Location l=null;
        LocationManager mLocationManager = (LocationManager)getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }


}
