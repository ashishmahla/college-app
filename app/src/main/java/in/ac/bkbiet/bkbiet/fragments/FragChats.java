package in.ac.bkbiet.bkbiet.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.activities.Conversation;
import in.ac.bkbiet.bkbiet.models.Chat;
import in.ac.bkbiet.bkbiet.models.Chat.Person;
import in.ac.bkbiet.bkbiet.models.ChatAdapter;
import in.ac.bkbiet.bkbiet.models.Message;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * FragChats fragment Created by Ashish on 12/7/2017.
 */

public class FragChats extends Fragment implements ChatAdapter.ChatClickListener {
    ChatAdapter mAdapter;
    ArrayList<Chat> chatsList = new ArrayList<>();
    ArrayList<Boolean> chatListSwapping = new ArrayList<>();
    RecyclerView rv_chats;
    FloatingActionButton fab_add_chat;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("chats").child("conversations");
    CardView info_msg;
    View _rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (_rootView == null) {
            // Inflate the layout for this fragment
            _rootView = inflater.inflate(R.layout.frag_chats, container, false);
            // Find and setup subviews
            initViews(_rootView);
        }
        /*else {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove _rootView from the existing parent view group
            // in onDestroyView() (it will be added back).
        }*/
        return _rootView;
    }

    private void initViews(View parent) {
        info_msg = parent.findViewById(R.id.card_fc_no_chats);
        rv_chats = parent.findViewById(R.id.rv_fc_chats);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_chats.setLayoutManager(mLayoutManager);
        rv_chats.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ChatAdapter(chatsList, this);
        rv_chats.setAdapter(mAdapter);

        fab_add_chat = parent.findViewById(R.id.fab_new_chat);
        fab_add_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChat();
            }
        });

        prepareChatData();
    }

    private void addChat() {
        final ArrayList<Person> usersAvailable = new ArrayList<>();
        final ArrayList<String> userNamesAvailable = new ArrayList<>();
        DatabaseReference chat_available_users_ref = dbRef.getParent().child("0_chat_users_available");
        chat_available_users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Person person = snapshot.getValue(Person.class);
                    assert person != null;
                    if (!person.getUsername().equals(Uv.currUser.getUsername())) {
                        usersAvailable.add(person);
                        userNamesAvailable.add(person.getName());
                    }
                }
                if (usersAvailable.size() > 0) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                    adb.setTitle("Available Users");
                    adb.setSingleChoiceItems(userNamesAvailable.toArray(new String[userNamesAvailable.size()]), -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startNewChat(usersAvailable.get(which));
                            dialog.dismiss();
                        }
                    });
                    adb.show();
                } else {
                    Statics.showToast(getActivity(), "No users available for chat.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void startNewChat(final Person secondPerson) {
        boolean isAlreadyPresent = false;
        for (Chat chat : chatsList) {
            if (chat.getSecondPerson().getUsername().equals(secondPerson.getUsername()))
                isAlreadyPresent = true;
        }

        if (!isAlreadyPresent) {
            DatabaseReference personRef = dbRef.child(secondPerson.getUsername());

            Map<String, Object> map = new HashMap<>();
            String temp_key = personRef.push().getKey();
            personRef.updateChildren(map);

            DatabaseReference chat_ref = personRef.child(temp_key);

            Chat c = new Chat(temp_key, new Person(Uv.currUser.getUsername(), Uv.currUser.getName(), Uv.currUser.getDpLink(), false),
                    secondPerson, "Say Hi to " + secondPerson.getName());

            chat_ref.setValue(c);

            chat_ref = dbRef.child(Uv.currUser.getUsername()).child(temp_key);
            chat_ref.setValue(c);
        } else {
            Statics.showToast(getActivity(), "Chat with user already started.");
        }
    }

    private void prepareChatData() {
        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("chats").child("conversations").child(Uv.currUser.getUsername());
        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat(dataSnapshot);
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

    private void append_chat(DataSnapshot snapshot) {
        Chat c = snapshot.getValue(Chat.class);
        if (c != null) {
            boolean found = false;
            int position = chatsList.size() - 1;
            for (int i = 0; i < chatsList.size(); i++) {
                if (chatsList.get(i).getId().equals(c.getId())) {
                    position = i;
                    found = true;
                    break;
                }
            }
            if (!c.getFirstPerson().getUsername().equals(Uv.currUser.getUsername())) {
                Person tempPerson = c.getFirstPerson();
                c.setFirstPerson(c.getSecondPerson());
                c.setSecondPerson(tempPerson);
                chatListSwapping.add(true);
            } else {
                chatListSwapping.add(false);
            }
            finalizeChatInfoWithMessage(found, position, c);
        }
    }

    private void finalizeChatInfoWithMessage(final boolean isUpdate, final int pos, final Chat c) {
        if (c.getLast_msg().contains("Say Hi to ")) {
            Message message = new Message("NoId", "Say Hi to " + c.getSecondPerson().getName());
            message.setSentAt(c.getLast_msg_sent_at());

            c.setLastMessage(message);
            if (!isUpdate) {
                chatsList.add(c);
            } else {
                chatsList.set(pos, c);
            }

            Collections.sort(chatsList);
            mAdapter.notifyDataSetChanged();

            rv_chats.setVisibility(View.VISIBLE);
            info_msg.setVisibility(View.GONE);
        } else {
            try {
                DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference().child("chats")
                        .child("messages").child(c.getId()).child(c.getLast_msg());
                messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Message message = dataSnapshot.getValue(Message.class);
                        if (message == null) {
                            message = new Message("NoId", "NoMessageFoundException");

                        }

                        c.setLastMessage(message);
                        if (!isUpdate) {
                            chatsList.add(c);
                        } else {
                            chatsList.set(pos, c);
                        }

                        Collections.sort(chatsList);
                        mAdapter.notifyDataSetChanged();
                        rv_chats.setVisibility(View.VISIBLE);
                        info_msg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            } catch (Exception ignored) {
                Message message = new Message("NoId", c.getLast_msg());
                message.setSentAt(c.getLast_msg_sent_at());
                c.setLastMessage(message);
                if (!isUpdate) {
                    chatsList.add(c);
                } else {
                    chatsList.set(pos, c);
                }

                Collections.sort(chatsList);
                mAdapter.notifyDataSetChanged();
                rv_chats.setVisibility(View.VISIBLE);
                info_msg.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onChatClickListener(View v, int position) {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putParcelable("chat", chatsList.get(position));
        i.putExtras(b);
        i.setClass(getActivity(), Conversation.class);
        startActivity(i);
    }

    @Override
    public void onChatLongClickListener(View view, final int position) {
        final Chat c = chatsList.get(position);
        final boolean swapped = chatListSwapping.get(position);
        final boolean isBlocking;

        Statics.logDebug(swapped+"x"+c.getFirstPerson().isBlockingChat() + "1--2" + c.getSecondPerson().isBlockingChat());
        isBlocking = c.getFirstPerson().isBlockingChat();

        if (!c.getSecondPerson().getUsername().equals("14ebkcs017")) {
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("chats");
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setTitle((isBlocking ? "Unblock " : "Block ") + c.getSecondPerson().getName() + "?");
            adb.setMessage(isBlocking ? "Messages could be sent by either party if unblocked." :
                    "Messages couldn't be sent by either party if blocked.");

            adb.setPositiveButton(isBlocking ? "Unblock" : "Block", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (swapped) {
                        dbRef.child("conversations").child(c.getFirstPerson().getUsername()).child(c.getId())
                                .child("secondPerson").child("blockingChat").setValue(!isBlocking);
                        dbRef.child("conversations").child(c.getSecondPerson().getUsername()).child(c.getId())
                                .child("secondPerson").child("blockingChat").setValue(!isBlocking);
                        c.getFirstPerson().setBlockingChat(!isBlocking);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        dbRef.child("conversations").child(c.getFirstPerson().getUsername()).child(c.getId())
                                .child("firstPerson").child("blockingChat").setValue(!isBlocking);
                        dbRef.child("conversations").child(c.getSecondPerson().getUsername()).child(c.getId())
                                .child("firstPerson").child("blockingChat").setValue(!isBlocking);
                        c.getFirstPerson().setBlockingChat(!isBlocking);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

            adb.setNegativeButton("Cancel", null);
            adb.show();
        }
    }

    @Override
    public void onResume() {
        /*prepareChatData();
        mAdapter.notifyDataSetChanged();*/
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        if (_rootView.getParent() != null) {
            ((ViewGroup) _rootView.getParent()).removeView(_rootView);
        }
        super.onDestroyView();
    }
}