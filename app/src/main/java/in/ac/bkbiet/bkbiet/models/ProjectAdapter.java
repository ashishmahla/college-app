package in.ac.bkbiet.bkbiet.models;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.utils.ImageLoading.ImageLoader;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;
import in.ac.bkbiet.bkbiet.utils.VolleyInit;

/**
 * Created by Ashish on 12/4/2017.
 */

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.MyViewHolder> {

    private static final String TAG = "ProjectAdapter";
    ProjectClickListener myProjectClickListener;
    private List<Project> projectsList;

    public ProjectAdapter(List<Project> projectsList, ProjectClickListener myProjectClickListener) {
        this.projectsList = projectsList;
        this.myProjectClickListener = myProjectClickListener;
        setHasStableIds(true);
    }

    @Override
    public ProjectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewStatus) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_project, parent, false);

        return new ProjectAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProjectAdapter.MyViewHolder holder, int position) {
        final Project project = projectsList.get(position);

        holder.tv_name.setText(project.getName());
        holder.tv_tech.setText(project.getTech());
        holder.tv_desc.setText(project.getDesc());
        holder.tv_adviser.setText((project.getAdviser()));

        switch (project.getStatus()) {
            case Project.STATUS_APPROVED:
                holder.iv_status.setBackgroundResource(R.drawable.ic_project_approved);
                holder.iv_status.setColorFilter(Color.argb(255, 24, 140, 30));
                break;
            case Project.STATUS_WAITING_REVIEW:
                holder.iv_status.setBackgroundResource(R.drawable.ic_project_waiting_review);
                holder.iv_status.setColorFilter(Color.argb(255, 255, 235, 59));
                break;
            case Project.STATUS_REJECTED:
                holder.iv_status.setBackgroundResource(R.drawable.ic_project_rejected);
                holder.iv_status.setColorFilter(Color.argb(255, 210, 0, 0));
                break;
            default:
                holder.iv_status.setVisibility(View.GONE);
        }

        holder.iv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] status = new int[]{project.getStatus()};
                AlertDialog.Builder adb = new AlertDialog.Builder(holder.iv_status.getContext());
                adb.setTitle("Change Status?");
                adb.setCancelable(false);
                adb.setSingleChoiceItems(new String[]{"Waiting", "Approved", "Rejected"}, status[0], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        status[0] = which;
                        //Log.i("ProjectAdapter", "Clicked with which = " + which);
                    }
                });

                adb.setNegativeButton("Cancel", null);

                adb.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeProjectStatus(holder.iv_status.getContext(), project.getId(), status[0], holder.iv_status);
                    }
                });

                adb.show();
            }
        });

        int student_count = 0;
        if (project.getDevelopers()[0] != null) {
            student_count++;
            holder.tv_dev1_name.setText(project.getDevelopers()[0].getName());
            holder.tv_dev1_username.setText(project.getDevelopers()[0].getUsername());
            ImageLoader imageLoader = new ImageLoader(holder.iv_dev1_dp.getContext());
            imageLoader.displayImage(project.getDevelopers()[0].getDpLink(), holder.iv_dev1_dp, project.getDevelopers()[0].getName());

            holder.iv_dev1_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Statics.openDialerIntent(holder.iv_dev1_call.getContext(), project.getDevelopers()[0].getMobileNo());
                    AuditRecord auditRecord = new AuditRecord("OpenMobileNo", "target_username#:" + project.getDevelopers()[0].getUsername() + "#;" +
                            "target_name#:" + project.getDevelopers()[0].getName() + "#;" + "target_mobileNo#:" + project.getDevelopers()[0].getMobileNo());
                    Statics.saveNewAuditRecord(auditRecord);
                }
            });
            holder.iv_dev1_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Statics.openMailIntent(holder.iv_dev1_email.getContext(), new String[]{project.getDevelopers()[0].getEmail()});
                    AuditRecord auditRecord = new AuditRecord("OpenEmail", "target_username#:" + project.getDevelopers()[0].getUsername() + "#;" +
                            "target_name#:" + project.getDevelopers()[0].getName() + "#;" + "target_email:" + project.getDevelopers()[0].getEmail());
                    Statics.saveNewAuditRecord(auditRecord);
                }
            });
        } else {
            holder.card_first_dev.setVisibility(View.GONE);
        }

        if (project.getDevelopers()[1] != null) {
            student_count++;
            holder.tv_dev2_name.setText(project.getDevelopers()[1].getName());
            holder.tv_dev2_username.setText(project.getDevelopers()[1].getUsername());
            ImageLoader imageLoader = new ImageLoader(holder.iv_dev2_dp.getContext());
            imageLoader.displayImage(project.getDevelopers()[1].getDpLink(), holder.iv_dev2_dp, project.getDevelopers()[1].getName());

            holder.iv_dev2_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuditRecord auditRecord = new AuditRecord("OpenMobileNo", "target_username#:" + project.getDevelopers()[1].getUsername() + "#;" +
                            "target_name#:" + project.getDevelopers()[1].getName() + "#;" + "target_mobileNo#:" + project.getDevelopers()[1].getMobileNo());
                    Statics.saveNewAuditRecord(auditRecord);
                    Statics.openDialerIntent(holder.iv_dev2_call.getContext(), project.getDevelopers()[1].getMobileNo());
                }
            });
            holder.iv_dev2_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuditRecord auditRecord = new AuditRecord("OpenEmail", "target_username#:" + project.getDevelopers()[1].getUsername() + "#;" +
                            "target_name#:" + project.getDevelopers()[1].getName() + "#;" + "target_email#:" + project.getDevelopers()[1].getEmail());
                    Statics.saveNewAuditRecord(auditRecord);
                    Statics.openMailIntent(holder.iv_dev2_email.getContext(), new String[]{project.getDevelopers()[1].getEmail()});
                }
            });
        } else {
            holder.card_second_dev.setVisibility(View.GONE);
        }
        holder.tv_student_count.setText(student_count == 1 ? "Student : 1" : "Student : 2");
    }

    private void setStatusToImage(ImageView iv_status, int status) {
        switch (status) {
            case Project.STATUS_APPROVED:
                iv_status.setBackgroundResource(R.drawable.ic_project_approved);
                iv_status.setColorFilter(Color.argb(255, 24, 140, 30));
                break;
            case Project.STATUS_WAITING_REVIEW:
                iv_status.setBackgroundResource(R.drawable.ic_project_waiting_review);
                iv_status.setColorFilter(Color.argb(255, 255, 235, 59));
                break;
            case Project.STATUS_REJECTED:
                iv_status.setBackgroundResource(R.drawable.ic_project_rejected);
                iv_status.setColorFilter(Color.argb(255, 210, 0, 0));
                break;
            default:
                iv_status.setVisibility(View.GONE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return projectsList.size();
    }

    private void changeProjectStatus(final Context context, final int id, final int status, final ImageView imageView) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Changing Status");
        progressDialog.show();
        progressDialog.setCancelable(false);
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setNegativeButton("Close", null);

        StringRequest strReq = new StringRequest(Request.Method.POST, Uv.sURL_EXTRA_FUNCTIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.cancel();
                progressDialog.setIndeterminate(false);

                Log.d(TAG, "Change project status response : " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        setStatusToImage(imageView, status);
                        adb.setTitle("Hurry!");
                        adb.setMessage("Status successfully updated.");
                        AuditRecord auditRecord = new AuditRecord("ChangedProjectStatus", "id#:" + id + "#;" + "status#:" + status + "#;");
                        Statics.saveNewAuditRecord(auditRecord);
                    } else {
                        // Error. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        adb.setTitle("Error!");
                        adb.setMessage(errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    adb.setTitle("JSON Error!");
                    adb.setMessage(e.getMessage());
                }
                adb.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error getting projects: " + error.getMessage());
                adb.setTitle("ERROR!");
                String errorText = "Unable to update status.";
                if (Uv.isDevOn) {
                    errorText += "\n\n(" + error.getMessage() + ")";
                }
                adb.setMessage(errorText);
                adb.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String authReq = (Uv.isDevOn) ? "false" : "true";
                params.put("is_user_auth_req", authReq);
                params.put("auth_username", Uv.currUser.getUsername());
                params.put("function_name", "change_project_status_with_id");
                params.put("id", id + "");
                params.put("status", status + "");
                return params;
            }
        };
        VolleyInit.getInstance().addToRequestQueue(strReq, "request_for_projects");
    }

    public interface ProjectClickListener {
        void onProjectClicked(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_tech, tv_desc, tv_adviser, tv_student_count;
        ImageView iv_status;
        CardView card_first_dev, card_second_dev;

        TextView tv_dev1_name, tv_dev1_username;
        TextView tv_dev2_name, tv_dev2_username;
        ImageView iv_dev1_dp, iv_dev2_dp;
        ImageView iv_dev1_call, iv_dev1_email, iv_dev2_call, iv_dev2_email;


        MyViewHolder(final View view) {
            super(view);
            tv_student_count = view.findViewById(R.id.tv_pm_student_count);
            tv_name = view.findViewById(R.id.tv_pm_name);
            tv_tech = view.findViewById(R.id.tv_pm_tech);
            tv_desc = view.findViewById(R.id.tv_pm_desc);
            tv_adviser = view.findViewById(R.id.tv_pm_adviser);
            iv_status = view.findViewById(R.id.iv_pm_status);

            card_first_dev = view.findViewById(R.id.mpd_first_dev);
            tv_dev1_name = card_first_dev.findViewById(R.id.tv_mpd_name);
            tv_dev1_username = card_first_dev.findViewById(R.id.tv_mpd_username);
            iv_dev1_dp = card_first_dev.findViewById(R.id.iv_mpd_dev_dp);
            iv_dev1_call = card_first_dev.findViewById(R.id.iv_mpd_dev_call);
            iv_dev1_email = card_first_dev.findViewById(R.id.iv_mpd_dev_mail);

            card_second_dev = view.findViewById(R.id.mpd_second_dev);
            tv_dev2_name = card_second_dev.findViewById(R.id.tv_mpd_name);
            tv_dev2_username = card_second_dev.findViewById(R.id.tv_mpd_username);
            iv_dev2_dp = card_second_dev.findViewById(R.id.iv_mpd_dev_dp);
            iv_dev2_call = card_second_dev.findViewById(R.id.iv_mpd_dev_call);
            iv_dev2_email = card_second_dev.findViewById(R.id.iv_mpd_dev_mail);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myProjectClickListener.onProjectClicked(v, getLayoutPosition());
        }
    }
}