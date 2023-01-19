package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {
    ArrayList<ChatModel> chats = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        RecyclerView recyclerView = v.findViewById(R.id.chatRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ChatAdapter adapter=new ChatAdapter(chats,getContext());
        recyclerView.setAdapter(adapter);


        databaseReference = firebaseDatabase.getReference("Chats").child(FirebaseAuth.getInstance().getUid());

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                firebaseDatabase.getReference("Users").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserModel uM = snapshot.getValue(UserModel.class);
                        ChatModel cM = new ChatModel(uM.name,snapshot.getKey(),uM.imageUrl,"","");
                        int index = chats.size();
                        chats.add(cM);
                        adapter.notifyItemInserted(chats.size());


                        firebaseDatabase.getReference("Chats").child(FirebaseAuth.getInstance().getUid()).child(snapshot.getKey()).limitToLast(1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(index>=0 && index<chats.size()) {
                                    MessageModel m = new MessageModel();
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        m = ds.getValue(MessageModel.class);
                                    }

                                    if (m.senderUid.equals(FirebaseAuth.getInstance().getUid()))
                                        chats.get(index).you = true;

                                    else
                                        chats.get(index).you = false;


                                    chats.get(index).lastMessage = m.messagetext;
                                    chats.get(index).lastTime = m.dateTime;
                                    adapter.notifyItemChanged(index);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        firebaseDatabase.getReference("Chats").child(FirebaseAuth.getInstance().getUid()).child(snapshot.getKey()).orderByChild("seen").equalTo("false").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                System.out.println("CHILD COUNT = "+snapshot.getChildrenCount());
                                chats.get(index).unseen = snapshot.getChildrenCount();
                                adapter.notifyItemChanged(index);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                for(int i=0; i<chats.size();i++){

                    if(chats.get(i).uid.equals(snapshot.getKey())){
                        chats.remove(i);
                        adapter.notifyItemRemoved(i);
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

        return v;
    }

}