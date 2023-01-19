package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.ArrayList;

public class ViewGroupAdapter extends RecyclerView.Adapter<ViewGroupAdapter.ViewHolder> {
    ArrayList<GroupChatModel> groupChat;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public static final String GID = "com.asadullahnawaz_alinaaftab.i200761_i200961.GID";

    public ViewGroupAdapter(ArrayList<GroupChatModel> groupChat, Context context) {
        this.groupChat = groupChat;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.group_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.chatName.setText(groupChat.get(position).groupName);

        String lastM = groupChat.get(holder.getAdapterPosition()).grp_lastMessage;

        if (!lastM.isEmpty()){


            if(groupChat.get(holder.getAdapterPosition()).you)
                lastM  =  "You: "+lastM;
            else
                lastM  = groupChat.get(holder.getAdapterPosition()).grp_lastSender +": "+lastM;

            if (lastM.length() > 22)
                lastM = lastM.substring(0, 22) + "...";

        holder.chatLastMessage.setText(lastM);
    }
    else{
        holder.chatLastMessage.setText("");
        }

        if(!groupChat.get(holder.getAdapterPosition()).grp_lastTime.isEmpty()) {

            holder.chatTime.setText(MainPage.formatDate(groupChat.get(position).grp_lastTime, "hh:mm a"));

        }

        else{
            holder.chatTime.setText("");
        }

        holder.chatCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,GroupChatActivity.class);
                intent.putExtra(GID,groupChat.get(holder.getAdapterPosition()).grpId);
                context.startActivity(intent);
            }
        });

        holder.exitGrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Group Chats").child(groupChat.get(holder.getAdapterPosition()).grpId).child("contacts");

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren()){

                            if(ds.getValue(ContactModel.class).uid.equals(FirebaseAuth.getInstance().getUid())){

                                firebaseDatabase.getReference("Group Chats").child(groupChat.get(holder.getAdapterPosition()).grpId).child("contacts").child(ds.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            groupChat.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());

                                        }

                                    }
                                });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





            }
        });



    }

    @Override
    public int getItemCount() {
        return groupChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView chatName;
        TextView chatLastMessage;
        TextView chatTime;
        LinearLayout chatCard;
        ImageView exitGrp;
        CardView delCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chatName = itemView.findViewById(R.id.view_grp_name);
            chatLastMessage = itemView.findViewById(R.id.view_grp_lastMessage);
            chatTime = itemView.findViewById(R.id.view_grp_lasttime);
            chatCard = itemView.findViewById(R.id.grp_chatCard);
            exitGrp = itemView.findViewById(R.id.exit);

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
