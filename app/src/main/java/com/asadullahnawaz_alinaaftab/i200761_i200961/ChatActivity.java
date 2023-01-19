package com.asadullahnawaz_alinaaftab.i200761_i200961;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity implements AddAttachmentInterface, LinkMenuInterface{

    private AddAttachmentInterface callback;
    private LinkMenuInterface lcallback;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference_get;
    DatabaseReference databaseReference_set1;
    DatabaseReference databaseReference_set2;
    ArrayList<MessageModel> messages;
    String destUID;
    String srcUID;
    ImageView sendMessage;
    ImageView addAttachment;
    ImageView backBtn;
    EditText messageTxt;
    TextView userName;
    ImageView userImage;
    Bitmap photoBitmap=null;
    ImageView newImage;
    CardView newImageCard;
    String imageUrl="";
    String senderName;
    Uri uri=null;
    ArrayList<String> recieverPlayerIDs = new ArrayList<String>();
    int index=-1;
    boolean imageAttached = false;
    boolean activeStatus;
    private static final int CAMERA_REQUEST = 1888;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        callback = this;
        lcallback = this;
        Intent intent = getIntent();

        newImage = findViewById(R.id.newImage);
        newImageCard = findViewById(R.id.newImageCard);
        destUID = intent.getStringExtra(ViewContactAdapter.UID);
        srcUID = FirebaseAuth.getInstance().getUid();
        sendMessage = findViewById(R.id.sendMessage);
        addAttachment = findViewById(R.id.addAttachment);
        messageTxt = findViewById(R.id.message_txt);
        backBtn = findViewById(R.id.chat_backBtn);
        firebaseDatabase = FirebaseDatabase.getInstance();
        messages = new ArrayList<MessageModel>();
        recyclerView = findViewById(R.id.messagesRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(0);
        messageAdapter = new MessageAdapter(messages,this,lcallback);
        recyclerView.setAdapter(messageAdapter);
        userName = findViewById(R.id.userName);
        userImage = findViewById(R.id.userPicture);

        registerForContextMenu(recyclerView);
        newImageCard.setVisibility(View.GONE);
        firebaseDatabase.getReference("Users").child(destUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel uM = snapshot.getValue(UserModel.class);

                userName.setText(uM.name);

                if(!uM.imageUrl.isEmpty()) {
//                    new DownloadImageTask(userImage).execute(uM.imageUrl);
                    Glide.with(ChatActivity.this).load(uM.imageUrl).into(userImage);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseDatabase.getReference("Users").child(destUID).child("Player IDs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren())
                    recieverPlayerIDs.add(ds.getValue(String.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        firebaseDatabase.getReference("Users").child(srcUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel uM = snapshot.getValue(UserModel.class);

                senderName = uM.name;


                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference_get = firebaseDatabase.getReference("Chats").child(srcUID).child(destUID);

        databaseReference_get.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                MessageModel m = snapshot.getValue(MessageModel.class);

                if(activeStatus)
                    m.seen = "true";

                messages.add(m);
                messageAdapter.notifyItemInserted(messages.size()-1);
                recyclerView.scrollToPosition(messages.size()-1);
                firebaseDatabase.getReference("Chats").child(srcUID).child(destUID).child(snapshot.getKey()).setValue(m);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                for(int i=0; i<messages.size(); i++){

                    if(messages.get(i).messageID_senderSide.equals(snapshot.getKey()) || messages.get(i).messageID_recieverSide.equals(snapshot.getKey())){
                        messages.get(i).messagetext = snapshot.getValue(MessageModel.class).messagetext;
                        messageAdapter.notifyItemChanged(i);
                        break;
                    }

                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                for(int i=0; i<messages.size(); i++){

                    if(messages.get(i).messageID_senderSide.equals(snapshot.getKey()) || messages.get(i).messageID_recieverSide.equals(snapshot.getKey())){
                        messages.remove(i);
                        messageAdapter.notifyItemRemoved(i);
                        break;
                    }

                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();;
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = messageTxt.getText().toString();

                if(message.isEmpty() && !imageAttached){
                    Toast.makeText(getApplicationContext(), "Cannot Send Empty Message", Toast.LENGTH_SHORT).show();
                }

                else{

                    MessageModel msg1 = new MessageModel(message,MainPage.getDateTime(),imageUrl,srcUID,"","","true");
                    MessageModel msg2 = new MessageModel(message,MainPage.getDateTime(),imageUrl,srcUID,"","","false");
                    databaseReference_set1 = firebaseDatabase.getReference("Chats").child(srcUID).child(destUID).push();
                    databaseReference_set2 = firebaseDatabase.getReference("Chats").child(destUID).child(srcUID).push();

                    msg1.messageID_senderSide = databaseReference_set1.getKey();
                    msg1.messageID_recieverSide = databaseReference_set2.getKey();

                    msg2.messageID_senderSide = databaseReference_set1.getKey();
                    msg2.messageID_recieverSide = databaseReference_set2.getKey();

                    databaseReference_set1.setValue(msg1);
                    databaseReference_set2.setValue(msg2).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                JSONObject json = new JSONObject();
                                JSONObject j1 = new JSONObject();
                                JSONObject j2 = new JSONObject();

                                try {
                                    j1.put("en",message);
                                    j2.put("en","Message From "+senderName);
                                    json.put("contents",j1);
                                    json.put("headings",j2);
                                    json.put("include_player_ids", new JSONArray(recieverPlayerIDs.toArray()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                OneSignal.postNotification(json, new OneSignal.PostNotificationResponseHandler() {
                                    @Override
                                    public void onSuccess(JSONObject jsonObject) {
                                        System.out.println("NOTIFICATION SENT");
                                    }

                                    @Override
                                    public void onFailure(JSONObject jsonObject) {
                                        System.out.println("NOTIFICATION NOT SENT "+jsonObject.toString());
                                    }
                                });

                            }
                            }


                    });
                    messageTxt.setText("");
                    imageUrl="";
                    newImageCard.setVisibility(View.GONE);
                    imageAttached=false;

                }

            }
        });

        addAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAttachmentBottomsheet bottomSheet = new AddAttachmentBottomsheet(callback);
                bottomSheet.show(getSupportFragmentManager(),"Add Attachment");
            }
        });


    }

    @Override
    public void selectAttachment(String type) {

        switch (type){

            case "photo":

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;

            case "gallery":

                Intent iGallery = new Intent();
                iGallery.setType("image/*");
                iGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(iGallery,"Select Picture"),100);
                break;

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data!=null){

            if(requestCode==CAMERA_REQUEST) {
                photoBitmap = (Bitmap) data.getExtras().get("data");
                uploadImage();
                newImageCard.setVisibility(View.VISIBLE);
            }
            else if(requestCode==100){

                uri = (Uri)data.getData();
                try {
                    photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    uploadImageUri();
                    newImageCard.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    void uploadImageUri(){

        String s = FirebaseDatabase.getInstance().getReference().push().getKey();
        StorageReference storageRef =  FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()+"_"+s+"_.jpg");

        if(uri!=null) {

            storageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> resultTask = taskSnapshot.getStorage().getDownloadUrl();
                            resultTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    imageAttached = true;
                                    newImage.setImageBitmap(photoBitmap);


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    newImageCard.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();

                                }

                            });
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            newImageCard.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }
    void uploadImage(){


        String s = FirebaseDatabase.getInstance().getReference().push().getKey();
        StorageReference storageRef =  FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()+"_"+s+"_.jpg");

            if(photoBitmap!=null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                storageRef.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> resultTask = taskSnapshot.getStorage().getDownloadUrl();
                                resultTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUrl = uri.toString();
                                        imageAttached = true;
                                        newImage.setImageBitmap(photoBitmap);


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        newImageCard.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();

                                    }

                                });
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                newImageCard.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();

                            }
                        });
            }



        }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId()==R.id.messagesRV) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.deleteMessage:
                if(index!=-1) {

                    String senderSide = messages.get(index).messageID_senderSide;
                    String recieverSide = messages.get(index).messageID_recieverSide;

                    boolean enable=true;
                    LocalDateTime old,curr;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        old = LocalDateTime.parse(messages.get(index).dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        curr = LocalDateTime.now();

                        if(ChronoUnit.MINUTES.between(old,curr)>=5){
                            enable=false;
                            Toast.makeText(getApplicationContext(), "Message is 5 Minutes Older", Toast.LENGTH_SHORT).show();
                        }



                    }


                    if(enable) {
                        if (messages.get(index).senderUid == srcUID) {
                            firebaseDatabase.getReference("Chats").child(srcUID).child(destUID).child(senderSide).removeValue();
                            firebaseDatabase.getReference("Chats").child(destUID).child(srcUID).child(recieverSide).removeValue();
                        } else {
                            firebaseDatabase.getReference("Chats").child(srcUID).child(destUID).child(recieverSide).removeValue();
                            firebaseDatabase.getReference("Chats").child(destUID).child(srcUID).child(senderSide).removeValue();

                        }

//                        messages.remove(index);
//                        messageAdapter.notifyItemRemoved(index);

                        if (index < messages.size())
                            messageAdapter.notifyItemChanged(index);
                    }
                }


                return true;
            case R.id.editMessage:
                if(index!=-1) {

                    String senderSide = messages.get(index).messageID_senderSide;
                    String recieverSide = messages.get(index).messageID_recieverSide;

                    boolean enable=true;
                    LocalDateTime old,curr;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        old = LocalDateTime.parse(messages.get(index).dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        curr = LocalDateTime.now();

                        System.out.println("Diff = "+ChronoUnit.HOURS.between(old,curr));
                        if(ChronoUnit.MINUTES.between(old,curr)>=5){
                            enable=false;
                            Toast.makeText(getApplicationContext(), "Message is 5 Minutes Older", Toast.LENGTH_SHORT).show();
                        }



                    }

                    if(enable) {
                        EditMessageBottomsheet bottomSheet = new EditMessageBottomsheet(lcallback, index, senderSide, recieverSide);
                        bottomSheet.show(getSupportFragmentManager(), "Edit Message");
                    }

                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void selectItem(int id) {
        index = id;
    }

    @Override
    public void setNewMessage(String newMessage, int i, String s, String r) {


        messages.get(i).messagetext = newMessage;

        if(messages.get(i).senderUid == srcUID) {
            firebaseDatabase.getReference("Chats").child(srcUID).child(destUID).child(s).setValue(messages.get(i));
            firebaseDatabase.getReference("Chats").child(destUID).child(srcUID).child(r).setValue(messages.get(i));
        }
        else{
            firebaseDatabase.getReference("Chats").child(srcUID).child(destUID).child(r).setValue(messages.get(i));
            firebaseDatabase.getReference("Chats").child(destUID).child(srcUID).child(s).setValue(messages.get(i));

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        activeStatus = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        activeStatus=false;
    }
}