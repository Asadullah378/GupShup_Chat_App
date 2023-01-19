package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class SearchContactAdapter extends RecyclerView.Adapter<SearchContactAdapter.ViewHolder> {
    ArrayList<ContactModel> contact;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public SearchContactAdapter(ArrayList<ContactModel> contact, Context context) {
        this.contact = contact;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.search_contact_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.contactName.setText(contact.get(position).name);
        holder.contactPhone.setText(contact.get(position).phone_number);

        int index = position;
        Optional<ContactModel> cm;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm = MainPage.viewContacts.all_contacts.stream().filter(c -> c.uid.equals(contact.get(position).uid)).findFirst();


            if (cm.isPresent()) {

                holder.addContact.setText("Added");
                holder.addContact.setEnabled(false);

            }
        }




        holder.addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Contacts").push();

                databaseReference.setValue(contact.get(index).uid).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        holder.addContact.setText("Added");
                        holder.addContact.setEnabled(false);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });

        if(!contact.get(position).url.isEmpty()) {
//            new DownloadImageTask(holder.contactImage).execute(contact.get(position).url);
            Glide.with(context).load(contact.get(position).url).into(holder.contactImage);

        }


    }

    @Override
    public int getItemCount() {
        return contact.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImage;
        TextView contactName;
        TextView contactPhone;
        Button addContact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            contactImage = itemView.findViewById(R.id.contactPicture);
            contactName = itemView.findViewById(R.id.contactName);
            contactPhone = itemView.findViewById(R.id.contactPhone);
            addContact = itemView.findViewById(R.id.addContact);
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
