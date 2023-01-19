package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton floatingActionButton;
    ViewContactAdapter viewContactAdapter;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        floatingActionButton = v.findViewById(R.id.add_contact_page_btn);
        floatingActionButton.setOnClickListener(this);

        recyclerView = v.findViewById(R.id.contactRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewContactAdapter = new ViewContactAdapter(MainPage.viewContacts.all_contacts,getContext());
        recyclerView.setAdapter(viewContactAdapter);
        MainPage.viewContacts.fetchContacts(viewContactAdapter);

        return v;
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.add_contact_page_btn:
                Intent intent = new Intent(getActivity(),AddContacts.class);
                startActivity(intent);
                break;
        }

    }


}