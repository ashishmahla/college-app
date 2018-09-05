package in.ac.bkbiet.bkbiet.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.models.Chat;
import in.ac.bkbiet.bkbiet.models.Message;
import in.ac.bkbiet.bkbiet.models.MessageAdapter;
import in.ac.bkbiet.bkbiet.utils.ImageLoading.ImageLoader;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Sv;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * ConvoActivity Created by Ashish on 12/7/2017.
 */

public class Conversation extends AppCompatActivity {
    Chat chat;
    RecyclerView.LayoutManager mLayoutManager;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("chats");
    DatabaseReference conversationRef;
    ChildEventListener newMsgListener;
    AppCompatButton btn_send_msg;
    ImageView dp;
    TextView name, subtitle;
    TextView tv_blockedMsg;
    CardView cv_block;
    CardView cv_msgBar;
    private EditText et_input_msg;
    private List<Message> msgsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MessageAdapter msgAdapter;
    private boolean sendReceipts = (Sv.getBooleanSetting(Sv.dSEND_RECEIPTS, true));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();
        intent.setExtrasClassLoader(Chat.class.getClassLoader());
        chat = intent.getParcelableExtra("chat");
        initViews();
    }

    private void initViews() {
        name = findViewById(R.id.tv_ch_title);
        name.setText(chat.getSecondPerson().getName());
        subtitle = findViewById(R.id.tv_ch_subtitle);
        subtitle.setText(chat.getSecondPerson().getUsername());

        dp = findViewById(R.id.iv_chat_dp);
        ImageLoader imageLoader = new ImageLoader(dp.getContext());
        imageLoader.displayImage(chat.getSecondPerson().getDp_link(), dp, chat.getSecondPerson().getName(), ImageLoader.QUALITY_LOW);

        cv_block = findViewById(R.id.card_ac_blocked);
        cv_msgBar = findViewById(R.id.card_ac_msg_bar);
        tv_blockedMsg = findViewById(R.id.tv_ac_blocking_msg);
        et_input_msg = findViewById(R.id.et_ac_message);

        recyclerView = findViewById(R.id.rv_ac_messages);
        msgAdapter = new MessageAdapter(Conversation.this, msgsList);
        mLayoutManager = new LinearLayoutManager(Conversation.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(msgAdapter);

        btn_send_msg = findViewById(R.id.b_ac_send);
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        updateChatBlocking();
        registerBlockingListener();

        if (Uv.currUser.getUsername() != null)
            prepareMsgsData();
    }

    public void sendMessage() {
        if (et_input_msg.getText().length() != 0) {
            DatabaseReference personRef = dbRef.child("messages").child(chat.getId());
            Map<String, Object> map = new HashMap<>();
            String temp_key = personRef.push().getKey();

            personRef.updateChildren(map);
            Message msg = new Message(temp_key, et_input_msg.getText().toString());

            DatabaseReference messageRef = personRef.child(temp_key);
            messageRef.setValue(msg);

            dbRef.child("conversations").child(Uv.currUser.getUsername()).
                    child(chat.getId()).child("last_msg").setValue(msg.getmId());
            dbRef.child("conversations").child(Uv.currUser.getUsername()).
                    child(chat.getId()).child("last_msg_sent_at").setValue(msg.getSentAt());
            dbRef.child("conversations").child(chat.getSecondPerson().getUsername()).
                    child(chat.getId()).child("last_msg").setValue(msg.getmId());
            dbRef.child("conversations").child(chat.getSecondPerson().getUsername()).
                    child(chat.getId()).child("last_msg_sent_at").setValue(msg.getSentAt());

            et_input_msg.setText("");
        }
        mLayoutManager.scrollToPosition(msgsList.size() - 1);
        recyclerView.getLayoutManager().scrollToPosition(msgsList.size() - 1);
    }

    private void append_message(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);
        if (message != null) {
            boolean found = false;
            int position = 0;
            for (int i = 0; i < msgsList.size(); i++) {
                if (msgsList.get(i).getmId().equals(message.getmId())) {
                    found = true;
                    position = i;
                    break;
                }
            }
            if (!found) {
                msgsList.add(message);
                /*recyclerView.getLayoutManager().scrollToPosition(msgsList.size() - 1);
                updateMessageReceipts(message);*/
            } else {
                msgsList.set(position, message);
            }
            msgAdapter.notifyDataSetChanged();
            recyclerView.getLayoutManager().scrollToPosition(msgsList.size() - 1);
            updateMessageReceipts(message);
        }
    }

    private void updateChatBlocking() {
        if (chat.getFirstPerson().isBlockingChat() || chat.getSecondPerson().isBlockingChat()) {
            cv_msgBar.setVisibility(View.GONE);
            cv_block.setVisibility(View.VISIBLE);
            String placeholder = "Chat is blocked.";
            tv_blockedMsg.setText(placeholder);
        } else {
            cv_msgBar.setVisibility(View.VISIBLE);
            cv_block.setVisibility(View.INVISIBLE);
        }
    }

    private void updateMessageReceipts(Message message) {
        if (!message.getSenderUsername().equals(Uv.currUser.getUsername()) && sendReceipts) {
            DatabaseReference messageRef = dbRef.child("messages").child(chat.getId()).child(message.getmId());
            if (message.getReceivedAt().equals("not_yet_received")) {
                messageRef.child("receivedAt").setValue(Statics.getTimeStamp());
            }
            if (message.getSeenAt().equals("not_yet_seen")) {
                messageRef.child("seenAt").setValue(Statics.getTimeStamp());
            }

            dbRef.child("conversations").child(Uv.currUser.getUsername()).
                    child(chat.getId()).child("last_msg_sent_at").setValue(chat.getLast_msg_sent_at().replace("ss", "") + "s");
        }
    }

    private void registerBlockingListener() {
        DatabaseReference FirstBlockingRef = dbRef.child("conversations").child(Uv.currUser.getUsername()).child(chat.getId()).child("firstPerson");
        DatabaseReference SecondBlockingRef = dbRef.child("conversations").child(Uv.currUser.getUsername()).child(chat.getId()).child("secondPerson");

        FirstBlockingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chat.Person person = dataSnapshot.getValue(Chat.Person.class);
                Statics.logDebug(dataSnapshot.toString());
                assert person != null;
                chat.getFirstPerson().setBlockingChat(person.isBlockingChat());
                updateChatBlocking();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        SecondBlockingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Chat.Person person = dataSnapshot.getValue(Chat.Person.class);
                assert person != null;
                chat.getSecondPerson().setBlockingChat(person.isBlockingChat());
                updateChatBlocking();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void prepareMsgsData() {
        conversationRef = dbRef.child("messages").child(chat.getId());
        newMsgListener = conversationRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_message(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_message(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (newMsgListener != null) {
            conversationRef.removeEventListener(newMsgListener);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (chat != null) {
            Uv.currChatWith = chat.getSecondPerson().getUsername();
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert nm != null;
            nm.cancel(chat.getSecondPerson().getUsername(), Uv.NEW_CHAT_NOTY_ID);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Uv.currChatWith = "none";
    }
}
