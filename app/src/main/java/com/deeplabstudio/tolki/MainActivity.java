package com.deeplabstudio.tolki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.deeplabstudio.tolki.Adapter.ChatList.ChatList;
import com.deeplabstudio.tolki.Adapter.ChatList.Users;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uid = FirebaseAuth.getInstance().getUid();
    private FloatingActionButton fab;
    private RecyclerView mChatRecyler;
    private ChatList adapter;
    private CircleImageView mProfile;
    private TextView mNewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }
                        String token = task.getResult();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("notificationID",token);
                        db.collection("Accounts").document(uid).set(hashMap, SetOptions.merge());
                        System.out.println(token);
                    }
                });

        mProfile = findViewById(R.id.mProfile);
        mNewMessage = findViewById(R.id.mNewMessage);

        mChatRecyler = findViewById(R.id.mChatRecyler);
        String uid = FirebaseAuth.getInstance().getUid();
        CollectionReference notebookRef = db.collection("Accounts").document(uid).collection("ChatChannel");
        notebookRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int item = task.getResult().size();
                if (item > 0) mNewMessage.setVisibility(View.GONE);
                else mNewMessage.setVisibility(View.VISIBLE);
            }
        });

        Query query = notebookRef.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();
        mChatRecyler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mChatRecyler.setLayoutManager(linearLayoutManager);
        mChatRecyler.setItemViewCacheSize(20);
        mChatRecyler.setDrawingCacheEnabled(true);
        mChatRecyler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new ChatList(options);
        mChatRecyler.setAdapter(adapter);
        adapter.startListening();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContacsFragment dialog = new ContacsFragment();
                dialog.show(getSupportFragmentManager(),"Show");
            }
        });

        findViewById(R.id.mProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        db.collection("Accounts").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("image") != null) Picasso.get().load(documentSnapshot.getString("image")).centerCrop().fit().into(mProfile);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {}

}