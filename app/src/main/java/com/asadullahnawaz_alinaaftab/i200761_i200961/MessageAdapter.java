package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    ArrayList<MessageModel> messages;
    Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    int screen_width;
    LinkMenuInterface lcallback;

    public MessageAdapter(ArrayList<MessageModel> messages, Context context, LinkMenuInterface lcallback) {
        this.messages = messages;
        this.context = context;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AppCompatActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_width = displayMetrics.widthPixels;
        this.lcallback = lcallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.group_message_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.senderName.setVisibility(View.GONE);
        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) holder.messageLayout.getLayoutParams();
        LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) holder.messageTime.getLayoutParams();

        if(messages.get(holder.getAdapterPosition()).senderUid.equals(FirebaseAuth.getInstance().getUid())) {
            holder.messageLayout.setCardBackgroundColor(Color.parseColor("#6210CC"));
            lp1.gravity = Gravity.RIGHT;
            lp2.gravity = Gravity.RIGHT;
        }
        else {
            holder.messageLayout.setCardBackgroundColor(Color.parseColor("#6C6771"));
            lp1.gravity = Gravity.LEFT;
            lp2.gravity = Gravity.LEFT;
        }

        holder.messageLayout.setLayoutParams(lp1);
        holder.messageTime.setLayoutParams(lp2);
        holder.messageLayout.setLayoutParams(holder.messageLayout.getLayoutParams());
        holder.messageTime.setText(MainPage.formatDate(messages.get(holder.getAdapterPosition()).dateTime,"hh:mm a"));

        String s1 ="";
        String s2 ="";
        LocalDateTime mdate1;
        LocalDateTime mdate2;
        LocalDateTime cDate;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cDate = LocalDateTime.now();
            mdate1 = LocalDateTime.parse(messages.get(holder.getAdapterPosition()).dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            if(ChronoUnit.DAYS.between(mdate1,cDate) == 0)
                s1 = "Today";

            else if(ChronoUnit.DAYS.between(mdate1,cDate) == 1)
                s1 = "Yesterday";

            else if(ChronoUnit.DAYS.between(mdate1,cDate) < 7)
                s1 = MainPage.formatDate(messages.get(holder.getAdapterPosition()).dateTime,"E");

            else if(ChronoUnit.YEARS.between(mdate1,cDate) < 1)
                s1 = MainPage.formatDate(messages.get(holder.getAdapterPosition()).dateTime,"E, MMM d");

            else
                s1 = MainPage.formatDate(messages.get(holder.getAdapterPosition()).dateTime,"MMM dd, yyyy");


            if(holder.getAdapterPosition()-1 >=0 ) {
                mdate2 = LocalDateTime.parse(messages.get(holder.getAdapterPosition() -1 ).dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                if (ChronoUnit.DAYS.between(mdate2, cDate) == 0)
                    s2 = "Today";

                else if (ChronoUnit.DAYS.between(mdate2, cDate) == 1)
                    s2 = "Yesterday";

                else if (ChronoUnit.DAYS.between(mdate2, cDate) < 7)
                    s2 = MainPage.formatDate(messages.get(holder.getAdapterPosition()).dateTime, "E");

                else if (ChronoUnit.YEARS.between(mdate2, cDate) < 1)
                    s2 = MainPage.formatDate(messages.get(holder.getAdapterPosition()).dateTime, "E, MMM d");

                else
                    s2 = MainPage.formatDate(messages.get(holder.getAdapterPosition()).dateTime, "MMM dd, yyyy");

            }

            if(s1.equals(s2))
                holder.messageDate.setVisibility(View.GONE);
            else {
                holder.messageDate.setText(s1);
            }

        }



        holder.messageText.setText(messages.get(position).messagetext);

        if(messages.get(holder.getAdapterPosition()).imageUrl.isEmpty()){

            holder.messageImageCard.setVisibility(View.GONE);
        }

        else{
            holder.messageImageCard.setVisibility(View.VISIBLE);
//            new DownloadImageTask(holder.messageImage).execute(messages.get(position).imageUrl);
            Glide.with(context).load(messages.get(position).imageUrl).into(holder.messageImage);
        }


        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(messages.get(holder.getAdapterPosition()).senderUid.equals(FirebaseAuth.getInstance().getUid())) {
                    lcallback.selectItem(holder.getAdapterPosition());
                    return false;
                }
                else{
                    return true;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView messageDate;
        TextView senderName;
        ImageView messageImage;
        CardView messageImageCard;
        CardView messageDateCard;
        CardView messageLayout;
        TextView messageTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageImage = itemView.findViewById(R.id.messageImage);
            messageText = itemView.findViewById(R.id.messageTxt);
            messageDateCard = itemView.findViewById(R.id.dateCard);
            messageImageCard = itemView.findViewById(R.id.messageImageCard);
            messageDate = itemView.findViewById(R.id.messageDate);
            messageLayout = itemView.findViewById(R.id.messageLayout);
            messageTime = itemView.findViewById(R.id.messageTime);
            senderName = itemView.findViewById(R.id.messageSender);
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
