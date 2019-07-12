package com.example.testmessager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Messager
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("messages");
    Button send;
    RecyclerView recyclerView;
    EditText editText;
    String message;
    private final int MESSAGE_MAX_LENTH=100;
    //Messager
    //Auth
    private static final int MY_REQUEST_CODE = 7117;
    List<AuthUI.IdpConfig> providers;
    Button logout;
    Button login;
    Button test;
    TextView usernameTextView;
    FirebaseUser user;
    String username ="username";
    //Auth


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Auth
        logout =findViewById(R.id.buttonLogout);
        logout.setEnabled(false);
        login =findViewById(R.id.buttonLogin);
        usernameTextView =findViewById(R.id.username);
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()

        );
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logout
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                logout.setEnabled(false);
                                send.setEnabled(false);
                                usernameTextView.setText("usernameTextView");
                                showSignInOptions();
                                Toast.makeText(MainActivity.this,"Logout",Toast.LENGTH_SHORT);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this," "+e.getMessage(),Toast.LENGTH_SHORT);

                    }
                });
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout.setEnabled(true);
                send.setEnabled(true);
                showSignInOptions();
            }
        });
/*
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    //usernameTextView.setText(username);

            }
        });
*/
        //Auth
        //Messager
        myRef.setValue("Message");
        final ArrayList<String> messages = new ArrayList<>();
        send = findViewById(R.id.buttonSend);
        send.setEnabled(false);
        editText =findViewById(R.id.editText);
        recyclerView =findViewById(R.id.rectclerView);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        final DataAdapter dataAdapter = new DataAdapter(this,messages);
        recyclerView.setAdapter(dataAdapter);
        //Messager




        //Messager
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                message=editText.getText().toString();

                if (message.equals("")){
                    Toast.makeText(getApplicationContext(),"Enter text",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (message.length() > MESSAGE_MAX_LENTH){
                    Toast.makeText(getApplicationContext(),"Max message length 150",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    //myRef.setValue(editText.getText().toString());
                    myRef.push().setValue(message);
                    editText.setText(" ");
                    
                    Log.d("message", "onClick: ");
                }
            }
        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String msg =dataSnapshot.getValue(String.class);
                messages.add(msg);
                dataAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messages.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Messager




    }
    //Auth
    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.MyTheme)
                        .build(), MY_REQUEST_CODE
        );

    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (requestCode == RESULT_OK){
                //Get user
                 user =FirebaseAuth.getInstance().getCurrentUser();
                 username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().toString();
                Log.d("", "onActivityResult: ");
                //Show email toast
                Toast.makeText(this," "+user.getEmail(),Toast.LENGTH_SHORT);
                logout.setEnabled(true);
            }else {
                Toast.makeText(this," "+response.getError(),Toast.LENGTH_SHORT);
            }
        }

    }
    //Auth

}
