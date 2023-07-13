package com.example.firestoreapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button SaveBTN;
    private EditText nameET;
    private EditText emailET;

    private TextView textView;

    private Button ShowBTN;
    private Button updateBTN;

    private Button deleteBTN;

    //Firebase Firestore
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private DocumentReference friendRef=db.collection("Users").document("Friends");

    //KEYs
    private static final String KEY_NAME="name";
    private static final String KEY_EMAIL="email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SaveBTN=findViewById(R.id.SaveBTN);
        nameET=findViewById(R.id.nameET);
        emailET=findViewById(R.id.emailET);
        textView=findViewById(R.id.text);

        SaveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveDataToFireStore();
            }
        });

        ShowBTN=findViewById(R.id.readBTN);

        ShowBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadData();
            }
        });

        updateBTN=findViewById(R.id.updateBTN);

        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdataData();
            }
        });

        deleteBTN=findViewById(R.id.deleteBTN);

        deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });
    }

    private void deleteData() {
        //Delete Pair

        friendRef.update(KEY_NAME, FieldValue.delete());
    }

    private void UpdataData() {
        String name=nameET.getText().toString().trim();
        String email=emailET.getText().toString().trim();

        Map<String,Object> data=new HashMap<>();
        data.put(KEY_NAME,name);
        data.put(KEY_EMAIL,email);

        friendRef.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "Update Succcessful!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void ReadData() {
        friendRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Let's display the data in textview
                if(documentSnapshot.exists()){
                    String fname=documentSnapshot.getString(KEY_NAME);
                    String femail=documentSnapshot.getString(KEY_EMAIL);

                    textView.setText("Username: "+fname+"\n"+"Email: "+femail);
                }
            }
        });
    }

    private void SaveDataToFireStore() {
        String name=nameET.getText().toString().trim();
        String email=emailET.getText().toString().trim();

        //Saving Data as Key-Value Pair(Mapping)

        Map<String,Object> data=new HashMap<>();
        data.put(KEY_NAME,name);
        data.put(KEY_EMAIL,email);

        //Saving in Collections
        db.collection("Users").document("Friends")
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Successfully Added", Toast.LENGTH_LONG).show();
                    }
                })

        //Adding other Listeners
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Not Able To Add, Try Again!", Toast.LENGTH_LONG).show();
                    }
                });

    }

    //Building CRUD App

    //1. Saving Data on Firestore (Creating Data)
    //2. Reading Data from Firestore (Retrieving Data)
    //     2.1 Listening to Snapshot changes
    //3. Updating simple Data
    // 4. Deleting Data (K-V Pairs)


    @Override
    protected void onStart() {
        super.onStart();

        //Listening all the time during lifecycle
        friendRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){
                    Toast.makeText(MainActivity.this, "Error Found!!", Toast.LENGTH_LONG).show();
                }
                if(value!=null && value.exists()){
                    String fname=value.getString(KEY_NAME);
                    String femail=value.getString(KEY_EMAIL);

                    textView.setText("Username: "+fname+"\n"+"Email: "+femail);
                }
            }
        });
    }
}