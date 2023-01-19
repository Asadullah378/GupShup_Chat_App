package com.asadullahnawaz_alinaaftab.i200761_i200961;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddGroups extends AppCompatActivity {

    ImageView back_btn;
    Button createGroup;
    RecyclerView recyclerView;
    EditText groupName;
    public AddGroupAdapter addGroupAdapter;
    ArrayList<ContactModel> contacts = new ArrayList<ContactModel>();
    ArrayList<ContactModel> selectedContacts = new ArrayList<ContactModel>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_groups);

        groupName = findViewById(R.id.grp_name);
        createGroup = findViewById(R.id.createGroup);
        back_btn = findViewById(R.id.grp_back_btn);
        recyclerView = findViewById(R.id.contactsRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addGroupAdapter = new AddGroupAdapter(contacts,selectedContacts,AddGroups.this);
        recyclerView.setAdapter(addGroupAdapter);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Contacts");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String uid = snapshot.getValue(String.class);

                DatabaseReference newRef = firebaseDatabase.getReference("Users").child(uid);

                newRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserModel uM = snapshot.getValue(UserModel.class);
                        ContactModel cM = new ContactModel(snapshot.getKey(), uM.name,uM.phone_number, uM.email, uM.bio, uM.gender, uM.imageUrl);

                        boolean newItem = true;
                        for(int i=0; i<contacts.size();i++){

                            if(contacts.get(i).uid.equals(snapshot.getKey())){

                                contacts.set(i, cM);
                                addGroupAdapter.notifyItemChanged(i);
                                newItem = false;
                                break;
                            }
                        }

                        if (newItem) {
                            contacts.add(cM);
                            addGroupAdapter.notifyItemInserted(contacts.size());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              String name = groupName.getText().toString();

              if(name.isEmpty())
                  Toast.makeText(getApplicationContext(), "Add a Group Name", Toast.LENGTH_SHORT).show();

              else {
                  DatabaseReference dbRef = firebaseDatabase.getReference("Group Chats").push();

                  firebaseDatabase.getReference("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {
                          UserModel um = snapshot.getValue(UserModel.class);
                          selectedContacts.add(new ContactModel(FirebaseAuth.getInstance().getUid(), um.name,um.phone_number,um.email,um.bio,um.gender,um.imageUrl));
                          GroupChatModel gcm = new GroupChatModel(name, selectedContacts);
                          gcm.grpId = dbRef.getKey();
                          dbRef.setValue(gcm);
                          finish();

                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                      }
                  });


              }

            }
        });
    }
}