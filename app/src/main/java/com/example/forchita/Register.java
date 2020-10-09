package com.example.forchita;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = " TAG";
    EditText name, email, password, phone;
    Button register;
    TextView log;
    FirebaseAuth auth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    AutoCompleteTextView actv;
    private static final String[] interests = new String[]{"Restaurant de fish and chips","Restaurant de spécialités à base de poisson-pêcheur",
            "Restaurant libanais", "Pizzeria", "Karantika", "Snack", "Shawarma", "Kebab", "Restaurant japonais", "Rôtisserie", "Restaurant méditerranéen",
            "Crêperie", "Cafétéria", "Restaurant familial", "Restaurant de poisson 'dojo' (Japon)", "Restaurant mexicain", "Restaurant de grillades",
            "Restaurant de cuisine traditionnelle", "Specialite poissons", "Restaurant gastronomique", "Restaurant chinois", "Restaurant italien",
            "Restaurant de spécialités traditionnelles des États-Unis", "Paella", "Restaurant asiatique", "Restaurant turc", "Restaurant syrien",
            "Restaurant", "Hamburger", "Restaurant indien", "Tacos", "Restauration rapide", "Glacier", "Restaurant de plats à emporter", "Convient aux végétariens",
            "Petit déjeuner", "Chalereux", "Repas sur place", "Groupes", "Convient aux enfants", "Livraison", "Service de restauration ouvert tard en soirée",
            "Terrasse", "Vente à emporter", "Chaleureux", "Décontracté", "Argent liquide seulement", "Livraison sans contact", "Drive disponible"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        register = findViewById(R.id.register);
        log = findViewById(R.id.already);
        progressBar = findViewById(R.id.progressBar);
        actv = findViewById(R.id.mactv);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,interests);
        actv.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),login.class));
            finish();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString().trim();
                String mdp = password.getText().toString().trim();
                String nom = name.getText().toString().trim();
                String phon = phone.getText().toString();
                if(TextUtils.isEmpty(mail)){
                    email.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty((mdp))){
                    password.setError("Password is Required.");
                    return;
                }

                if(password.length()<6){
                    password.setError("6 Characters at least");
                    return;
                }

                if(TextUtils.isEmpty((nom))){
                    name.setError("Name is Required.");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.createUserWithEmailAndPassword(mail,mdp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userID = auth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            user.put("name",nom);
                            user.put("email",mail);
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
                            startActivity(new Intent(getApplicationContext(),FirstFragement.class));
                        }
                        else{
                            Toast.makeText(Register.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
