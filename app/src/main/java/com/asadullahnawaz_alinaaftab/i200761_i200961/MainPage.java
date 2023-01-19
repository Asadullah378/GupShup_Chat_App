package com.asadullahnawaz_alinaaftab.i200761_i200961;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MainPage extends AppCompatActivity {

    ImageView logout;
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;
    ChatsFragment chatsFragment;
    ContactsFragment contactsFragment;
    UpdateProfileFragment updateProfileFragment;
    GroupChatFragment groupChatFragment;
    Fragment active;

    static ContactsProvider viewContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);



        chatsFragment = new ChatsFragment();
        contactsFragment = new ContactsFragment();
        updateProfileFragment = new UpdateProfileFragment();
        groupChatFragment = new GroupChatFragment();
        firebaseAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout_btn);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        active = chatsFragment;
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, updateProfileFragment, "4").hide(updateProfileFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, contactsFragment, "3").hide(contactsFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, groupChatFragment, "2").hide(groupChatFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout,chatsFragment, "1").commit();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String player_id = OneSignal.getDeviceState().getUserId();

                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Player IDs").orderByValue().equalTo(player_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds: snapshot.getChildren())
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Player IDs").child(ds.getKey()).removeValue();

                        firebaseAuth.signOut();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                Intent intent = new Intent(MainPage.this,SignIn.class);
                startActivity(intent);
            }
        });


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.chats:
                        loadFragment(chatsFragment);
                        active = chatsFragment;
                        break;
                    case R.id.group_chats:
                        loadFragment(groupChatFragment);
                        active = groupChatFragment;
                        break;

                    case R.id.contacts:
                        loadFragment(contactsFragment);
                        active = contactsFragment;
                        break;
                    case R.id.profile:
                        loadFragment(updateProfileFragment);
                        active = updateProfileFragment;

                }
                return true;
            }
        });


    }

    void loadFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().hide(active).show(fragment).commit();
    }

    @Override
    public void onBackPressed() {

    }

    public static String getDateTime() {

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(date);

    }

    public static String formatDate(String dateTime, String pattern) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime date = LocalDateTime.parse(dateTime,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return formatter.format(date);
        }
        return "Time";

    }
}