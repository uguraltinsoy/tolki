package com.deeplabstudio.tolki.Adapter.Contacts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deeplabstudio.tolki.ChatActivity;
import com.deeplabstudio.tolki.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private ArrayList<Account> accounts;
    private Context mContext;

    public ContactsAdapter(ArrayList<Account> accounts) {
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ic_chat_user,
                parent, false);
        mContext = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mProfileName.setText(accounts.get(position).getName());
        String image = accounts.get(position).getImage();
        if (image != null){
            Picasso.get().load(image).centerCrop().fit().into(holder.mProfileImage);
        }else{
            Picasso.get().load(R.drawable.user).centerCrop().fit().into(holder.mProfileImage);
        }
        holder.mChatOpenLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("uid", accounts.get(position).getUid());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    public void filterList(ArrayList<Account> filterList){
        accounts = filterList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView mProfileImage;
        TextView mProfileName;
        RelativeLayout mChatOpenLinear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfileImage = itemView.findViewById(R.id.mProfileImage);
            mProfileName = itemView.findViewById(R.id.mProfileName);
            mChatOpenLinear = itemView.findViewById(R.id.mChatOpenLinear);
        }
    }
}
