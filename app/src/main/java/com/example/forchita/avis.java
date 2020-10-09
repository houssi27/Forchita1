package com.example.forchita;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class avis extends Fragment {
    RatingBar ratingBar3;
    TextView restoName;
    Button submit,cancel;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    String userID;
    String restoID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.avis, container, false);
        ratingBar3 = view.findViewById(R.id.ratingBar3);
        restoName = view.findViewById(R.id.restoName);
        submit = view.findViewById(R.id.submit);
        cancel = view.findViewById(R.id.cancel);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        if(fAuth.getCurrentUser() != null) {
            userID = fAuth.getCurrentUser().getUid();
            Bundle bundle = this.getArguments();
            if (bundle != null) {
                restoID = bundle.getString("key2");
                restoName.setText(bundle.getString("key1"));
            }


            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirstFragement secondFragement = new FirstFragement();
                    getFragmentManager().beginTransaction().replace(R.id.fragment, secondFragement).commit();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    float getrating = ratingBar3.getRating();
                    rootNode = FirebaseDatabase.getInstance();
                    reference = rootNode.getReference("Rating");
                    String stars = String.valueOf(getrating);
                    String name = restoName.getText().toString();
                    String userid = userID;
                    String restoid = restoID;
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(""+userid+restoid)) {
                                Toast.makeText(getActivity(), "You have already let a review.", Toast.LENGTH_SHORT).show();
                                FirstFragement fragment = new FirstFragement();
                                getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
                            } else {
                                UserHelperClass helperClass = new UserHelperClass(name,userid,restoid,stars);
                                reference.child(userID + restoid).setValue(helperClass);
                                Toast.makeText(getActivity(), "Review added.", Toast.LENGTH_SHORT).show();
                                FirstFragement fragment = new FirstFragement();
                                getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
        }
        if(fAuth.getCurrentUser() == null){
            Toast.makeText(getActivity(), "Login first.", Toast.LENGTH_SHORT).show();
            FirstFragement secondFragement = new FirstFragement();
            getFragmentManager().beginTransaction().replace(R.id.fragment, secondFragement).commit();
        }

        return view;
    }
}
