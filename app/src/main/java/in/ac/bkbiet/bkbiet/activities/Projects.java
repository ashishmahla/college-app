package in.ac.bkbiet.bkbiet.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import in.ac.bkbiet.bkbiet.models.Project;
import in.ac.bkbiet.bkbiet.models.ProjectAdapter;
import in.ac.bkbiet.bkbiet.models.User;
import in.ac.bkbiet.bkbiet.utils.Uv;
import in.ac.bkbiet.bkbiet.utils.VolleyInit;

/**
 * ProjectsProfiles Created by Ashish on 12/2/2017.
 */

public class Projects extends AppCompatActivity implements ProjectAdapter.ProjectClickListener {
    private static final String TAG = "ProjectsList";
    private List<Project> projectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProjectAdapter mAdapter;

    private MenuItem project_count_menu_item;
    private CardView card;
    private TextView tv_status;
    private ProgressBar pb_progress;
    private ImageView iv_error;
    private SwipeRefreshLayout swipe_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        initViews();

        mAdapter = new ProjectAdapter(projectList, this);
        recyclerView.setAdapter(mAdapter);
        User user1 = new User("gintokisakata", "Gin", "gin@oddjobs.com", "", User.TYPE_TEACHER, "9939339393", "https://firebasestorage.googleapis.com/v0/b/bkbiet-ac-in.appspot.com/o/gin.jpg?alt=media&token=1fc15831-15a7-4e1f-8539-aea1cd31a316");
        User user2 = new User("goku", "Goku", "goku@dragonball.com", "", User.TYPE_STUDENT, "9945549155", "https://otakukart.com/animeblog/wp-content/uploads/2017/10/son_goku_ultra_instinct_form_by_rmehedi-dbpwu2b.png");
        User user3 = new User("strawhat", "Monkey D. Luffy", "luffy@strawhat.com", "", User.TYPE_ADMIN, "1423668878", "http://awswallpapershd.com/wp-content/uploads/2016/10/Monkey-D-Luffy-One-Piece-Free-Wallpaper-Background.jpg");
        User user4 = new User("kira", "Light", "light@kira.com", "", User.TYPE_STUDENT, "3393233932", "https://data.whicdn.com/images/6661611/large.jpg");
        Project project = new Project(2, "Find OnePiece", "Dark King", "Going Mary", new User[]{user1}, "", Project.STATUS_APPROVED);


        prepareProjectsData();
    }

    private void initViews() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        swipe_refresh = findViewById(R.id.swipe_projects);
        swipe_refresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareProjectsData();
            }
        });

        recyclerView = findViewById(R.id.rv_pl);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        card = findViewById(R.id.card_pl_loading);
        tv_status = findViewById(R.id.tv_pl_status);
        pb_progress = findViewById(R.id.pb_pl_progress);
        iv_error = findViewById(R.id.iv_pl_error);
    }

    private void prepareProjectsData() {
        //----------------------------------------------------------------------------------------------------
        StringRequest strReq = new StringRequest(Request.Method.POST, Uv.sURL_EXTRA_FUNCTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                swipe_refresh.setRefreshing(false);
                Log.d(TAG, "Get all projects response : " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error && Integer.parseInt(jObj.getJSONObject("projects").getString("project_count")) > 0) {
                        JSONObject projects = jObj.getJSONObject("projects");
                        int projectCount = Integer.parseInt(projects.getString("project_count"));

                        project_count_menu_item.setTitle(projectCount + "");
                        // TODO: 12/22/2017 uncomment it
                        //projectList.clear();
                        for (int i = projectCount - 1; i >= 0; i--) {
                            JSONObject projectJObj = projects.getJSONObject(i + "");
                            int id = projectJObj.getInt("id");
                            String name = projectJObj.getString("name");
                            String tech = projectJObj.getString("tech");
                            int project_status = projectJObj.getInt("status");
                            String adviser = projectJObj.getString("adviser");

                            JSONObject dev1 = projectJObj.getJSONObject("dev1");
                            User user1 = null;
                            if (!dev1.getString("username").equalsIgnoreCase("notset")) {
                                user1 = new User();
                                user1.setName(dev1.getString("name"));
                                user1.setUsername(dev1.getString("username"));
                                user1.setDpLink(dev1.getString("dp_link"));
                                user1.setEmail(dev1.getString("email"));
                                user1.setMobileNo(dev1.getString("mobile_no"));
                            }

                            JSONObject dev2 = projectJObj.getJSONObject("dev2");
                            User user2 = null;
                            if (!dev2.getString("username").equalsIgnoreCase("notset")) {
                                user2 = new User();
                                user2.setName(dev2.getString("name"));
                                user2.setUsername(dev2.getString("username"));
                                user2.setDpLink(dev2.getString("dp_link"));
                                user2.setEmail(dev2.getString("email"));
                                user2.setMobileNo(dev2.getString("mobile_no"));
                            }

                            String desc = projectJObj.getString("desc");
                            String created_on = projectJObj.getString("created_on");

                            int status;
                            switch (project_status) {
                                case 0: // Waiting Review
                                    status = Project.STATUS_WAITING_REVIEW;
                                    break;
                                case 1: // Approved
                                    status = Project.STATUS_APPROVED;
                                    break;
                                case 2: // Rejected
                                    status = Project.STATUS_REJECTED;
                                    break;
                                default: // Others
                                    status = -1;
                                    break;
                            }

                            Project project = new Project(id, name, adviser, tech, new User[]{user1, user2}, desc, status, created_on);
                            projectList.add(project);

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
                swipe_refresh.setRefreshing(false);
                Log.e(TAG, "Error getting projects: " + error.getMessage());
                String errorText = "ERROR: Cannot access project details.";
                if (Uv.isDevOn) {
                    errorText += "\n\n(" + error.getMessage() + ")";
                }

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
                params.put("function_name", "get_all_projects");
                return params;
            }
        };
        VolleyInit.getInstance().addToRequestQueue(strReq, "request_for_projects");
        //-----------------------------------------------------------------------------------------------
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        project_count_menu_item = menu.findItem(R.id.info_menu_item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onProjectClicked(View view, int position) {
        Project project = projectList.get(position);
        AlertDialog.Builder adb = new AlertDialog.Builder(Projects.this);
        adb.setTitle("(Id: " + project.getId() + ") " + project.getName());
        adb.setMessage("Adviser: " + project.getAdviser() + "\n\nDescription:\n" + project.getDesc());
        adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        adb.show();
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