package com.example.forchita;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;

public class resto extends Fragment {

    TextView nameR, adresseR, categorieR;
    ImageView photoR;
    RatingBar ratingBar2;
    Button rate, info, retour;
    ScrollView scrollView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String id;
    Location location;
    String userID;
    FusedLocationProviderClient fusedLocationProviderClient;
    public resto(){
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.resto, container, false);
        nameR = view.findViewById(R.id.nameR);
        adresseR = view.findViewById(R.id.adresseR);
        categorieR = view.findViewById(R.id.categorieR);
        photoR = view.findViewById(R.id.photoR);
        rate = view.findViewById(R.id.rate);
        retour = view.findViewById(R.id.retour);
        info = view.findViewById(R.id.moreinfo);
        ratingBar2 = view.findViewById(R.id.ratingBar2);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        location = getLastKnownLocation();
        userID = fAuth.getCurrentUser().getUid();
        Bundle bundle = this.getArguments();
        if(bundle != null){
            id = bundle.getString("key0");
            nameR.setText(bundle.getString("key1"));
            categorieR.setText(bundle.getString("key2"));
            Picasso.get().load(bundle.getString("key3")).into(photoR);
            ratingBar2.setRating(Float.parseFloat(bundle.getString("key4")));
            adresseR.setText(bundle.getString("key5"));
            String latitude = bundle.getString("key6");
            String longitude = bundle.getString("key7");
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
            Call<Void> update=api.updateWeights(userID, Integer.parseInt(id), location.getLatitude(), location.getLongitude());
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Mettre à jour les poids

                    update.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                    });

                    openWebURL("https://maps.google.com/?q="+latitude+","+longitude);
                }
            });
        }
        ((MainActivity)getActivity()).setActionBarTitle((String) nameR.getText());
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                avis secondFragement = new avis();
                Bundle bundle = new Bundle();
                bundle.putString("key1", String.valueOf(nameR.getText()));
                bundle.putString("key2",id);
                secondFragement.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.fragment, secondFragement).commit();
            }
        });
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rate rate = new rate();
                getFragmentManager().beginTransaction().replace(R.id.fragment, rate).commit();
            }
        });
        return view;
    }
    public void openWebURL( String inURL ) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );

        startActivity( browse );
    }
    private Location getLastKnownLocation() {
        Location l=null;
        LocationManager mLocationManager = (LocationManager)getContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
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
