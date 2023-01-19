package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

public class SignIn extends AppCompatActivity {
    TextView signup;
    Button signin;
    EditText email;
    EditText password;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.signin_progress_bar);
        signup = findViewById(R.id.signUpLink);
        signin = findViewById(R.id.SigninBTN);
        email = findViewById(R.id.signin_email_txt);
        password = findViewById(R.id.signin_password_txt);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_txt = email.getText().toString();
                String password_txt = password.getText().toString();

                if(email_txt.isEmpty())
                    Toast.makeText(getApplicationContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();

                else if(password_txt.isEmpty())
                    Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();

                else{

                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email_txt,password_txt).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            String player_id = OneSignal.getDeviceState().getUserId();
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Player IDs").orderByValue().equalTo(player_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if(!snapshot.hasChildren())
                                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Player IDs").push().setValue(player_id);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            Intent intent = new Intent(SignIn.this, MainPage.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            MainPage.viewContacts = new ContactsProvider();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Could Not Login", Toast.LENGTH_SHORT).show();
                            email.setText("");
                            password.setText("");
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        onDestroy();
    }
}