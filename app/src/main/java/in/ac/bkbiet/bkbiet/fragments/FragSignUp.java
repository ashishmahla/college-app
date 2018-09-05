package in.ac.bkbiet.bkbiet.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.activities.MainActivity;
import in.ac.bkbiet.bkbiet.models.User;
import in.ac.bkbiet.bkbiet.utils.SQLiteHandler;
import in.ac.bkbiet.bkbiet.utils.Uv;
import in.ac.bkbiet.bkbiet.utils.VolleyInit;

import static in.ac.bkbiet.bkbiet.utils.Statics.isCollegeIdValid;

/**
 * FragSignUp Created by Ashish on 8/31/2017.
 */

public class FragSignUp extends Fragment {
    private static final String TAG = "FragSignUp";

    private TextInputEditText et_password, et_username, et_name, et_email, et_mobile_no;
    private ProgressBar progress;
    private TextInputLayout til_s_username, til_s_password, til_s_name, til_s_email;
    private ImageView iv_signUp;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_signup, container, false);
        initViews(rootView);


        return rootView;
    }

    private void initViews(View parent) {
        til_s_email = parent.findViewById(R.id.til_s_email);
        til_s_name = parent.findViewById(R.id.til_s_name);
        til_s_username = parent.findViewById(R.id.til_s_cid);
        til_s_password = parent.findViewById(R.id.til_s_password);

        et_email = parent.findViewById(R.id.et_s_email);
        et_name = parent.findViewById(R.id.et_s_name);
        et_username = parent.findViewById(R.id.et_s_cid);
        et_password = parent.findViewById(R.id.et_s_password);
        et_mobile_no = parent.findViewById(R.id.et_s_mobile_no);

        progress = parent.findViewById(R.id.pb_signup);
        iv_signUp = parent.findViewById(R.id.iv_signup);
        Button btn_signup = parent.findViewById(R.id.b_signup);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til_s_username.setError("");
                til_s_password.setError("");
                til_s_email.setError("");
                til_s_name.setError("");
                setUserColor(0);

                final String username = et_username.getText().toString();
                final String password = et_password.getText().toString();
                final String name = et_name.getText().toString();
                final String email = et_email.getText().toString();
                final String mobile_no = et_mobile_no.getText().toString();

                if (name.isEmpty()) {
                    til_s_name.setError("Name is required");

                } else if (username.isEmpty()) {
                    til_s_username.setError("College Id is required");
                } else if (isCollegeIdValid(username)) {
                    til_s_username.setError("CollegeId is not valid");
                } else if (email.isEmpty()) {
                    til_s_email.setError("Email is required");
                } else if (!email.contains("@") || !email.contains(".") || email.length() < 10) {
                    til_s_email.setError("Email format not valid");
                } else if (password.isEmpty()) {
                    til_s_password.setError("Password is required");
                } else if (password.length() < 8) {
                    til_s_password.setError("Password must be atleast 8 characters long");
                } else {
                    progress.setVisibility(View.VISIBLE);
                    registerUser(new User(username, name, email, password, User.TYPE_STUDENT), mobile_no);
                }
            }
        });
    }

    private void registerUser(final User user, final String mobile_no) {
        // Tag used to cancel the request
        String sSTRING_REQUEST = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST, Uv.sURL_SIGNUP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.setVisibility(View.GONE);
                Log.d(TAG, "Register Response: " + response);
                SQLiteHandler db = null;

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        boolean isActive = jObj.getInt("is_active")==1;
                        boolean isBanned = jObj.getInt("is_banned")==1;
                        setUserColor(2);
                        if (isActive && !isBanned) {
                            User user = new User();
                            user.setUsername(jObj.getString("user_username"));
                            user.setName(jObj.getString("user_name"));
                            user.setEmail(jObj.getString("user_email"));
                            switch (jObj.getInt("user_type")) {
                                case 2: // teacher
                                    user.setType(User.TYPE_TEACHER);
                                    break;
                                case 4: // student
                                    user.setType(User.TYPE_STUDENT);
                                    break;
                                case 1: // admin
                                    user.setType(User.TYPE_ADMIN);
                                    break;
                                default: // others
                                    user.setType(User.TYPE_OTHER);
                                    break;
                            }

                            user.setMobileNo(jObj.getString("user_mobile_no"));

                            user.setDpLink(jObj.getString("user_db_link"));

                            db = new SQLiteHandler(getActivity().getApplicationContext());
                            db.deleteUser();
                            db.addUser(user);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();

                        } else {
                            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                            final EditText tv_acty_code = new EditText(getActivity());
                            adb.setTitle("Registration Successful");
                            adb.setMessage("Your credentials have been registered successfully on the server.\n" +
                                    "Please verify your account using activation code sent to your email.");

                            adb.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            if(!isActive) {
                                adb.setView(tv_acty_code);
                                adb.setPositiveButton("Activate", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String acty_code = tv_acty_code.getText().toString();
                                        if(acty_code.isEmpty()){
                                            Toast.makeText(getActivity(),"Enter Activation Code",Toast.LENGTH_SHORT).show();
                                        }else {
                                            activateAccount(acty_code);
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                            adb.show();
                        }

                    } else {
                        // Error occurred in registration. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        setUserColor(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Internal Error.", Toast.LENGTH_LONG).show();
                } finally {
                    if (db != null) {
                        db.close();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null&& error.getMessage()!=null) {
                    Log.e(TAG, "Registration Error: " + error.getMessage());
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Unknown registration error. Try again later.", Toast.LENGTH_LONG).show();
                }
                setUserColor(1);
                progress.setVisibility(View.GONE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("user_username", user.getUsername());
                params.put("user_name", user.getName());
                params.put("user_email", user.getEmail());
                params.put("user_password", user.getPassword());
                params.put("user_mobile_no", mobile_no);
                //// TODO: 9/10/2017 for future use to obtain ip of user.
                params.put("user_last_ip", "--android app--");
                Log.i(TAG,"Sending params : "+ params);
                return params;
            }
        };
        // Adding request to request queue
        VolleyInit.getInstance().addToRequestQueue(strReq, sSTRING_REQUEST);
    }

    // TODO: 12/1/2017
    /*private void storeStudentDetails(JSONObject jObj) {
        Student student = new Student();
        SQLiteHandler db = null;
        try {
            student.setCollegeId(jObj.getString("username"));
            student.setName(jObj.getString("name"));
            student.setEmail(jObj.getString("email"));
            student.setContact(jObj.getString("contact"));
            student.setMothersName(jObj.getString("mother's_name"));
            student.setFathersName(jObj.getString("father's_name"));
            student.setFathersEmail(jObj.getString("father's_email"));
            student.setFathersContact(jObj.getString("father's_contact"));
            student.setDob(jObj.getString("dob"));
            student.setAddress(jObj.getString("address"));
            student.resetFields();
            Uv.currentStudent = student;
            db = new SQLiteHandler(getActivity().getApplicationContext());
            db.deleteStudent();
            db.addStudent(student);
        } catch (JSONException je) {
            Toast.makeText(getActivity(), "Error while parsing student details.", Toast.LENGTH_LONG).show();
            je.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }*/

    void setUserColor(int color) {
        switch (color) {
            case 0:
                iv_signUp.setColorFilter(Color.rgb(239, 239, 239));
                break;
            case 1:
                iv_signUp.setColorFilter(Color.rgb(213, 0, 0));
                break;
            case 2:
                iv_signUp.setColorFilter(Color.rgb(0, 213, 0));
                break;
        }
    }

    private void activateAccount(final String acty_code){
        StringRequest strReq = new StringRequest(Request.Method.POST, Uv.sURL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Activation Response : " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                        adb.setTitle("Activation Successful");
                        adb.setMessage("Greetings\n\nYour account has been activated. You can now login to your account.");
                        adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.show();
                    } else {
                        // Error in activation. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                        adb.setTitle("ACTIVATION ERROR");
                        adb.setMessage("Oops...\n\n"+errorMsg);
                        adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "JSON ERROR : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    setUserColor(1);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Activation Error: " + error.getMessage());
                Toast.makeText(getActivity(),"Activation Error: "+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("activation_code", acty_code);
                return params;
            }
        };
        VolleyInit.getInstance().addToRequestQueue(strReq, "activation_request");
    }



}
