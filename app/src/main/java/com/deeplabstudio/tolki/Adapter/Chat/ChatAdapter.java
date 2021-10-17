package com.deeplabstudio.tolki.Adapter.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Trace;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.deeplabstudio.tolki.MediaPlayerUtils;
import com.deeplabstudio.tolki.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ChatAdapter extends FirestoreRecyclerAdapter<Chat,ChatAdapter.ChatHolder> {
    private Context mContext;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Runnable runnable;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Chat> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatAdapter.ChatHolder chatHolder, int i, @NonNull Chat chat) {
        if (chat.getUid() != null && chat.getUid().equals(FirebaseAuth.getInstance().getUid())){
            chatHolder.message_root.setGravity(Gravity.RIGHT);
            chatHolder.mMessageLinear.setBackground(mContext.getDrawable(R.drawable.rec_round_primary_color));
            chatHolder.mRead.setVisibility(View.VISIBLE);
            if (chat.isRead()) chatHolder.mRead.setImageResource(R.drawable.ic_baseline_done_all_24_read);
        }else {
            chatHolder.message_root.setGravity(Gravity.LEFT);
            chatHolder.mMessageLinear.setBackground(mContext.getDrawable(R.drawable.rec_round_while));
            chatHolder.mRead.setVisibility(View.GONE);
        }

        chatHolder.mPlayPause.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                MediaPlayerUtils.releaseMediaPlayer();
                chatHolder.mSeekBar.setProgress(0);
                chatHolder.mPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                try {
                    if (chat.isEffect()) MediaPlayerUtils.startAndPlayMediaPlayer(chat.getAudio(),0.75f, chatHolder.mSeekBar, chatHolder.mPlayPause);
                    else MediaPlayerUtils.startAndPlayMediaPlayer(chat.getAudio(), chatHolder.mSeekBar, chatHolder.mPlayPause);
                    chatHolder.mPlayPause.setImageResource(R.drawable.ic_baseline_stop_24);
                    if (chat.getUid() != null && !chat.getUid().equals(FirebaseAuth.getInstance().getUid())){
                        HashMap<String, Object> chatUpdate = new HashMap<>();
                        chatUpdate.put("read", true);
                        FirebaseFirestore.getInstance()
                                .collection("ChatChannel").document(chat.getChannelId())
                                .collection("Message").document(chat.getMessageUUID())
                                .set(chatUpdate, SetOptions.merge());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        chatHolder.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) MediaPlayerUtils.applySeekBarValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (chat.getTimestamp() != null){
            @SuppressLint("SimpleDateFormat") String sysDay = new SimpleDateFormat("dd").format(new Date(System.currentTimeMillis()));
            @SuppressLint("SimpleDateFormat") String msjDay = new SimpleDateFormat("dd").format(new Date(chat.getTimestamp().getSeconds() * 1000));
            Date msjDate = new Date(chat.getTimestamp().getSeconds() * 1000);
            if (msjDay.equals(sysDay)){
                String date = new SimpleDateFormat("HH:mm").format(msjDate);
                chatHolder.mTimeText.setText(date);
            }else {
                String date = new SimpleDateFormat("HH:mm dd/MM/yyyy").format(msjDate);
                chatHolder.mTimeText.setText(date);
            }
        }
    }

    @NonNull
    @Override
    public ChatAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ic_message_recyler,
                parent, false);
        mContext = parent.getContext();
        return new ChatHolder(v);
    }

    public class ChatHolder extends RecyclerView.ViewHolder {
        TextView mTimeText;
        LinearLayout message_root, mMessageLinear;
        ImageView mPlayPause;
        SeekBar mSeekBar;
        ImageView mRead;
        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            mMessageLinear = itemView.findViewById(R.id.mMessageLinear);
            mTimeText = itemView.findViewById(R.id.mTimeText);
            message_root = itemView.findViewById(R.id.message_root);
            mPlayPause = itemView.findViewById(R.id.mPlayPause);
            mSeekBar = itemView.findViewById(R.id.mSeekBar);
            mRead = itemView.findViewById(R.id.mRead);
            int position = getAdapterPosition();

        }
    }
}
