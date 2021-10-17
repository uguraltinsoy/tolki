package com.deeplabstudio.tolki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.deeplabstudio.tolki.Adapter.Chat.Chat;
import com.deeplabstudio.tolki.Adapter.Chat.ChatAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String youUid;
    private String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ImageView mSend,mBackToolbar,mEffect;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    private boolean CHAT = false;
    private RecyclerView recyler_view_message;
    private ChatAdapter adapter;

    private String uuID = null;
    private int lastPosition;
    private CircleImageView mProfileImage;
    private TextView mProfileName;
    private String notificationID;
    private String name;
    private LinearLayout mProfileLinerLayout;
    private boolean BLOCK = false, EFFECT = false;
    private ProgressBar mChatActifiyProgressBar;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private MediaRecorder mRecorder;
    private String mBaseFile = null , file = null;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private LinearLayout mAnimation, mFullAnimation;
    private TextView mTime;
    private Timer timer;
    private TimerTask timerTask;
    private double time = 0.0;

    private static final String MY_PREFERENCES = "PREFERENCES";
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sharedPref = getSharedPreferences(MY_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Intent intent = getIntent();
        youUid = intent.getStringExtra("uid");

        mAnimation = findViewById(R.id.mAnimation);
        mFullAnimation = findViewById(R.id.mFullAnimation);
        mSend = findViewById(R.id.mSend);
        recyler_view_message = findViewById(R.id.recyler_view_message);
        mTime = findViewById(R.id.mTime);
        mEffect = findViewById(R.id.mEffect);

        mBackToolbar = findViewById(R.id.mBackToolbar);
        mProfileImage = findViewById(R.id.mProfileImage);
        mProfileName = findViewById(R.id.mProfileName);
        mProfileLinerLayout = findViewById(R.id.mProfileLinerLayout);
        mChatActifiyProgressBar = findViewById(R.id.mChatActifiyProgressBar);


        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        mBaseFile = getExternalCacheDir().getAbsolutePath() + "/";

        mBackToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        chekChat();

        if (sharedPref.contains("EFFECT")){
            EFFECT = sharedPref.getBoolean("EFFECT",false);
            if (EFFECT){
                mEffect.setImageResource(R.drawable.deepvoice);
            }else{
                mEffect.setImageResource(R.drawable.uservoice);
            }
        }else{
            editor.putBoolean("EFFECT", EFFECT);
            editor.apply();
        }

        mEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!EFFECT){
                    mEffect.setImageResource(R.drawable.deepvoice);
                }else{
                    mEffect.setImageResource(R.drawable.uservoice);
                }
                EFFECT = !EFFECT;
                editor.putBoolean("EFFECT", EFFECT);
                editor.apply();
            }
        });

        db.collection("Accounts").document(youUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String image = documentSnapshot.getString("image");
                String name = documentSnapshot.getString("name");
                notificationID = documentSnapshot.getString("notificationID");
                mProfileName.setText(name);
                if (image != null){
                    Picasso.get().load(image).centerCrop().fit().into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            mChatActifiyProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            mChatActifiyProgressBar.setVisibility(View.VISIBLE);
                        }
                    });
                }else
                    Picasso.get().load(R.drawable.user).centerCrop().fit().into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            mChatActifiyProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            mChatActifiyProgressBar.setVisibility(View.VISIBLE);
                        }
                    });
            }
        });

        db.collection("Accounts").document(myUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name = documentSnapshot.getString("name");
            }
        });


        timer = new Timer();
        int defaultWidth = mAnimation.getLayoutParams().width;

        mSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    mEffect.setEnabled(false);
                    startRecording();
                    ViewGroup.LayoutParams drawerParams = mAnimation.getLayoutParams();
                    drawerParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    mAnimation.setLayoutParams(drawerParams);

                    Animation anim = new ScaleAnimation(
                            1f, 1.5f,
                            1, 1.5f,
                            Animation.RELATIVE_TO_SELF, 1f,
                            Animation.RELATIVE_TO_SELF, 1f);
                    anim.setFillAfter(true);
                    anim.setDuration(100);
                    mSend.startAnimation(anim);
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    mTime.setText("");
                    time = 0.0;
                    Animation anim = new ScaleAnimation(
                            1.5f, 1f,
                            1.5f, 1f,
                            Animation.RELATIVE_TO_SELF, 1f,
                            Animation.RELATIVE_TO_SELF, 1f);
                    anim.setFillAfter(true);
                    anim.setDuration(100);
                    mSend.startAnimation(anim);
                    stopRecording(myUid, youUid);
                    mSend.setEnabled(false);
                    ViewGroup.LayoutParams drawerParams = mAnimation.getLayoutParams();
                    drawerParams.width = defaultWidth;
                    mAnimation.setLayoutParams(drawerParams);
                    return true;
                }
                return false;
            }
        });
    }

    private String getTimerText(){
        int rounded = (int) Math.round(time);
        int second = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);
        return formatTime(second,minutes,hours);
    }

    private String formatTime(int second, int minutes, int hours){
        return String.format("%02d:",minutes)+String.format("%02d",second);
    }

    private void startRecording(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        mTime.setText(getTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask,0,1000);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        file = String.valueOf(UUID.randomUUID()) + ".3gp";
        mRecorder.setOutputFile(mBaseFile + file);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioEncodingBitRate(128000);
        mRecorder.setAudioSamplingRate(96000);
        try {
            mRecorder.prepare();
        }catch (IOException e){ e.printStackTrace();}
        mRecorder.start();
    }

    private void stopRecording(String myUid,String youUid){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        timerTask.cancel();
        uploadAudio(myUid,youUid);
    }

    private void uploadAudio(String uid_1,String uid_2){
        if (!CHAT) {
            final String uuid = String.valueOf(UUID.randomUUID());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("channelId", uuid);
            hashMap.put("zero", uid_1);
            hashMap.put("one", uid_2);
            hashMap.put("timestamp", FieldValue.serverTimestamp());
            HashMap<String, Object> hash = new HashMap<>();
            hash.put("channelId", uuid);
            hash.put("zero", uid_2);
            hash.put("one", uid_1);
            hash.put("timestamp", FieldValue.serverTimestamp());
            db.collection("Accounts").document(uid_1).collection("ChatChannel").document(uid_2).set(hashMap, SetOptions.merge());
            db.collection("Accounts").document(uid_2).collection("ChatChannel").document(uid_1).set(hash, SetOptions.merge());
            db.collection("ChatChannel").document(uuid).set(hash, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    uuID = uuid;
                    sendAudio();
                    createRecylerview(uuid);
                    CHAT = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            sendAudio();
        }
    }

    private void sendAudio(){
        Uri uri = Uri.fromFile(new File(mBaseFile + file));
        StorageReference filepath = mStorageReference.child("Audio").child(file);
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference mReferanceAUDIO = mStorageReference.child("Audio").child(file);
                mReferanceAUDIO.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String messageUUID = String.valueOf(UUID.randomUUID());
                        String url = uri.toString();
                        String uid = FirebaseAuth.getInstance().getUid();
                        HashMap<String, Object> chatHash = new HashMap<>();
                        chatHash.put("channelId", uuID);
                        chatHash.put("uid", uid);
                        chatHash.put("audio", url);
                        chatHash.put("read", false);
                        chatHash.put("effect", EFFECT);
                        chatHash.put("messageUUID", messageUUID);
                        chatHash.put("timestamp", FieldValue.serverTimestamp());
                        HashMap<String, Object> timeHash = new HashMap<>();
                        timeHash.put("timestamp", FieldValue.serverTimestamp());
                        db.collection("Accounts").document(myUid).collection("ChatChannel").document(youUid).set(timeHash, SetOptions.merge());
                        db.collection("Accounts").document(youUid).collection("ChatChannel").document(myUid).set(timeHash, SetOptions.merge());
                        db.collection("ChatChannel").document(uuID).collection("Message").document(messageUUID).set(chatHash, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mSend.setEnabled(true);
                                mEffect.setEnabled(true);
                                FCMSend.pushNotification(ChatActivity.this, notificationID,name + " yeni sesli mesaj");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mSend.setEnabled(true);
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mSend.setEnabled(true);
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem item = menu.findItem(R.id.itemBlock);
        db.collection("Accounts").document(youUid).collection("blockList")
                .whereEqualTo("uid", myUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    mSend.setEnabled(true);
                    item.setVisible(true);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String ss = document.getString("uid");
                        if (ss.equals(myUid)){
                            mSend.setEnabled(false);
                            item.setVisible(false);
                        }
                    }
                }
            }
        });
        db.collection("Accounts").document(myUid).collection("blockList")
                .whereEqualTo("uid", youUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String ss = document.getString("uid");
                        if (ss.equals(youUid)){
                            item.setTitle("Engeli Kaldır");
                            BLOCK = true;
                        }else {
                            item.setTitle("Engelle");
                            BLOCK = false;
                        }
                    }
                }
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.itemBlock:
                if (BLOCK){
                    db.collection("Accounts").document(myUid).collection("blockList").document(youUid).delete();
                    item.setTitle("Engelle");
                }else{
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid",youUid);
                    db.collection("Accounts").document(myUid).collection("blockList").document(youUid).set(hashMap);
                    item.setTitle("Engeli Kaldır");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chekChat(){
        db.collection("Accounts").document(myUid).collection("ChatChannel")
                .whereEqualTo("one", youUid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String ss = document.getString("one");
                        if (ss.equals(youUid)){
                            CHAT = true;
                            db.collection("Accounts").document(myUid).collection("ChatChannel").document(ss).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    uuID = documentSnapshot.getString("channelId");
                                    createRecylerview(uuID);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void createRecylerview(String uuid){
        CollectionReference notebookRef = db.collection("ChatChannel").document(uuid).collection("Message");
        Query query = notebookRef.orderBy("timestamp", Query.Direction.ASCENDING)/*.whereEqualTo("status",true)*/;
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();
        recyler_view_message.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyler_view_message.setLayoutManager(linearLayoutManager);
        recyler_view_message.setItemViewCacheSize(20);
        recyler_view_message.setDrawingCacheEnabled(true);
        recyler_view_message.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        adapter = new ChatAdapter(options);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyler_view_message.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
        recyler_view_message.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerUtils.releaseMediaPlayer();
    }
}