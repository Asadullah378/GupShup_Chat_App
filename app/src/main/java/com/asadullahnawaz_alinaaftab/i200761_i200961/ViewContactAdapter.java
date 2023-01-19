package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;

public class ViewContactAdapter extends RecyclerView.Adapter<ViewContactAdapter.ViewHolder> {
    ArrayList<ContactModel> contact;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public static final String UID = "com.asadullahnawaz_alinaaftab.i200761_i200961.UID";
    public ViewContactAdapter(ArrayList<ContactModel> contact, Context context) {
        this.contact = contact;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.view_contact_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(contact.get(position).name);
        holder.phone_number.setText(contact.get(position).phone_number);
        holder.bio.setText(contact.get(position).bio);

        String gender = contact.get(position).gender;

        if(gender.equals("Male"))
            holder.gender.setImageResource(R.drawable.male_avatar);

        else if(gender.equals("Female"))
            holder.gender.setImageResource(R.drawable.female_avatar);

        else if(gender.equals("Other"))
            holder.gender.setImageResource(R.drawable.other_avatar);

        if(!contact.get(position).url.isEmpty()) {
//            new DownloadImageTask(holder.profilePic).execute(contact.get(position).url);
            Glide.with(context).load(contact.get(position).url).into(holder.profilePic);
        }

        holder.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra(UID,contact.get(holder.getAdapterPosition()).uid);
                context.startActivity(intent);

            }
        });

        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.get(holder.getAdapterPosition()).phone_number));
                context.startActivity(intentDial);
            }
        });

        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Contacts");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){

                            if(ds.getValue(String.class).equals(contact.get(holder.getAdapterPosition()).uid)) {

                                int index= holder.getAdapterPosition();
                                ds.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                                MainPage.viewContacts.all_contacts.remove(index);
                                                notifyItemRemoved(index);

                                        }
                                    }
                                });
                                break;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        holder.showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.moreButtons.getVisibility()==View.GONE){

                    holder.moreButtons.setVisibility(view.VISIBLE);
                    holder.showMore.setImageResource(R.drawable.less_icon);
                }

                else{
                    holder.moreButtons.setVisibility(view.GONE);
                    holder.showMore.setImageResource(R.drawable.more_icon);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return contact.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView phone_number;
        TextView bio;
        CardView moreButtons;
        ImageView profilePic;
        ImageView gender;
        ImageView chatBtn;
        ImageView callBtn;
        ImageView delBtn;
        ImageView showMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.viewName);
            phone_number = itemView.findViewById(R.id.viewPhone);
            bio = itemView.findViewById(R.id.viewBio);
            gender = itemView.findViewById(R.id.viewGender);
            chatBtn = itemView.findViewById(R.id.chatContact);
            callBtn = itemView.findViewById(R.id.callContact);
            delBtn = itemView.findViewById(R.id.deleteContact);
            profilePic = itemView.findViewById(R.id.viewPicture);
            showMore = itemView.findViewById(R.id.showMore);
            moreButtons  = itemView.findViewById(R.id.buttonsCard);

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
