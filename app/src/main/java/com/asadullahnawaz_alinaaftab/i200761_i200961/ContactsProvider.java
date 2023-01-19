package com.asadullahnawaz_alinaaftab.i200761_i200961;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsProvider {

    ArrayList<ContactModel>  all_contacts = new ArrayList<>();

    void fetchContacts(ViewContactAdapter VCA){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid()).child("Contacts");
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
                        for(int i=0; i<all_contacts.size();i++){

                            if(all_contacts.get(i).uid.equals(snapshot.getKey())){

                                all_contacts.set(i, cM);
                                VCA.notifyItemChanged(i);
                                newItem = false;
                                break;
                            }
                        }

                        if (newItem) {
                            all_contacts.add(cM);
                            VCA.notifyItemInserted(all_contacts.size());
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
    }

}
