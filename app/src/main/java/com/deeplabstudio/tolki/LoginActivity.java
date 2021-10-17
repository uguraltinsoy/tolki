package com.deeplabstudio.tolki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationID;

    private EditText mPhone;
    private LinearLayout mVerifyCode, mPhoneLinear;
    private Button mSend, mVerify;
    private EditText mCode1, mCode2, mCode3, mCode4, mCode5, mCode6;
    private String code = "";
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        mCode1 = findViewById(R.id.mCode1);
        mCode2 = findViewById(R.id.mCode2);
        mCode3 = findViewById(R.id.mCode3);
        mCode4 = findViewById(R.id.mCode4);
        mCode5 = findViewById(R.id.mCode5);
        mCode6 = findViewById(R.id.mCode6);

        mPhone = findViewById(R.id.mPhone);
        mVerifyCode = findViewById(R.id.mVerifyCode);
        mSend = findViewById(R.id.mSend);
        mVerify = findViewById(R.id.mVerify);
        mPhoneLinear = findViewById(R.id.mPhoneLinear);

        mAuth.useAppLanguage();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                code = "";
                mCode1.setText("");
                mCode2.setText("");
                mCode3.setText("");
                mCode4.setText("");
                mCode5.setText("");
                mCode6.setText("");
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationID = verificationId;
                mPhoneLinear.setVisibility(View.GONE);
                mSend.setVisibility(View.GONE);
                mVerifyCode.setVisibility(View.VISIBLE);
                mVerify.setVisibility(View.VISIBLE);
            }
        };

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mPhone.getText().toString();
                if (!TextUtils.isEmpty(phone)){
                    startPohoneNumverVerification("+90" + phone);
                }
            }
        });

        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verfCode = getTextString(mCode1) + getTextString(mCode2) + getTextString(mCode3) + getTextString(mCode4) + getTextString(mCode5) + getTextString(mCode6);
                if (!TextUtils.isEmpty(verfCode)){
                    verifyPhoneNumberWithCode(mVerificationID, verfCode);
                }
            }
        });


        mCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mCode1.getText().toString().length() == 1) {
                    code += charSequence.toString();
                    mCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mCode2.getText().toString().length() == 1) {
                    code += charSequence.toString();
                    mCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mCode3.getText().toString().length() == 1) {
                    code += charSequence.toString();
                    mCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mCode4.getText().toString().length() == 1) {
                    code += charSequence.toString();
                    mCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mCode5.getText().toString().length() == 1) {
                    code += charSequence.toString();
                    mCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCode6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mCode6.getText().toString().length() == 1){
                    code += charSequence.toString();
                    verifyPhoneNumberWithCode(mVerificationID, code);
                    mVerify.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private String getTextString(EditText text){
        return text.getText().toString().trim();
    }

    private void startPohoneNumverVerification( String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationID, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                boolean isNew = authResult.getAdditionalUserInfo().isNewUser();
                if (isNew) startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                else startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }

}