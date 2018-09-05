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
import android.support.v7.widget.AppCompatButton;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.activities.MainActivity;
import in.ac.bkbiet.bkbiet.models.AuditRecord;
import in.ac.bkbiet.bkbiet.models.User;
import in.ac.bkbiet.bkbiet.utils.SQLiteHandler;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;
import in.ac.bkbiet.bkbiet.utils.VolleyInit;

/**
 * FragLogin Created by Ashish on 8/31/2017.
 */

public class FragLogin extends Fragment {
    ImageView iv_login;
    AppCompatButton btn_login;
    Button btn_guest_login;
    private TextInputEditText et_password, et_username;
    private ProgressBar progress;
    private TextInputLayout til_l_username, til_l_password;
    private String TAG = "FragLogin";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_login, container, false);

        initViews(rootView);

        return rootView;
    }

    private void initViews(View rootView) {
        iv_login = rootView.findViewById(R.id.iv_login);
        btn_login = rootView.findViewById(R.id.b_login);
        et_username = rootView.findViewById(R.id.et_l_username);
        et_password = rootView.findViewById(R.id.et_l_password);
        progress = rootView.findViewById(R.id.pb_login);
        til_l_username = rootView.findViewById(R.id.til_l_username);
        til_l_password = rootView.findViewById(R.id.til_l_password);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_login.setClickable(false);
                login();
            }
        });

        btn_guest_login = rootView.findViewById(R.id.b_guest_login);
        btn_guest_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Statics.loginAsGuest(getActivity());
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

    private void login() {
        til_l_username.setError("");
        til_l_password.setError("");
        setUserColor(0);

        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();

        if (username.isEmpty()) {
            til_l_username.setError("Username is required.");
        } else if (username.contains(".") || username.contains("/") || username.contains("?")) {
            til_l_username.setError("Username cannot contain . / ?");
        } else if (password.isEmpty()) {
            til_l_password.setError("Password is required.");
        } else if (password.length() < 8) {
            til_l_password.setError("Password must be atleast 8 characters long.");
        } else {
            progress.setVisibility(View.VISIBLE);

            StringRequest strReq = new StringRequest(Request.Method.POST, Uv.sURL_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Login Response : " + response);
                    progress.setVisibility(View.GONE);

                    SQLiteHandler db = null;
                    try {
                        db = new SQLiteHandler(getActivity());
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");

                        if (!error) {
                            if (jObj.getString("user_username") != null) {
                                boolean isActive = jObj.getInt("user_is_active") == 1;
                                boolean isBanned = jObj.getInt("user_is_banned") == 1;
                                User newUser = new User();
                                if (isActive && !isBanned) {
                                    newUser.setUsername(jObj.getString("user_username"));
                                    newUser.setName(jObj.getString("user_name"));
                                    newUser.setEmail(jObj.getString("user_email"));

                                    newUser.setCreatedAt(jObj.getString("user_created_at"));

                                    int user_type = jObj.getInt("user_type");
                                    switch (user_type) {
                                        case 4: // student
                                            newUser.setType(User.TYPE_STUDENT);
                                            break;
                                        case 2: // teacher
                                            newUser.setType(User.TYPE_TEACHER);
                                            break;
                                        case 1: // admin
                                            newUser.setType(User.TYPE_ADMIN);
                                            break;
                                        default: // others
                                            newUser.setType(User.TYPE_GUEST);
                                            break;
                                    }

                                    newUser.setMobileNo(jObj.getString("user_mobile_no"));

                                    newUser.setDpLink(jObj.getString("user_dp_link"));
                                    db.deleteUser();
                                    db.addUser(newUser);
                                    setUserColor(2);
                                    Uv.isLoggedIn = true;
                                    Uv.currUser = newUser;
                                    // Launch main activity
                                    Statics.saveNewAuditRecord(new AuditRecord("Login",
                                            "deviceId#:" + Statics.getDeviceId(getActivity())));

                                    Statics.refreshUserDeviceToken();
                                    //startActivity(new Intent(getActivity(), MainActivity.class));
                                    Statics.restartApp(getActivity());
                                    getActivity().finish();

                                } else {
                                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                                    final EditText tv_acty_code = new EditText(getActivity());
                                    String err = !isActive ? "Your account is currently inactive. " : "Your account has been " +
                                            "deactivated by the admin (not developer) " +
                                            "due to following reason : \n\n" + jObj.getString("user_ban_msg");

                                    adb.setTitle(!isActive ? "Inactive Account" : "Account Banned");
                                    adb.setMessage(err + (!isActive ? "\n" +
                                            "To activate your account enter activation code sent to you via email."
                                            : "\n Contact admin for more info."));

                                    adb.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    if (!isActive) {
                                        adb.setView(tv_acty_code);
                                        adb.setPositiveButton("Activate", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String acty_code = tv_acty_code.getText().toString();
                                                if (acty_code.isEmpty()) {
                                                    Toast.makeText(getActivity(), "Enter Activation Code", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    activateAccount(acty_code);
                                                    dialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                    adb.show();
                                    btn_login.setClickable(true);
                                }
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Error : No results found.", Toast.LENGTH_LONG).show();
                                btn_login.setClickable(true);
                            }
                        } else {
                            // Error in login. Get the error message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Error : " + errorMsg, Toast.LENGTH_LONG).show();
                            setUserColor(1);
                            btn_login.setClickable(true);
                        }
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getActivity().getApplicationContext(), "JSON ERROR", Toast.LENGTH_LONG).show();
                        setUserColor(1);
                        btn_login.setClickable(true);
                    } finally {
                        if (db != null) {
                            db.close();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Possible network error.");
                    if (error.networkResponse == null) {
                        return;
                    }
                    String body = "";
                    //get status code here
                    //final String statusCode = String.valueOf(error.networkResponse.statusCode);
                    //get response body and parse with appropriate encoding
                    try {
                        body = new String(error.networkResponse.data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        // exception
                    }

                    if (Uv.isDevOn)
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Possible Network error.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Login Error: " + error.getMessage() + body);
                    setUserColor(1);
                    progress.setVisibility(View.GONE);
                    btn_login.setClickable(true);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);
                    return params;
                }
            };
            VolleyInit.getInstance().addToRequestQueue(strReq, "login_request");
        }
    }

    private void activateAccount(final String acty_code) {
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
                        adb.setMessage("Oops...\n\n" + errorMsg);
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
                Toast.makeText(getActivity(), "Activation Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    void setUserColor(int color) {
        switch (color) {
            case 0:
                iv_login.setColorFilter(Color.rgb(239, 239, 239));
                break;
            case 1:
                iv_login.setColorFilter(Color.rgb(213, 0, 0));
                break;
            case 2:
                iv_login.setColorFilter(Color.rgb(0, 213, 0));
                break;
        }
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
}