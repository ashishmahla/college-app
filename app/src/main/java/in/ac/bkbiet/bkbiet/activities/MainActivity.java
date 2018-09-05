package in.ac.bkbiet.bkbiet.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.fragments.FragChats;
import in.ac.bkbiet.bkbiet.fragments.FragNotices;
import in.ac.bkbiet.bkbiet.fragments.FragNoties;
import in.ac.bkbiet.bkbiet.fragments.FragProfile;
import in.ac.bkbiet.bkbiet.fragments.FragWeb;
import in.ac.bkbiet.bkbiet.models.Chat;
import in.ac.bkbiet.bkbiet.models.Message;
import in.ac.bkbiet.bkbiet.models.User;
import in.ac.bkbiet.bkbiet.unofficial.ActPerksAndBerries;
import in.ac.bkbiet.bkbiet.unofficial.ActResultPrediction;
import in.ac.bkbiet.bkbiet.unofficial.ActivitySyllabus;
import in.ac.bkbiet.bkbiet.utils.SQLiteHandler;
import in.ac.bkbiet.bkbiet.utils.Uv;

import static in.ac.bkbiet.bkbiet.utils.Sv.dSHOW_AUDITS;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSHOW_DEV_SETTINGS;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSHOW_STUDENT_ACCESS;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSHOW_TEACHER_ACCESS;
import static in.ac.bkbiet.bkbiet.utils.Sv.getBooleanSetting;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int CHATS = 3;
    public static final int NOTICES = 4;
    public static final int WEB_CLIENT = 189;
    private static final int PROFILE = 1;
    private static final int NOTY = 2;
    Toolbar toolbar;
    int notyCount = 0;
    FragProfile profileFrag = new FragProfile();
    FragWeb fragWeb = new FragWeb();
    FragNoties notificationFrag = new FragNoties();
    FragChats chatsFrag = new FragChats();
    FragNotices noticesFrag = new FragNotices();
    ImageView iv_chats;
    TextView newChatBadge;
    int newChatCount = 0;
    ArrayList<Chat> chatsList = new ArrayList<>();
    ImageView iv_bell;
    TextView newNotyBadge;
    DatabaseReference convoRoot;
    ChildEventListener newMsgListener;
    String noNewItemColor = "#688ccc";          //a0a0c8
    String newItemColor = "#ffffff";
    private int openFragment = PROFILE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, Uv.sADMOB_ID);
        /*final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)           // Enables Crashlytics debugger
                .build();
        Fabric.with(fabric);*/

        if (savedInstanceState != null) {
            Uv.isLoggedIn = savedInstanceState.getBoolean("isLoggedIn");
            Uv.currUser = savedInstanceState.getParcelable("CurrentUser");
        }

        // TODO: 12/17/2017 uncomment
        //Crashlytics.log(Uv.currUser.getUsername() + " : " + Uv.currUser.getName());

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        //set nav drawer

        navigationView.inflateMenu(R.menu.nav_drawer);
        Menu menu = navigationView.getMenu();

        switch (Uv.currUser.getType()) {
            case User.TYPE_STUDENT:
                menu.setGroupVisible(R.id.nav_group_students, true);
                break;
            case User.TYPE_TEACHER:
                menu.setGroupVisible(R.id.nav_group_teachers, true);
                break;
            case User.TYPE_ADMIN:
                menu.setGroupVisible(R.id.nav_group_students, true);
                menu.setGroupVisible(R.id.nav_group_teachers, true);
                menu.setGroupVisible(R.id.nav_group_admin, true);
                break;
            default:
                break;
        }

        menu.findItem(R.id.nav_uo_syllabus).setVisible(true);
        menu.findItem(R.id.nav_uo_rewards).setVisible(true);
        menu.findItem(R.id.nav_uo_result_predict).setVisible(true);

        if (Uv.currUser.getEffectiveType() == User.TYPE_DEVELOPER) {
            if (getBooleanSetting(dSHOW_STUDENT_ACCESS, false)) {
                menu.setGroupVisible(R.id.nav_group_students, true);
            }

            if (getBooleanSetting(dSHOW_TEACHER_ACCESS, false)) {
                menu.setGroupVisible(R.id.nav_group_teachers, true);
            }

            menu.setGroupVisible(R.id.nav_group_admin, true);
            menu.findItem(R.id.nav_group_developer).setVisible(true);

            if (getBooleanSetting(dSHOW_AUDITS, false)) {
                menu.findItem(R.id.nav_audits).setVisible(true);
            }

            if (getBooleanSetting(dSHOW_DEV_SETTINGS, false)) {
                menu.findItem(R.id.nav_developer_settings).setVisible(true);
            }
        }

        // show new version available snackbar
        CoordinatorLayout cord = findViewById(R.id.content_main);
        if (Uv.isNewVersionAvailable) {
            Snackbar s = Snackbar.make(cord, Uv.updateMsg, Snackbar.LENGTH_LONG);
            if (Uv.isUpdateLinkAvailable) {
                s.setAction("UPDATE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = Uv.updateLink;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }
            // Changing message text color
            s.setActionTextColor(Color.RED);
            // Changing action button text color
            View sbView = s.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            textView.setTextSize(14);
            s.show();
        }

        /*//Setup ads
        switch (getIntSetting(dAD_TYPE_TO_SHOW, 1)) {
            case 1: // ads
                MobileAds.initialize(this, Uv.sADMOB_ID);
                break;
            case 2: // development ads
                MobileAds.initialize(this, Uv.sADMOB_DEV_ID);
                break;
            case 3: // no ads
                break;
            default: // ads
                MobileAds.initialize(this, Uv.sADMOB_ID);
                break;
        }*/

        navigationView.setNavigationItemSelectedListener(this);
        FragmentManager fragMan = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragTransaction = fragMan.beginTransaction();
        fragTransaction.replace(R.id.frag_container, profileFrag).commit();

        registerBroadcastListener();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (openFragment != PROFILE) {
            openFragment = PROFILE;
            FragmentManager fragMan = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragTransaction = fragMan.beginTransaction();
            fragTransaction.replace(R.id.frag_container, profileFrag).commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem itemBell = menu.findItem(R.id.action_bell);
        MenuItemCompat.setActionView(itemBell, R.layout.noty_counter);
        RelativeLayout rl_bell = (RelativeLayout) MenuItemCompat.getActionView(itemBell);
        iv_bell = rl_bell.findViewById(R.id.iv_bell);
        newNotyBadge = rl_bell.findViewById(R.id.new_noty_count);

        iv_bell.setColorFilter(Color.parseColor(noNewItemColor));
        rl_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment = CHATS;
                FragmentManager fragMan = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragTransaction = fragMan.beginTransaction();
                fragTransaction.replace(R.id.frag_container, notificationFrag);
                fragTransaction.commit();
            }
        });

        if (notyCount > 0) {
            String sNotyCount = "" + notyCount;
            newNotyBadge.setText(sNotyCount);
            newNotyBadge.setVisibility(View.VISIBLE);
            iv_bell.setColorFilter(Color.parseColor(newItemColor));
        } else {
            newNotyBadge.setVisibility(View.GONE);
            iv_bell.setColorFilter(Color.parseColor(noNewItemColor));
        }

        MenuItem itemChats = menu.findItem(R.id.action_chat);
        MenuItemCompat.setActionView(itemChats, R.layout.menu_layout_chats);
        RelativeLayout rl_chats = (RelativeLayout) MenuItemCompat.getActionView(itemChats);
        iv_chats = rl_chats.findViewById(R.id.iv_chats);
        newChatBadge = rl_chats.findViewById(R.id.new_chat_count);

        iv_chats.setColorFilter(Color.parseColor(noNewItemColor));
        rl_chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment = CHATS;
                FragmentManager fragMan = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragTransaction = fragMan.beginTransaction();
                fragTransaction.replace(R.id.frag_container, chatsFrag);
                fragTransaction.commit();
            }
        });
        registerNewChatListener();
        return super.onCreateOptionsMenu(menu);
    }

    private void setNotyCount(int notyCount) {
        this.notyCount = notyCount;
        if (newNotyBadge == null) {
            invalidateOptionsMenu();
        } else {
            if (notyCount > 0) {
                String sNotyCount = "" + notyCount;
                newNotyBadge.setText(sNotyCount);
                newNotyBadge.setVisibility(View.VISIBLE);
                iv_bell.setColorFilter(Color.parseColor(newItemColor));
            } else {
                newNotyBadge.setVisibility(View.GONE);
                iv_bell.setColorFilter(Color.parseColor(noNewItemColor));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        FragmentManager fragMan = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragTransaction = fragMan.beginTransaction();
        switch (id) {
            case R.id.action_bell:
                openFragment = NOTY;
                fragTransaction.replace(R.id.frag_container, notificationFrag);
                fragTransaction.commit();
                return true;
            case R.id.action_chat:
                openFragment = CHATS;
                fragTransaction.replace(R.id.frag_container, chatsFrag);
                fragTransaction.commit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragMan = getSupportFragmentManager();
        fragMan.popBackStack();
        android.support.v4.app.FragmentTransaction fragTransaction = fragMan.beginTransaction();

        switch (id) {
            case R.id.nav_profile:
                openFragment = PROFILE;
                fragTransaction.replace(R.id.frag_container, profileFrag).commit();
                break;

            case R.id.nav_web_client:
                openFragment = WEB_CLIENT;
                fragTransaction.replace(R.id.frag_container, fragWeb).commit();
                break;

            /*case R.id.nav_noties:
                openFragment = NOTY;
                fragTransaction.replace(R.id.frag_container, notificationFrag);
                fragTransaction.commit();
                break;

            case R.id.nav_chats:
                openFragment = CHATS;
                fragTransaction.replace(R.id.frag_container, chatsFrag);
                fragTransaction.commit();
                break;*/

            case R.id.nav_project_reg:
                startActivity(new Intent(MainActivity.this, ProjectReg.class));
                break;

            case R.id.nav_user_profiles:
                startActivity(new Intent(MainActivity.this, UserProfiles.class));
                break;

            case R.id.nav_projects_list:
                startActivity(new Intent(MainActivity.this, Projects.class));
                break;

            case R.id.nav_about:
                startActivity(new Intent(MainActivity.this, About.class));
                break;

            case R.id.nav_audits:
                startActivity(new Intent(MainActivity.this, Audits.class));
                break;

            case R.id.nav_send_noties:
                startActivity(new Intent(MainActivity.this, SendNoty.class));
                break;

            case R.id.nav_developer_settings:
                startActivity(new Intent(MainActivity.this, DevSettings.class));
                break;

            case R.id.nav_settings:
                startActivity(new Intent(MainActivity.this, Settings.class));
                break;

            case R.id.nav_uo_notices:
                openFragment = NOTICES;
                fragTransaction.replace(R.id.frag_container, noticesFrag).commit();
                break;

            case R.id.nav_uo_syllabus:
                startActivity(new Intent(MainActivity.this, ActivitySyllabus.class));
                break;

            case R.id.nav_uo_rewards:
                startActivity(new Intent(MainActivity.this, ActPerksAndBerries.class));
                break;

            case R.id.nav_uo_result_predict:
                startActivity(new Intent(MainActivity.this, ActResultPrediction.class));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        int unreadCount = db.getUnreadNotyCount();
        db.close();
        setNotyCount(unreadCount);
        //registerNewChatListener();
        super.onResume();
    }

    private void registerBroadcastListener() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (newNotyBadge != null) {
                            setNotyCount(intent.getIntExtra("unread_count", 0));
                        }
                    }
                }, new IntentFilter(Uv.ACTION_NOTY_COUNT_BROADCAST)
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("CurrentUser", Uv.currUser);
        outState.putBoolean("isLoggedIn", Uv.isLoggedIn);
        super.onSaveInstanceState(outState);
    }

    private void registerNewChatListener() {
        chatsList.clear();
        newChatCount = 0;
        if (Uv.currUser.getUsername() != null) {
            convoRoot = FirebaseDatabase.getInstance().getReference().child("chats").child("conversations").child(Uv.currUser.getUsername());
            if (newMsgListener != null) {
                convoRoot.removeEventListener(newMsgListener);
            }
            newMsgListener = convoRoot.addChildEventListener(new ChildEventListener() {
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
    }

    private void append_chat(DataSnapshot snapshot) {
        newChatCount = 0;
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
                Chat.Person tempPerson = c.getFirstPerson();
                c.setFirstPerson(c.getSecondPerson());
                c.setSecondPerson(tempPerson);
            }
            finalizeChatInfoWithMessage(found, position, c);
        }
    }

    private void finalizeChatInfoWithMessage(final boolean isUpdate, final int pos, final Chat c) {
        if (!c.getLast_msg().contains("Say Hi to ")) {
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

                        newChatCount = 0;
                        for (Chat chat : chatsList) {
                            if (!chat.getLastMessage().getSenderUsername().equals(Uv.currUser.getUsername()) &&
                                    chat.getLastMessage().getSeenAt().equals("not_yet_seen")) {
                                newChatCount++;
                            }
                        }

                        if (newChatCount > 0) {
                            iv_chats.setColorFilter(Color.parseColor(newItemColor));
                            newChatBadge.setVisibility(View.VISIBLE);
                            String cCount = "" + newChatCount;
                            newChatBadge.setText(cCount);
                        } else {
                            newChatBadge.setVisibility(View.GONE);
                            iv_chats.setColorFilter(Color.parseColor(noNewItemColor));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            } catch (Exception ignored) {
            }
        }
    }
}