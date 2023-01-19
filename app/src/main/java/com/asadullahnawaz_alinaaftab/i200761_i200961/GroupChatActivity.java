package com.asadullahnawaz_alinaaftab.i200761_i200961;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class GroupChatActivity extends AppCompatActivity implements LinkMenuInterface,AddAttachmentInterface{

    private AddAttachmentInterface callback;
    private LinkMenuInterface lcallback;
    ImageView sendMessage;
    ImageView addAttachment;
    ImageView newImage;
    ImageView backBtn;
    EditText messageTxt;
    CardView newImageCard;
    TextView userName;
    String grpID;
    String messageImage="";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    GroupMessageAdapter groupMessageAdapter;
    ArrayList<GroupMessageModel> group_messages = new ArrayList<GroupMessageModel>();
    RecyclerView recyclerView;
    int index=-1;
    String senderName;
    Bitmap photoBitmap=null;
    Boolean imageAttached = false;
    ArrayList<String> recieverPlayerIDs = new ArrayList<String>();
    String groupName="";

    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        lcallback = this;
        callback = this;
        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.groupMessagesRV);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        groupMessageAdapter = new GroupMessageAdapter(group_messages,this,lcallback);
        recyclerView.setAdapter(groupMessageAdapter);

        registerForContextMenu(recyclerView);
        sendMessage = findViewById(R.id.group_sendMessage);
        addAttachment = findViewById(R.id.groupAddAttachment);
        backBtn = findViewById(R.id.group_backBtn);
        messageTxt = findViewById(R.id.group_message_txt);
        userName = findViewById(R.id.groupName);
        newImage = findViewById(R.id.groupNewImage);
        newImageCard = findViewById(R.id.groupNewImageCard);

        Intent intent = getIntent();
        grpID = intent.getStringExtra(ViewGroupAdapter.GID);

        newImageCard.setVisibility(View.GONE);

        firebaseDatabase.getReference("Group Chats").child(grpID).child("contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds:  snapshot.getChildren()){


                    if(ds.getValue(ContactModel.class).uid.equals(FirebaseAuth.getInstance().getUid()))
                        continue;

                    firebaseDatabase.getReference("Users").child(ds.getValue(ContactModel.class).uid).child("Player IDs").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for(DataSnapshot ds:  snapshot.getChildren()){

                                recieverPlayerIDs.add(ds.getValue(String.class));

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseDatabase.getReference("Group Chats").child(grpID).child("groupName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName.setText(snapshot.getValue(String.class));
                groupName = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderName = snapshot.getValue(UserModel.class).name;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseDatabase.getReference("Group Chats").child(grpID).child("Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                group_messages.add(snapshot.getValue(GroupMessageModel.class));
                groupMessageAdapter.notifyItemInserted(group_messages.size()-1);
                recyclerView.scrollToPosition(group_messages.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                for(int i=0; i<group_messages.size(); i++){

                    if(group_messages.get(i).messageID_senderSide.equals(snapshot.getKey())){
                        group_messages.get(i).messagetext = snapshot.getValue(MessageModel.class).messagetext;
                        groupMessageAdapter.notifyItemChanged(i);
                        break;
                    }

                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                for(int i=0; i<group_messages.size(); i++){

                    if(group_messages.get(i).messageID_senderSide.equals(snapshot.getKey())){
                        group_messages.remove(i);
                        groupMessageAdapter.notifyItemRemoved(i);
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
                finish();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = messageTxt.getText().toString();

                if(message.isEmpty() && !imageAttached)
                    Toast.makeText(getApplicationContext(), "Cannot Send Empty Message", Toast.LENGTH_SHORT).show();

                else {
                    GroupMessageModel gm = new GroupMessageModel(message,MainPage.getDateTime(),messageImage, FirebaseAuth.getInstance().getUid(),senderName,"");
                    databaseReference = firebaseDatabase.getReference("Group Chats").child(grpID).child("Messages").push();
                    gm.messageID_senderSide = databaseReference.getKey();
                    databaseReference.setValue(gm).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                JSONObject json = new JSONObject();
                                JSONObject j1 = new JSONObject();
                                JSONObject j2 = new JSONObject();

                                try {
                                    j1.put("en",message);
                                    j2.put("en","Message From "+senderName+" in Group ( "+groupName+" ) ");
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
                    newImageCard.setVisibility(View.GONE);
                    messageTxt.setText("");
                    messageImage = "";
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
    public void selectItem(int id) {
        index = id;
    }

    @Override
    public void setNewMessage(String newMessage, int i, String s, String r) {

        group_messages.get(i).messagetext = newMessage;
        firebaseDatabase.getReference("Group Chats").child(grpID).child("Messages").child(s).setValue(group_messages.get(i));

    }

    @Override
    public void selectAttachment(String type) {

        switch (type){

            case "photo":

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data!=null){

            photoBitmap = (Bitmap)data.getExtras().get("data");
            uploadImage();
            newImageCard.setVisibility(View.VISIBLE);
        }

    }

    void uploadImage(){


        String s = FirebaseDatabase.getInstance().getReference().push().getKey();
        StorageReference storageRef =  FirebaseStorage.getInstance().getReference().child(FirebaseAuth.getInstance().getUid()+"_"+s+"_.jpg");

        System.out.println("HERE");
        if(photoBitmap!=null) {
            System.out.println("HERE In");
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
                                    messageImage = uri.toString();
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

        if (v.getId()==R.id.groupMessagesRV) {

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

                    String senderSide = group_messages.get(index).messageID_senderSide;

                    boolean enable=true;
                    LocalDateTime old,curr;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        old = LocalDateTime.parse(group_messages.get(index).dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        curr = LocalDateTime.now();

                        if(ChronoUnit.MINUTES.between(old,curr)>=5){
                            enable=false;
                            Toast.makeText(getApplicationContext(), "Message is 5 Minutes Older", Toast.LENGTH_SHORT).show();
                        }

                        if(enable) {

                            firebaseDatabase.getReference("Group Chats").child(grpID).child("Messages").child(senderSide).removeValue();

                            if (index < group_messages.size())
                                groupMessageAdapter.notifyItemChanged(index);
                        }

                    }



                }


                return true;
            case R.id.editMessage:
                if(index!=-1) {

                    String senderSide = group_messages.get(index).messageID_senderSide;

                    boolean enable=true;
                    LocalDateTime old,curr;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        old = LocalDateTime.parse(group_messages.get(index).dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                        curr = LocalDateTime.now();

                        System.out.println("Diff = "+ChronoUnit.HOURS.between(old,curr));
                        if(ChronoUnit.MINUTES.between(old,curr)>=5){
                            enable=false;
                            Toast.makeText(getApplicationContext(), "Message is 5 Minutes Older", Toast.LENGTH_SHORT).show();
                        }



                    }

                    if(enable) {
                        EditMessageBottomsheet bottomSheet = new EditMessageBottomsheet(lcallback, index, senderSide, "");
                        bottomSheet.show(getSupportFragmentManager(), "Edit Message");
                    }

                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }
}