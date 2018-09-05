package in.ac.bkbiet.bkbiet.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.models.AdviserAdapter;
import in.ac.bkbiet.bkbiet.models.AuditRecord;
import in.ac.bkbiet.bkbiet.models.Project;
import in.ac.bkbiet.bkbiet.models.User;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;
import in.ac.bkbiet.bkbiet.utils.VolleyInit;

import static in.ac.bkbiet.bkbiet.utils.Statics.isCollegeIdValid;

/**
 * ProjectReg Created by Ashish on 12/5/2017.
 */

public class ProjectReg extends Activity implements AdviserAdapter.RecyclerViewItemClickListener {

    private static final int cINST = 0;
    private static final int cMAIN = 1;
    private static final String TAG = "ProjectReg";
    private static final int PAGE_TURNING_DURATION = 1000;
    private static final float PAGE_TURNING_DISTANCE = 1000;
    RelativeLayout mainPage;
    TextInputEditText title, tech, partner;
    ArrayList<User> adviserList = new ArrayList<>();
    ArrayList<String> adviserNameList = new ArrayList<>();
    CardView card_adviser;
    TextInputEditText desc;
    TextInputEditText tiet_adviser;
    LinearLayout instPage;
    ProgressBar pb;
    TextView status, instructions, closingDate, regType, instHeader;
    LinearLayout project_reg_info;
    ImageView iv_error;
    Project_Reg_Info pri;
    CardView card_loading;
    Button cont;
    Button send;
    TextInputLayout til_title, til_tech, til_adviser, til_partner, til_desc;
    private int currCard = cINST;
    private Project project = new Project();
    private DatabaseReference db_project_reg_info_ref = FirebaseDatabase.getInstance().getReference().child("project_reg");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_reg);

        initCardInstruction();
        initCardMainPage();

        getProjectRegInfo();
    }

    private void initCardInstruction() {
        // CARD 1 INSTRUCTIONS
        card_loading = findViewById(R.id.card_pr_loading);
        cont = findViewById(R.id.b_pr_continue);
        project_reg_info = findViewById(R.id.ll_project_reg_info);
        instPage = findViewById(R.id.ll_pr_inst);
        pb = findViewById(R.id.pb_pr_progress);
        status = findViewById(R.id.tv_pr_status);
        iv_error = findViewById(R.id.iv_pr_error);
        instructions = findViewById(R.id.tv_pr_instructions);
        closingDate = findViewById(R.id.tv_closingDate);
        regType = findViewById(R.id.tv_pr_reg_type);
        instHeader = findViewById(R.id.tv_pr_inst_header);
    }

    private void getProjectRegInfo() {
        db_project_reg_info_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pri = dataSnapshot.getValue(Project_Reg_Info.class);
                project_reg_info.setVisibility(View.VISIBLE);


                regType.setText(pri.getRegType());
                closingDate.setText(pri.getClosingDate());

                if (!pri.isOpen) {
                    pb.setVisibility(View.GONE);
                    status.setText("Registrations Closed");
                } else {
                    getAdviserList();
                }

                if (pri.isInst) {
                    instHeader.setText(pri.instHeader);
                    instructions.setText(pri.instructions);
                } else {
                    instHeader.setVisibility(View.GONE);
                    instructions.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pb.setVisibility(View.GONE);
                iv_error.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initCardMainPage() {
        mainPage = findViewById(R.id.card_pr_main_page);
        tiet_adviser = findViewById(R.id.tiet_pr_adviser);
        card_adviser = findViewById(R.id.card_pr_adviser);
        title = findViewById(R.id.tiet_pr_title);
        tech = findViewById(R.id.tiet_pr_tech);
        partner = findViewById(R.id.tiet_pr_partner);
        desc = findViewById(R.id.tiet_pr_desc);
        send = findViewById(R.id.b_pr_send);

        til_title = findViewById(R.id.til_pr_title);
        til_tech = findViewById(R.id.til_pr_desc);
        til_adviser = findViewById(R.id.til_pr_adviser);
        til_partner = findViewById(R.id.til_pr_partner);
        til_desc = findViewById(R.id.til_pr_tech);

        tiet_adviser.setMovementMethod(null);
        tiet_adviser.setKeyListener(null);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    private boolean mainPageValidity() {
        boolean isValid = true;

        til_title.setError("");
        til_tech.setError("");
        til_adviser.setError("");
        til_partner.setError("");
        til_desc.setError("");

        if (title.getText().length() == 0) {
            isValid = false;
            til_title.setError("Title is required.");
        }

        if (tech.getText().length() == 0) {
            isValid = false;
            til_tech.setError("Tech/Language is required.");
        }

        if (tiet_adviser.getText().length() == 0) {
            isValid = false;
            til_adviser.setError("Adviser is required.");
        }

        if (partner.getText().length() > 0 && isCollegeIdValid(partner.getText().toString())) {
            isValid = false;
            til_partner.setError("Partner Id not Valid.");
        }

        if (desc.getText().length() == 0) {
            isValid = false;
            til_desc.setError("Description is required.");
        }

        if (!isValid) {
            project.setDesc(desc.getText().toString());
        }

        if (isValid) {
            project.setName(title.getText().toString());
            project.setTech(tech.getText().toString());
            project.setAdviser(tiet_adviser.getText().toString());
            project.setDesc(desc.getText().toString());
            if (partner.getText().length() > 0) {
                project.setDevelopers(new User[]{new User(Uv.currUser.getUsername()), new User(partner.getText().toString())});
            } else {
                project.setDevelopers(new User[]{new User(Uv.currUser.getUsername()), new User(0 + "")});
            }
        }

        return isValid;
    }

    private void getAdviserList() {
        StringRequest strReq = new StringRequest(Request.Method.POST, Uv.sURL_EXTRA_FUNCTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get All Advisers Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error && Integer.parseInt(jObj.getJSONObject("teachers").getString("teach_count")) > 0) {
                        JSONObject teachers = jObj.getJSONObject("teachers");
                        int teachCount = Integer.parseInt(teachers.getString("teach_count"));
                        for (int i = 0; i < teachCount; i++) {
                            JSONObject teacher = teachers.getJSONObject(i + "");
                            String name = teacher.getString("name");
                            String username = teacher.getString("username");
                            String dp_link = teacher.getString("dp_link");
                            String email = teacher.getString("email");

                            User adviser = new User(username, name, email, "", User.TYPE_TEACHER, "", dp_link);
                            Log.e(TAG, adviser.getName() + " : " + name);
                            adviserList.add(adviser);
                            adviserNameList.add(adviser.getName());
                        }

                        final CharSequence[] advisers = adviserNameList.toArray(new String[]{});
                        //final String[] strings = adviserNameList.toArray(new String[]{});

                        card_adviser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ProjectReg.this);
                                builder.setTitle("Select Adviser");
                                builder.setSingleChoiceItems(advisers, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        project.setAdviser(adviserList.get(which).getUsername());
                                        tiet_adviser.setText(adviserList.get(which).getName());
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        });
                        pb.setVisibility(View.GONE);
                        status.setText("Registration form is ready.");
                        cont.setVisibility(View.VISIBLE);
                        cont.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                next();
                            }
                        });
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        pb.setVisibility(View.GONE);
                        status.setText(errorMsg);
                        iv_error.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    pb.setVisibility(View.GONE);
                    status.setText("Error: Can't load adviser list.\n\n" + (Uv.isDevOn ? e.getMessage() : ""));
                    iv_error.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Fetching list : " + error.getMessage());

                pb.setVisibility(View.GONE);
                status.setText("Error: Unable to load adviser list.\n\n" + (Uv.isDevOn ? error : ""));
                iv_error.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String authReq = (Uv.isDevOn) ? "false" : "true";
                params.put("is_user_auth_req", authReq);
                params.put("auth_username", Uv.currUser.getUsername());
                params.put("function_name", "get_all_teachers");
                return params;
            }
        };
        VolleyInit.getInstance().addToRequestQueue(strReq, "request_for_advisers");
        //-----------------------------------------------------------------------------------------------
    }

    private void showErrorDialog(String msg) {
        AlertDialog.Builder adb = new AlertDialog.Builder(ProjectReg.this);
        adb.setTitle("Error");
        adb.setMessage(msg);
        adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();
    }

    private void uploadProject() {
        send.setClickable(false);
        send.setText("Sending...");
        final ProgressBar pb_send = findViewById(R.id.pb_pr_upload);
        pb_send.setVisibility(View.VISIBLE);

        StringRequest strReq = new StringRequest(Request.Method.POST, Uv.sURL_EXTRA_FUNCTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pb_send.setVisibility(View.GONE);
                Log.d(TAG, "Upload Project: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        AuditRecord auditRecord = new AuditRecord("ProjectReg", "pName#:" + project.getName());
                        Statics.saveNewAuditRecord(auditRecord);
                        runUploadingSuccessfulAnimations();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        showErrorDialog(errorMsg);
                        send.setText("Send");
                        send.setClickable(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorDialog(e.getMessage());
                    send.setText("Send");
                    send.setClickable(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Uploading Project : " + error.getMessage());
                showErrorDialog("Unable to register.");
                send.setText("Send");
                send.setClickable(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String authReq = (Uv.isDevOn) ? "false" : "true";
                params.put("is_user_auth_req", authReq);
                params.put("auth_username", Uv.currUser.getUsername());
                params.put("function_name", "register_project");

                params.put("username", project.getDevelopers()[0].getUsername());
                params.put("title", project.getName());
                params.put("desc", project.getDesc());
                params.put("tech", project.getTech());
                params.put("adviser", project.getAdviser());
                params.put("partner", project.getDevelopers()[1].getUsername());

                return params;
            }
        };
        VolleyInit.getInstance().addToRequestQueue(strReq, "request_for_project_upload");
        //-----------------------------------------------------------------------------------------------
    }

    private void runUploadingSuccessfulAnimations() {
        mainPage.animate().scaleX(0.3f).scaleY(.3f).translationY(400).setDuration(1000).withEndAction(new Runnable() {
            @Override
            public void run() {
                mainPage.animate().rotation(0).translationY(-2000).setDuration(700).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        instPage.animate().translationX(0).alpha(0).scaleX(1).scaleY(1).setDuration(0);
                        instHeader.setVisibility(View.GONE);
                        instructions.setVisibility(View.GONE);
                        closingDate.setVisibility(View.GONE);
                        regType.setVisibility(View.GONE);
                        status.setText("Project Registration Successful.");
                        cont.setText("Close");
                        cont.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        instPage.setVisibility(View.VISIBLE);
                        instPage.animate().alpha(1).setDuration(1000);
                    }
                });
            }
        });
    }

    private void next() {
        switch (currCard) {
            case cINST:
                turnPage();
                break;
            case cMAIN:
                if (mainPageValidity()) {
                    uploadProject();
                }
                break;
        }
    }

    private void turnPage() {
        currCard++;
        float scale = 0.5f;
        float trans = -PAGE_TURNING_DISTANCE;
        float alpha = 0.9f;

        mainPage.setVisibility(View.VISIBLE);
        instPage.animate().scaleX(scale).scaleY(scale).translationX(trans).alpha(alpha).setDuration(PAGE_TURNING_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                instPage.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRecyclerViewItemClicked(View view, int position) {
        Log.i("ProjectReg", "Click detected");
        project.setAdviser(adviserList.get(position).getUsername());
        next();
    }

    private static class Project_Reg_Info {
        public boolean isOpen;
        public boolean isInst;
        String regType;
        String closingDate;
        String instHeader;
        String instructions;

        public Project_Reg_Info() {

        }

        public String getClosingDate() {
            return closingDate;
        }

        public void setClosingDate(String closingDate) {
            this.closingDate = closingDate;
        }

        public String getInstHeader() {
            return instHeader;
        }

        public void setInstHeader(String instHeader) {
            this.instHeader = instHeader;
        }

        public String getInstructions() {
            return instructions;
        }

        public void setInstructions(String instructions) {
            this.instructions = instructions;
        }

        public String getRegType() {
            return regType;
        }

        public void setRegType(String regType) {
            this.regType = regType;
        }

        public void setInst(boolean inst) {
            isInst = inst;
        }

        public void setOpen(boolean open) {
            isOpen = open;
        }
    }
}