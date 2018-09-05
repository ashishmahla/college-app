package in.ac.bkbiet.bkbiet.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.models.User;
import in.ac.bkbiet.bkbiet.models.UserProfileAdapter;
import in.ac.bkbiet.bkbiet.utils.Uv;
import in.ac.bkbiet.bkbiet.utils.VolleyInit;

/**
 * UserProfiles Created by Ashish on 12/2/2017.
 */

public class UserProfiles extends AppCompatActivity {
    private static final String TAG = "UserProfiles";
    private List<User> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserProfileAdapter mAdapter;

    private MenuItem user_count_menu_item;
    private SwipeRefreshLayout swipe_users;
    private CardView card;
    private TextView tv_status;
    private ProgressBar pb_progress;
    private ImageView iv_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profiles);
        initViews();

        mAdapter = new UserProfileAdapter(userList);
        recyclerView.setAdapter(mAdapter);

        /*User user = new User("gintokisakata", "Gin", "gin@oddjobs.com", "", User.TYPE_TEACHER, "9939339393", "https://firebasestorage.googleapis.com/v0/b/bkbiet-ac-in.appspot.com/o/gin.jpg?alt=media&token=1fc15831-15a7-4e1f-8539-aea1cd31a316");
        userList.add(user);
        user = new User("goku", "Goku", "goku@dragonball.com", "", User.TYPE_STUDENT, "9945549155", "https://otakukart.com/animeblog/wp-content/uploads/2017/10/son_goku_ultra_instinct_form_by_rmehedi-dbpwu2b.png");
        userList.add(user);
        user = new User("strawhat", "Monkey D. Luffy", "luffy@strawhat.com", "", User.TYPE_ADMIN, "1423668878", "http://awswallpapershd.com/wp-content/uploads/2016/10/Monkey-D-Luffy-One-Piece-Free-Wallpaper-Background.jpg");
        userList.add(user);
        user = new User("kira", "Light", "light@kira.com", "", User.TYPE_STUDENT, "3393233932", "https://data.whicdn.com/images/6661611/large.jpg");
        userList.add(user);*/

        prepareUserData();
    }

    private void initViews() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.rv_up_profiles);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        swipe_users = findViewById(R.id.swipe_users);
        swipe_users.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipe_users.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareUserData();
            }
        });

        card = findViewById(R.id.card_up_loading);
        tv_status = findViewById(R.id.tv_up_status);
        pb_progress = findViewById(R.id.pb_up_progress);
        iv_error = findViewById(R.id.iv_up_error);
    }

    private void prepareUserData() {
        //----------------------------------------------------------------------------------------------------
        StringRequest strReq = new StringRequest(Request.Method.POST, Uv.sURL_EXTRA_FUNCTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get all users response : " + response);
                swipe_users.setRefreshing(false);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error && Integer.parseInt(jObj.getJSONObject("users").getString("user_count")) > 0) {
                        JSONObject users = jObj.getJSONObject("users");
                        int userCount = Integer.parseInt(users.getString("user_count"));

                        user_count_menu_item.setTitle(userCount + "");
                        userList.clear();
                        for (int i = userCount - 1; i >= 0; i--) {
                            JSONObject userJObj = users.getJSONObject(i + "");
                            String name = userJObj.getString("name");
                            String username = userJObj.getString("username");
                            String email = userJObj.getString("email");
                            String mobile_no = userJObj.getString("mobile_no");
                            int type = userJObj.getInt("type");
                            String dpLink = userJObj.getString("dp_link");
                            String createdAt = userJObj.getString("created_at");

                            int user_type;
                            switch (type) {
                                case 2: // teacher
                                    user_type = User.TYPE_TEACHER;
                                    break;
                                case 4: // student
                                    user_type = User.TYPE_STUDENT;
                                    break;
                                case 1: // admin
                                    user_type = User.TYPE_ADMIN;
                                    break;
                                default: // others
                                    user_type = User.TYPE_OTHER;
                                    break;
                            }

                            User user = new User(username, name, email, "-password-", user_type, mobile_no, dpLink);
                            userList.add(user);

                            mAdapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);
                            card.setVisibility(View.GONE);
                        }
                    } else {
                        // Error. Get the error message
                        String errorMsg = jObj.getString("error_msg");

                        recyclerView.setVisibility(View.GONE);
                        card.setVisibility(View.VISIBLE);
                        tv_status.setText("Oops! \n\n" + errorMsg);
                        pb_progress.setVisibility(View.GONE);
                        iv_error.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();

                    recyclerView.setVisibility(View.GONE);
                    card.setVisibility(View.VISIBLE);
                    tv_status.setText("JSON ERROR : " + e.getMessage());
                    pb_progress.setVisibility(View.GONE);
                    iv_error.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipe_users.setRefreshing(false);

                Log.e(TAG, "Error getting users: " + error.getMessage());
                String errorText = "ERROR: Cannot access user details.";
                if (Uv.isDevOn)
                    errorText += "\n\n(" + error.getMessage() + ")";

                recyclerView.setVisibility(View.GONE);
                card.setVisibility(View.VISIBLE);
                tv_status.setText(errorText);
                pb_progress.setVisibility(View.GONE);
                iv_error.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String authReq = (Uv.isDevOn) ? "false" : "true";
                params.put("is_user_auth_req", authReq);
                params.put("auth_username", Uv.currUser.getUsername());
                params.put("function_name", "get_all_user_details");
                return params;
            }
        };
        VolleyInit.getInstance().addToRequestQueue(strReq, "request_for_users");
        //-----------------------------------------------------------------------------------------------
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        this.user_count_menu_item = menu.findItem(R.id.info_menu_item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}