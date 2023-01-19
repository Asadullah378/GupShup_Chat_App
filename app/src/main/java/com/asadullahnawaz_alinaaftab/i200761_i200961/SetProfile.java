package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;

import java.io.InputStream;


public class SetProfile extends AppCompatActivity {

    Button setProf;
    String email_txt;
    String pass_txt;
    EditText fname;
    EditText phone_no;
    EditText bio;
    ImageView profile_pic;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    Uri galleryUri=null;
    String uploadUri="";
    StorageReference storageRef;
    CardView female_gender;
    CardView male_gender;
    CardView other_gender;
    String gender="";
    Boolean uploaded;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        setProf = findViewById(R.id.setProf);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        progressBar = findViewById(R.id.progress_bar);
        female_gender = findViewById(R.id.female_gender);
        male_gender = findViewById(R.id.male_gender);
        other_gender = findViewById(R.id.other_gender);

        progressBar.setVisibility(View.GONE);
        female_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        male_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        other_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));


        Intent intent = getIntent();
        email_txt = intent.getStringExtra(SignUp.EMAIL);
        pass_txt = intent.getStringExtra(SignUp.PASSWORD);
        fname = findViewById(R.id.fname);
        phone_no = findViewById(R.id.pnumber);
        bio = findViewById(R.id.bio);
        profile_pic = findViewById(R.id.displayPicture);

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery = new Intent();
                iGallery.setType("image/*");
                iGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(iGallery,"Select Picture"),100);
            }
        });

        setProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fname_txt = fname.getText().toString();
                String phoneNumber_txt = phone_no.getText().toString();
                String bio_txt = bio.getText().toString();

                if(fname_txt.isEmpty())
                    Toast.makeText(getApplicationContext(), "You must provide a first name", Toast.LENGTH_SHORT).show();

                if(gender.isEmpty())
                    Toast.makeText(getApplicationContext(), "You must select a gender", Toast.LENGTH_SHORT).show();

                else {


                        progressBar.setVisibility(View.VISIBLE);
                        firebaseAuth.createUserWithEmailAndPassword(email_txt, pass_txt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressBar.setVisibility(View.VISIBLE);
                                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
                                DatabaseReference ref;
                                FirebaseUser CurrentUser;

                                if (task.isSuccessful()) {

                                    CurrentUser = task.getResult().getUser();
                                    ref = dbRef.child(CurrentUser.getUid());
                                    UserModel uM = new UserModel(email_txt, fname_txt, phoneNumber_txt, bio_txt, gender, uploadUri);
                                    ref.setValue(uM).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
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
                                                Intent intent = new Intent(SetProfile.this, MainPage.class);
                                                startActivity(intent);
                                                MainPage.viewContacts = new ContactsProvider();
                                            }

                                        }
                                    });


                                } else {

                                    Toast.makeText(getApplicationContext(), "Account Creation Failed", Toast.LENGTH_SHORT).show();
                                    finish();

                                }

                            }
                        });
                    }

                }

        });

        female_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                female_gender.setCardBackgroundColor(Color.parseColor("#DFCA0B"));
                male_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                other_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                gender = "Female";
            }
        });

        male_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                female_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                male_gender.setCardBackgroundColor(Color.parseColor("#DFCA0B"));
                other_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                gender = "Male";
            }
        });

        other_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                female_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                male_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                other_gender.setCardBackgroundColor(Color.parseColor("#DFCA0B"));
                gender = "Other";
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK)
        {
            if(requestCode==100)

            {
                galleryUri=(Uri)data.getData();  //save uri for further processing
                uploadImage();
            }
        }

    }

    void uploadImage(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();


        StorageReference mountainsRef = storageRef.child(email_txt+"_profile_pic.jpg");
        StorageReference mountainImagesRef = storageRef.child("images/"+email_txt+"-profile_pic.jpg");

        mountainsRef.getName().equals(mountainImagesRef.getName());
        mountainsRef.getPath().equals(mountainImagesRef.getPath());

        if(galleryUri!=null) {
            progressBar.setVisibility(View.VISIBLE);
            mountainsRef.putFile(galleryUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> resultTask = taskSnapshot.getStorage().getDownloadUrl();
                            resultTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadUri = uri.toString();
                                    profile_pic.setImageURI(galleryUri);
                                    progressBar.setVisibility(View.GONE);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    uploadUri = "";
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
                                }

                            });
                        }

                    });

        }


    }


}

