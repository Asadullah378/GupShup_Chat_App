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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    ArrayList<ChatModel> chat;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public ChatAdapter(ArrayList<ChatModel> chat, Context context) {
        this.chat = chat;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.chat_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.chatName.setText(chat.get(position).name);

        if(chat.get(holder.getAdapterPosition()).unseen==0){

            holder.unseenCard.setVisibility(View.GONE);

        }

        else{
            holder.unseenCard.setVisibility(View.VISIBLE);
            holder.unseenCount.setText(""+chat.get(holder.getAdapterPosition()).unseen);
        }

        String lastM = chat.get(position).lastMessage;

        if(!lastM.isEmpty()) {
            if (chat.get(holder.getAdapterPosition()).you)
                lastM = "You: " + lastM;

            if (lastM.length() > 20)
                lastM = lastM.substring(0, 20) + "...";

            holder.chatLastMessage.setText(lastM);
        }
        else{
            holder.chatLastMessage.setText("");
        }
        if(!chat.get(holder.getAdapterPosition()).lastTime.isEmpty()) {

                holder.chatTime.setText(MainPage.formatDate(chat.get(position).lastTime, "hh:mm a"));

        }
        else{
            holder.chatTime.setText("");
        }

        if(!chat.get(holder.getAdapterPosition()).imageUrl.isEmpty()) {
//            new DownloadImageTask(holder.chatPicture).execute(chat.get(position).imageUrl);
            Glide.with(context).load(chat.get(position).imageUrl).into(holder.chatPicture);

        }

        holder.chatCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra(ViewContactAdapter.UID,chat.get(holder.getAdapterPosition()).uid);
                context.startActivity(intent);
            }
        });

        holder.chatDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase = FirebaseDatabase.getInstance();
                databaseReference = firebaseDatabase.getReference("Chats").child(FirebaseAuth.getInstance().getUid()).child(chat.get(holder.getAdapterPosition()).uid);
                int index = holder.getAdapterPosition();
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){


//                            chat.remove(index);
//                            notifyItemRemoved(index);

                        }
                    }
                });


            }
        });

        holder.chatMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.delCard.getVisibility()==View.VISIBLE){

                    holder.delCard.setVisibility(View.GONE);
                    holder.chatMore.setImageResource(R.drawable.more_icon);
                }

                else{
                    holder.delCard.setVisibility(View.VISIBLE);
                    holder.chatMore.setImageResource(R.drawable.less_icon);

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView chatDelete;
        TextView chatName;
        TextView chatLastMessage;
        TextView chatTime;
        ImageView chatPicture;
        LinearLayout chatCard;
        TextView unseenCount;
        CardView unseenCard;
        ImageView chatMore;
        CardView delCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            chatDelete = itemView.findViewById(R.id.chat_delete);
            chatName = itemView.findViewById(R.id.chat_name);
            chatLastMessage = itemView.findViewById(R.id.chat_lastMessage);
            chatTime = itemView.findViewById(R.id.chat_time);
            chatPicture = itemView.findViewById(R.id.chat_picture);
            chatCard = itemView.findViewById(R.id.chatCard);
            unseenCard = itemView.findViewById(R.id.unseenCard);
            unseenCount = itemView.findViewById(R.id.unseenCount);
            chatMore = itemView.findViewById(R.id.chat_more);
            delCard = itemView.findViewById(R.id.chat_deleteCard);

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
