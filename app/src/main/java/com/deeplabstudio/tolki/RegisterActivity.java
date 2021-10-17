package com.deeplabstudio.tolki;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid = user.getUid();
    private Button mNext;
    private CircleImageView mImage;
    private int IMAGE = 1;
    private Uri ProfileImage;
    private Bitmap bitmap = null;
    private boolean IMAGE_SELECT = false;
    private EditText mName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mNext = findViewById(R.id.mNext);
        mImage = findViewById(R.id.mImage);
        mName = findViewById(R.id.mName);

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mName.getText().toString().trim();
                if (!TextUtils.isEmpty(name)){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name",name);
                    hashMap.put("phone", user.getPhoneNumber().substring(user.getPhoneNumber().length() - 11));
                    hashMap.put("phone_full", user.getPhoneNumber());
                    hashMap.put("status","user");
                    hashMap.put("uid",uid);
                    hashMap.put("timestamp", FieldValue.serverTimestamp());
                    showProgressDialog();
                    if (IMAGE_SELECT){
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                        byte[] data = baos.toByteArray();
                        StorageReference imageRef = mStorageReference.child("Accounts").child(uid).child(uid + "_M.jpg");
                        imageRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference mReferanceIMAGE = FirebaseStorage.getInstance().getInstance().getReference().child("Accounts").child(uid).child(uid + "_M.jpg");
                                mReferanceIMAGE.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String url = uri.toString();
                                        StorageReference hRef = mStorageReference.child("Accounts").child(uid).child(uid + "_H.jpg");
                                        hRef.putFile(ProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                StorageReference mHref = FirebaseStorage.getInstance().getInstance().getReference().child("Accounts").child(uid).child(uid + "_H.jpg");
                                                mHref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String hUri = uri.toString();
                                                        hashMap.put("image",url);
                                                        hashMap.put("image_H",hUri);
                                                        FirebaseFirestore.getInstance().collection("Accounts").document(uid)
                                                                .set(hashMap, SetOptions.merge())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        progressDialog.dismiss();

                                                                    }
                                                                });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        hashMap.put("image",null);
                        FirebaseFirestore.getInstance().collection("Accounts").document(uid)
                                .set(hashMap, SetOptions.merge())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                }else Toast.makeText(RegisterActivity.this, "Lütfen adınızı giriniz", Toast.LENGTH_SHORT).show();
            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},IMAGE);
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, IMAGE);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        user.delete();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Lütfen Bekleyin...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE){
                int rotate = 0;
                try {
                    ProfileImage = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),ProfileImage);
                    Picasso.get().load(Uri.parse(String.valueOf(ProfileImage))).centerCrop().fit().into(mImage);
                    IMAGE_SELECT = true;
                }catch (Exception e){
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}