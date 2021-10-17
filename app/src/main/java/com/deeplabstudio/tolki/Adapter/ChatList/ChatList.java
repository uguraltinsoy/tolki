package com.deeplabstudio.tolki.Adapter.ChatList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deeplabstudio.tolki.ChatActivity;
import com.deeplabstudio.tolki.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatList extends FirestoreRecyclerAdapter<Users,ChatList.ChatListHolder> {
    private Context mContext;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ChatList(@NonNull FirestoreRecyclerOptions<Users> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatListHolder chatListHolder, int i, @NonNull Users users) {
        try{
            final String uid = users.getOne();
            db.collection("Accounts").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String name = documentSnapshot.getString("name");
                    String image = documentSnapshot.getString("image");
                    chatListHolder.mProfileName.setText(name);
                    if (image != null){
                        Picasso.get().load(image).centerCrop().fit().into(chatListHolder.mProfileImage);
                    }else{
                        Picasso.get().load(R.drawable.user).centerCrop().fit().into(chatListHolder.mProfileImage);
                    }
                    chatListHolder.mChatOpenLinear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try{
                                Intent intent = new Intent(mContext, ChatActivity.class);
                                intent.putExtra("uid", uid);
                                mContext.startActivity(intent);
                            }catch (Exception e){System.out.println(e.getMessage());}
                        }
                    });
                }
            });
            db.collection("ChatChannel").document(users.getChannelId()).collection("Message")
                    .orderBy("timestamp", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    try{
                        DocumentSnapshot lastVisible = documentSnapshots.getDocuments().get(0);
                        String docubentUID = lastVisible.getString("uid");
                        boolean read = lastVisible.getBoolean("read");
                        if (docubentUID.equals(uid)){
                            if (read) chatListHolder.mNotification.setVisibility(View.GONE);
                            else chatListHolder.mNotification.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){ e.printStackTrace(); }
                }
            });
        }catch (Exception e){System.out.println(e.getMessage());}
    }

    @NonNull
    @Override
    public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ic_chat_user,
                parent, false);
        mContext = parent.getContext();
        return new ChatListHolder(v);
    }

    public class ChatListHolder extends RecyclerView.ViewHolder{
        CircleImageView mProfileImage;
        TextView mProfileName;
        RelativeLayout mChatOpenLinear;
        ImageView mNotification;

        public ChatListHolder(@NonNull View itemView) {
            super(itemView);
            mProfileImage = itemView.findViewById(R.id.mProfileImage);
            mProfileName = itemView.findViewById(R.id.mProfileName);
            mChatOpenLinear = itemView.findViewById(R.id.mChatOpenLinear);
            mNotification = itemView.findViewById(R.id.mNotification);
        }
    }
}
