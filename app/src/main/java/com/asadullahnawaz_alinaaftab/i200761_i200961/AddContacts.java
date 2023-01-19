package com.asadullahnawaz_alinaaftab.i200761_i200961;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddContacts extends AppCompatActivity {

    ImageView search_btn;
    EditText search_txt;
    RecyclerView recyclerView;
    SearchContactAdapter contactAdapter;
    ArrayList<ContactModel> contacts = new ArrayList<ContactModel>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacts);

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("Users");
        recyclerView = findViewById(R.id.RecyclerViewAddContacts);
        backBtn = findViewById(R.id.backBtn);

        search_btn = findViewById(R.id.search_btn);
        search_txt = findViewById(R.id.search_txt);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        contactAdapter = new SearchContactAdapter(contacts,AddContacts.this);
//        recyclerView.setAdapter(contactAdapter);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String search = search_txt.getText().toString();

                if(search.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No Text To Search", Toast.LENGTH_SHORT).show();
                }

                else{

                    contacts.clear();
//                    contactAdapter.notifyDataSetChanged();
                    contactAdapter = new SearchContactAdapter(contacts,AddContacts.this);
                    recyclerView.setAdapter(contactAdapter);

                    databaseReference.orderByChild("name").startAt(search).endAt(search+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for(DataSnapshot ds : snapshot.getChildren()){

                                if(ds.getKey().equals(FirebaseAuth.getInstance().getUid()))
                                    continue;

                                ContactModel cM = new ContactModel(ds.getKey(),ds.child("name").getValue(String.class), ds.child("phone_number").getValue(String.class) ,ds.child("email").getValue(String.class),ds.child("bio").getValue(String.class),ds.child("gender").getValue(String.class),ds.child("imageUrl").getValue(String.class));
                                contacts.add(cM);
                                contactAdapter.notifyItemInserted(contacts.size()-1);

                            }
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