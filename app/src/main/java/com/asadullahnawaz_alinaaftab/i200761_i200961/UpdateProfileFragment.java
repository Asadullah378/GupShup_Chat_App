package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.HashMap;

public class UpdateProfileFragment extends Fragment implements View.OnClickListener {

    Button update_profile;
    EditText fname;
    EditText phone_number;
    EditText bio;
    ImageView profilePic;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    CardView female_gender;
    CardView male_gender;
    CardView other_gender;
    DatabaseReference ref;
    StorageReference storageRef;
    UserModel uM;
    String gender_txt = "";
    Uri galleryUri=null;
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_update_profile, container, false);

        progressBar = v.findViewById(R.id.update_progress_bar);

        fname = v.findViewById(R.id.edit_fname);
        phone_number = v.findViewById(R.id.edit_pnumber);
        bio = v.findViewById(R.id.edit_bio);
        profilePic = v.findViewById(R.id.edit_displayPicture);
        female_gender = v.findViewById(R.id.edit_female_gender);
        male_gender = v.findViewById(R.id.edit_male_gender);
        other_gender = v.findViewById(R.id.edit_other_gender);
        update_profile = v.findViewById(R.id.edit_prof);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        female_gender.setOnClickListener(this);
        male_gender.setOnClickListener(this);
        other_gender.setOnClickListener(this);
        update_profile.setOnClickListener(this);
        profilePic.setOnClickListener(this);

        female_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        other_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        male_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid());


        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                uM = snapshot.getValue(UserModel.class);
                fname.setText(uM.name);
                phone_number.setText(uM.phone_number);
                bio.setText(uM.bio);

                if(uM.gender.equals("Male")){
                    male_gender.setCardBackgroundColor(Color.parseColor("#DFCA0B"));
                    gender_txt = "Male";
                }

                else if(uM.gender.equals("Female")){
                    female_gender.setCardBackgroundColor(Color.parseColor("#DFCA0B"));
                    gender_txt = "Female";
                }

                else if(uM.gender.equals("Other")){
                    other_gender.setCardBackgroundColor(Color.parseColor("#DFCA0B"));
                    gender_txt = "Other";
                }

                if(!uM.imageUrl.equals("")){
//                    new DownloadImageTask(profilePic)
//                            .execute(uM.imageUrl);
                Glide.with(getActivity()).load(uM.imageUrl).into(profilePic);
                }
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.edit_prof:

                String fname_txt = fname.getText().toString();

                if(fname_txt.isEmpty())
                    Toast.makeText(getContext(), "First Name cannot be Empty", Toast.LENGTH_SHORT).show();

                else {
                    uM.name = fname_txt;
                    uM.phone_number = phone_number.getText().toString();
                    uM.gender = gender_txt;
                    uM.bio = bio.getText().toString();

                    HashMap map = new HashMap();
                    map.put("name",uM.name);
                    map.put("phone_number",uM.phone_number);
                    map.put("gender",uM.gender);
                    map.put("bio",uM.bio);
                    map.put("imageUrl",uM.imageUrl);
                    ref.updateChildren(map);
                }
                break;

            case R.id.edit_female_gender:
                female_gender.setCardBackgroundColor(Color.parseColor("#DFCA0B"));
                male_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                other_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                gender_txt = "Female";
                break;

            case R.id.edit_male_gender:
                female_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                male_gender.setCardBackgroundColor(Color.parseColor("#DFCA0B"));
                other_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                gender_txt = "Male";
                break;

            case R.id.edit_other_gender:
                female_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                male_gender.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                other_gender.setCardBackgroundColor(Color.parseColor("#DFCA0B"));
                gender_txt = "Other";
                break;

            case R.id.edit_displayPicture:
                Intent iGallery = new Intent();
                iGallery.setType("image/*");
                iGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(iGallery,"Select Picture"),100);
                break;


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==getActivity().RESULT_OK)
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


        StorageReference mountainsRef = storageRef.child(uM.email+"_profile_pic.jpg");

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
                                    uM.imageUrl = uri.toString();
                                    profilePic.setImageURI(galleryUri);
                                    progressBar.setVisibility(View.GONE);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getActivity().getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
                                }

                            });
                        }

                    });

        }


    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}

