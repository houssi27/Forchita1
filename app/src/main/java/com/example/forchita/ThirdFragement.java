package com.example.forchita;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragement extends Fragment {
    Button login;
    TextView create,forgot;
    EditText email1,password2;
    FirebaseAuth fAuth;
    ProgressBar progressBar2;
    public ThirdFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third_fragement, container, false);
        login = view.findViewById(R.id.login);
        create = view.findViewById(R.id.create);
        email1 = view.findViewById(R.id.email1);
        password2 = view.findViewById(R.id.password2);
        progressBar2 = view.findViewById(R.id.progressBar2);
        fAuth = FirebaseAuth.getInstance();
        forgot = view.findViewById(R.id.forgot);

        if(fAuth.getCurrentUser()!= null){
            login fragment = new login();
            getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        }

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset password ?");
                passwordResetDialog.setMessage("Enter your Email to receive reset link.");
                passwordResetDialog.setView(resetMail);
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Reset link has been sent to your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error !" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThirdFragement2 fragment = new ThirdFragement2();
                getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email11 = email1.getText().toString().trim();
                String password = password2.getText().toString().trim();
                if(TextUtils.isEmpty(email11)){
                    email1.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    password2.setError("Password is required.");
                    return;
                }
                if(password.length() < 6){
                    password2.setError("Password must be =< 6 characters");
                    return;
                }
                progressBar2.setVisibility(View.VISIBLE);
                login.setVisibility(View.INVISIBLE);

                fAuth.signInWithEmailAndPassword(email11,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                            getActivity().recreate();
                            login fragment = new login();
                            getFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
                        }else {
                            Toast.makeText(getActivity(), "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar2.setVisibility(View.INVISIBLE);
                            login.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
        return view;
    }
}
