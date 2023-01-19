package com.asadullahnawaz_alinaaftab.i200761_i200961;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;




public class MainActivity extends AppCompatActivity {

    class Helper extends TimerTask
    {
        public void run()
        {
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                MainPage.viewContacts = new ContactsProvider();
                Intent intent = new Intent(MainActivity.this, MainPage.class);
                startActivity(intent);
            }

            else {

                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference("Users").keepSynced(true);
        FirebaseDatabase.getInstance().getReference("Chats").keepSynced(true);
        FirebaseDatabase.getInstance().getReference("Group Chats").keepSynced(true);
       // FirebaseAuth.getInstance().signOut();;
        Timer timer = new Timer();
        TimerTask task = new Helper();
        timer.schedule(task, 5000);

    }
}