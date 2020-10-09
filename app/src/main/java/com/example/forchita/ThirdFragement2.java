package com.example.forchita;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ThirdFragement2 extends Fragment {
    String TAG = " TAG";
    TextView already;
    EditText name, email, phone, password;
    Button create;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    String userID;
    MultiAutoCompleteTextView mactv;
    String[] interests = new String[]{"Restaurant de fish and chips","Restaurant de spécialités à base de poisson-pêcheur",
            "Restaurant libanais", "Pizzeria", "Karantika", "Snack", "Shawarma", "Kebab", "Restaurant japonais", "Rôtisserie", "Restaurant méditerranéen",
            "Crêperie", "Cafétéria", "Restaurant familial", "Restaurant de poisson 'dojo' (Japon)", "Restaurant mexicain", "Restaurant de grillades",
            "Restaurant de cuisine traditionnelle", "Specialite poissons", "Restaurant gastronomique", "Restaurant chinois", "Restaurant italien",
            "Restaurant de spécialités traditionnelles des États-Unis", "Paella", "Restaurant asiatique", "Restaurant turc", "Restaurant syrien",
            "Restaurant", "Hamburger", "Restaurant indien", "Tacos", "Restauration rapide", "Glacier", "Restaurant de plats à emporter", "Convient aux végétariens",
            "Petit déjeuner", "Chalereux", "Repas sur place", "Groupes", "Convient aux enfants", "Livraison", "Service de restauration ouvert tard en soirée",
            "Terrasse", "Vente à emporter", "Chaleureux", "Décontracté", "Argent liquide seulement", "Livraison sans contact", "Drive disponible"};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.register, container, false);
        already = view.findViewById(R.id.already);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        password = view.findViewById(R.id.password);
        create = view.findViewById(R.id.create);
        progressBar = view.findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mactv = view.findViewById(R.id.mactv);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,interests);
        mactv.setAdapter(adapter);
        mactv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        if(fAuth.getCurrentUser()!= null){
            login fragment = new login();
            getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        }

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email1 = email.getText().toString().trim();
                String password1 = password.getText().toString().trim();
                String name1 = name.getText().toString().trim();
                String phon = phone.getText().toString();
                String interest = mactv.getText().toString().trim();
                if(TextUtils.isEmpty(email1)){
                    email.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password1)){
                    password.setError("Password is required.");
                    return;
                }
                if(password1.length() < 6){
                    password.setError("Password must be =< 6 characters");
                    return;
                }
                if(TextUtils.isEmpty(name1)){
                    name.setError("Name is required.");
                    return;
                }
                if(TextUtils.isEmpty(interest)){
                    mactv.setError("Interest is required.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                create.setVisibility(View.INVISIBLE);

                fAuth.createUserWithEmailAndPassword(email1,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser userr = fAuth.getCurrentUser();
                            userr.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(view.getContext(), "Verification Email has been sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    create.setVisibility(View.VISIBLE);
                                    Log.d(TAG,"OnFailure : "+ e.toString());
                                }
                            });

                            Toast.makeText(getActivity(), "User Created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("name",name1);
                            user.put("email",email1);
                            user.put("phone",phon);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"OnSuccess : user profile is created for "+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"OnFailure : "+ e.toString());
                                }
                            });

                            rootNode = FirebaseDatabase.getInstance();
                            reference = rootNode.getReference("users");
                            String choice = mactv.getText().toString().trim();
                            String[] a = choice.split("\\s*,\\s*");
                            String b=a[0];
                            for(int i=1; i<a.length;i++){
                                b += ","+a[i];
                            }
                            String finalB = b;
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserHelperClass2 helperClass2 = new UserHelperClass2("0.5","0.5",finalB);
                                    reference.child(userID).setValue(helperClass2);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            login fragment = new login();
                            getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
                        }else {
                            progressBar.setVisibility(View.INVISIBLE);
                            already.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThirdFragement fragment = new ThirdFragement();
                getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
            }
        });

        return view;


    }
}
