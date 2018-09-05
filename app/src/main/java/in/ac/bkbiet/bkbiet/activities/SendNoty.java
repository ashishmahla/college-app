package in.ac.bkbiet.bkbiet.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.models.Noty;
import in.ac.bkbiet.bkbiet.models.Token;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * SendNoty Created by Ashish on 9/15/2017.
 */

public class SendNoty extends AppCompatActivity {
    TextView tv_senderName, tv_sendTime;
    Button bSendNoty;
    TextInputEditText tiet_title, tiet_body;
    TextView tv_receiver;
    ArrayList<String> usernameList = new ArrayList<>();
    ArrayList<String> userList = new ArrayList<>();
    String receiver = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_noty);
        initViews();
        prepareUserData();
    }

    private void prepareUserData() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference()
                .child("user_tokens");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    Token token = shot.getValue(Token.class);
                    assert token != null;
                    usernameList.add(shot.getKey());
                    userList.add(token.getName());
                }
                tv_receiver.setText("Select a receiver");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initViews() {
        tv_senderName = findViewById(R.id.tv_asn_senderName);
        tv_sendTime = findViewById(R.id.tv_asn_sendTime);
        tv_senderName.setText(Uv.currUser.getName());
        tv_sendTime.setText(Statics.getTimeStamp("MMMM dd"));

        tv_receiver = findViewById(R.id.tv_asn_receiver);
        tv_receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userList.isEmpty()) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(SendNoty.this);
                    adb.setTitle("Select Receiver");
                    adb.setSingleChoiceItems(userList.toArray(new CharSequence[userList.size()]), -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            tv_receiver.setText(userList.get(i));
                            receiver = usernameList.get(i);
                            dialogInterface.dismiss();
                        }
                    });
                    adb.setNegativeButton("Cancel", null);
                    adb.show();
                }
            }
        });

        tiet_title = findViewById(R.id.tiet_asn_title);
        tiet_body = findViewById(R.id.tiet_asn_body);

        bSendNoty = findViewById(R.id.b_asn_sendNoty);
        bSendNoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = tiet_title.getText().toString();
                String body = tiet_body.getText().toString();

                if (title.isEmpty()) {
                    //((TextInputLayout) tiet_title.getParent()).setError("Title is required.");
                    Snackbar.make(tiet_title, "Title is required.", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else if (body.isEmpty()) {
                    //((TextInputLayout) tiet_title.getParent()).setError("Body is required.");
                    Snackbar.make(tiet_body, "Title is required.", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else if (receiver.isEmpty()) {
                    Snackbar.make(tiet_body, "Select a receiver.", BaseTransientBottomBar.LENGTH_SHORT).show();
                } else {
                    Noty noty = new Noty();
                    noty.setTitle(title);
                    noty.setBody(body);
                    noty.setSenderId(Uv.currUser.getUsername());
                    noty.setSender(Uv.currUser.getName());
                    noty.setColor("#ff0000d5");
                    noty.setSentAt(Statics.getTimeStamp(Uv.NOTIFICATION_DATE_FORMAT));
                    noty.setSendTo("device");
                    noty.setReceiver(receiver);

                    sendNoty(noty);
                    Toast.makeText(SendNoty.this, "Notification added to queue.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void sendNoty(Noty noty) {
        DatabaseReference notyQueueRef = FirebaseDatabase.getInstance().getReference()
                .child("notyQueue").child(Uv.currUser.getUsername());
        String nId = notyQueueRef.push().getKey();
        noty.setnId(nId);
        notyQueueRef.child(nId).setValue(noty);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }
}