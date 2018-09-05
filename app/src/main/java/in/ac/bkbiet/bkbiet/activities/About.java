package in.ac.bkbiet.bkbiet.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import in.ac.bkbiet.bkbiet.BuildConfig;
import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.models.Versions;
import in.ac.bkbiet.bkbiet.models.VersionsAdapter;
import in.ac.bkbiet.bkbiet.utils.SQLiteHandler;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Sv;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * About Created by Ashish on 9/5/2017.
 */

public class About extends AppCompatActivity {
    TextView tv_version, tv_developer;
    RecyclerView rv_version_log;
    VersionsAdapter vAdapter;
    ArrayList<Versions> vList;
    int timesClicked = 0;
    int ms = 0;
    boolean threadRunning = false;
    TextView tv_intro;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initViews();
    }

    private void initViews() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tv_version = findViewById(R.id.tv_version);
        tv_version.setText(BuildConfig.VERSION_NAME);
        tv_developer = findViewById(R.id.tv_developer);
        tv_developer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryDeveloperSettings();
            }
        });

        tv_intro = findViewById(R.id.tv_intro);
        tv_intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(About.this, Intro.class));
                finish();
            }
        });

        rv_version_log = findViewById(R.id.rv_version_log);
        rv_version_log.setLayoutManager(new LinearLayoutManager(this));
        vAdapter = new VersionsAdapter(this);
        vList = new ArrayList<>();
        populateRecyclerView();

    }

    private void populateRecyclerView() {
        ValueEventListener versionListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    Versions version = child.getValue(Versions.class);
                    vList.add(version);
                }
                Collections.sort(vList);
                vAdapter.setListContent(vList);
                rv_version_log.setAdapter(vAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(About.this, "Failed to fetch Version Info.", Toast.LENGTH_LONG).show();
            }
        };
        dbRef.child("app_info").child("version_log").addListenerForSingleValueEvent(versionListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    private void tryDeveloperSettings() {
        if (Uv.isDevOn) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Close Dev Settings?");
            adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                    db.setPref(Uv.pIS_DEV_ON, ("false"));
                    db.close();
                    Uv.isDevOn = false;
                    Toast.makeText(getApplicationContext(), "Developer account revoked.", Toast.LENGTH_LONG).show();
                    Statics.restartApp(About.this);
                }
            });
            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            adb.show();

        } else {
            timesClicked++;
            if (timesClicked == 7) {
                ms = 0;
                openDeveloperSettings();
                return;
            }

            if (timesClicked > 4) {
                Toast.makeText(this, "You are " + (7 - timesClicked) + " clicks away from developer settings.", Toast.LENGTH_SHORT).show();
            }

            ms = 3000;
            if (!threadRunning) {
                try {
                    Thread th = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (ms > 0) {
                                try {
                                    Thread.sleep(200);
                                    ms -= 200;
                                } catch (InterruptedException ignored) {
                                }
                            }
                            timesClicked = 0;
                            threadRunning = false;
                        }
                    });
                    threadRunning = true;
                    th.start();
                } catch (IllegalThreadStateException ignored) {
                    ms = 0;
                    timesClicked = 0;
                    threadRunning = false;
                }
            }
        }
    }

    private void openDeveloperSettings() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Developer Settings");
        adb.setMessage("Please enter dynamic password to open developer settings.");

        final EditText password = new EditText(this);
        adb.setView(password);

        adb.setCancelable(false);
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        adb.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkDevPassword(password.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "You are now a developer.", Toast.LENGTH_LONG).show();
                    SQLiteHandler db = new SQLiteHandler(getApplicationContext());
                    db.setPref(Uv.pIS_DEV_ON, "true");
                    db.setPref(Sv.dSHOW_DEV_SETTINGS, "true");
                    db.close();
                    Uv.isDevOn = true;
                    Statics.restartApp(About.this);
                } else {
                    Toast.makeText(getApplicationContext(), "Oops! Wrong Password", Toast.LENGTH_LONG).show();
                }
            }
        });
        adb.show();
    }

    private boolean checkDevPassword(String pass) {
        return pass.matches("ashish" + "[A-Za-z_]*" + (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR) % 10) + "[A-Za-z0-9_]*"
                + (java.util.Calendar.getInstance().get(java.util.Calendar.DATE) % 10) + "[A-Za-z_]*" + "dev");
    }
}
