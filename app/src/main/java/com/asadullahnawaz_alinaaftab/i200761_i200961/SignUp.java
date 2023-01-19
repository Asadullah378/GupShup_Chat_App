package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {
    TextView signin;
    Button signup;
    EditText email;
    EditText password;

    public static final String EMAIL = "com.asadullahnawaz_alinaaftab.i200761_i200961.EMAIL";
    public static final String PASSWORD = "com.asadullahnawaz_alinaaftab.i200761_i200961.PASSWORD";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signin = findViewById(R.id.signInLink);
        signup = findViewById(R.id.SignupBTN);
        email = findViewById(R.id.signup_email_txt);
        password = findViewById(R.id.signup_password_txt);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_txt = email.getText().toString();
                String password_txt = password.getText().toString();

                if(email_txt.isEmpty())
                    Toast.makeText(getApplicationContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();

                else if(password_txt.isEmpty())
                    Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();

                else{

                    Intent intent = new Intent(SignUp.this, SetProfile.class);
                    intent.putExtra(EMAIL,email_txt);
                    intent.putExtra(PASSWORD,password_txt);
                    startActivity(intent);

                }
            }
        });

    }


}