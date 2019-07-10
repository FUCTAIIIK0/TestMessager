package com.example.testmessager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("messages");
    Button send;
    RecyclerView recyclerView;
    EditText editText;
    String message;
    private final int MESSAGE_MAX_LENTH=100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myRef.setValue("Message");
        final ArrayList<String> messages = new ArrayList<>();


        send = findViewById(R.id.buttonSend);
        editText =findViewById(R.id.editText);
        recyclerView =findViewById(R.id.rectclerView);

        recyclerView.setLayoutManager( new LinearLayoutManager(this));
        final DataAdapter dataAdapter = new DataAdapter(this,messages);

        recyclerView.setAdapter(dataAdapter);


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



    }
}
