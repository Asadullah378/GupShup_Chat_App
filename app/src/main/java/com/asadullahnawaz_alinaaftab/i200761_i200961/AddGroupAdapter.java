package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class AddGroupAdapter extends RecyclerView.Adapter<AddGroupAdapter.ViewHolder> {
    ArrayList<ContactModel> contact;
    ArrayList<ContactModel> selected_contacts;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public AddGroupAdapter(ArrayList<ContactModel> contact,ArrayList<ContactModel> selected_contacts, Context context) {
        this.contact = contact;
        this.selected_contacts = selected_contacts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.gc_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.cname.setText(contact.get(position).name);
        holder.cphone.setText(contact.get(position).phone_number);

        holder.selectorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.selectedCard.getCardBackgroundColor().getDefaultColor() == Color.parseColor("#FFFFFF")){

                    holder.selectedCard.setCardBackgroundColor(Color.parseColor("#FFA000"));
                    selected_contacts.add(contact.get(holder.getAdapterPosition()));
                }

                else{
                    holder.selectedCard.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

                    for(int i=0; i<selected_contacts.size(); i++){
                        if(contact.get(holder.getAdapterPosition()).uid.equals(selected_contacts.get(i).uid)) {
                            selected_contacts.remove(i);
                            break;
                        }
                    }

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return contact.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView selectorCard;
        CardView selectedCard;
        TextView cname;
        TextView cphone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selectorCard = itemView.findViewById(R.id.contactCard);
            selectedCard = itemView.findViewById(R.id.contactSelected);
            cname = itemView.findViewById(R.id.cname);
            cphone = itemView.findViewById(R.id.cphpne);
        }
    }

}
