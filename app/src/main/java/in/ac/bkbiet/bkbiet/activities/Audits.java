package in.ac.bkbiet.bkbiet.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.models.AuditAdapter;
import in.ac.bkbiet.bkbiet.models.AuditRecord;

/**
 * Created by Ashish on 12/9/2017.
 */

public class Audits extends AppCompatActivity {

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("reports");
    RecyclerView recyclerView;
    AuditAdapter mAdapter;
    ArrayList<AuditRecord> auditRecords = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audits);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        mAdapter = new AuditAdapter(auditRecords);
        recyclerView.setAdapter(mAdapter);
        prepareUserData();
    }

    private void initViews() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.rv_audits);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void prepareUserData() {
        DatabaseReference auditsRef = dbRef;
        auditsRef.addChildEventListener(new ChildEventListener() {
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

    private void append_message(DataSnapshot dataSnapshot) {
        try {
            //Log.e("Count " ,""+snapshot.getChildrenCount());
            String header = dataSnapshot.getKey();
            String username = header.substring(0, header.indexOf("%%"));
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                AuditRecord post = postSnapshot.getValue(AuditRecord.class);
                post.setTimeStamp(postSnapshot.getKey());
                post.setUsername(username);
                auditRecords.add(post);
            }

            mAdapter.notifyDataSetChanged();
        } catch (Exception ignored) {
            Log.e("debug", dataSnapshot.toString());
        }
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