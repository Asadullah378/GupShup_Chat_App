package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupChatFragment extends Fragment implements View.OnClickListener{

    RecyclerView recyclerView;
    ViewGroupAdapter viewGroupAdapter;
    ArrayList<GroupChatModel> group_chats = new ArrayList<GroupChatModel>();
    FloatingActionButton addGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_group_chat, container, false);

        recyclerView = v.findViewById(R.id.groupsRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewGroupAdapter = new ViewGroupAdapter(group_chats,getContext());
        recyclerView.setAdapter(viewGroupAdapter);
        addGroup = v.findViewById(R.id.add_grp_btn);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Group Chats");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                GroupChatModel gcm = snapshot.getValue(GroupChatModel.class);


                for(int i=0; i<gcm.contacts.size(); i++){

                    if(gcm.contacts.get(i) != null && gcm.contacts.get(i).uid.equals(FirebaseAuth.getInstance().getUid())){

                            int index=group_chats.size();
                            group_chats.add(gcm);;
                            viewGroupAdapter.notifyItemInserted(group_chats.size());

                        firebaseDatabase.getReference("Group Chats").child(snapshot.getKey()).child("Messages").limitToLast(1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.hasChildren()) {
                                    GroupMessageModel gmm = new GroupMessageModel();
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        gmm = ds.getValue(GroupMessageModel.class);

                                    }

                                    if(index>=0 && index<group_chats.size()) {
                                        group_chats.get(index).grp_lastMessage = gmm.messagetext;
                                        group_chats.get(index).grp_lastTime = gmm.dateTime;
                                        group_chats.get(index).grp_lastSender = gmm.senderName;
                                        System.out.println("LastMessage = " + group_chats.get(index).grp_lastMessage);
                                        System.out.println("LastTime = " + group_chats.get(index).grp_lastTime);
                                        System.out.println("LastSender = " + group_chats.get(index).grp_lastSender);

                                        if (gmm.senderUid.equals(FirebaseAuth.getInstance().getUid()))
                                            group_chats.get(index).you = true;

                                        else
                                            group_chats.get(index).you = false;

                                        viewGroupAdapter.notifyItemChanged(index);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                            break;
                        }

                    }
                }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                for(int i=0; i<group_chats.size(); i++){

                    if(snapshot.getKey().equals(group_chats.get(i).grpId)){

                    }


                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                for(int i=0; i<group_chats.size(); i++){

                    if(snapshot.getKey().equals(group_chats.get(i).grpId)){
                        group_chats.remove(i);
                        viewGroupAdapter.notifyItemRemoved(i);
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


        addGroup.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.add_grp_btn:

                Intent intent = new Intent(getActivity(),AddGroups.class);
                startActivity(intent);
                break;


        }
    }
}