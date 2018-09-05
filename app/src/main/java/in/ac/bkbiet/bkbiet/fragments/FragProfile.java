package in.ac.bkbiet.bkbiet.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import in.ac.bkbiet.bkbiet.activities.LoginSignUp;
import in.ac.bkbiet.bkbiet.models.AuditRecord;
import in.ac.bkbiet.bkbiet.models.User;
import in.ac.bkbiet.bkbiet.utils.ImageLoading.ImageLoader;
import in.ac.bkbiet.bkbiet.utils.SQLiteHandler;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;
import in.ac.bkbiet.bkbiet.utils.VolleyInit;

/**
 * Created by Ashish Mahla on 9/3/2017.
 * Profile fragment is meant to show user information.
 */

public class FragProfile extends Fragment {
    final String TAG = "FragProfile";
    TextView username, email, type, name, password, mobile_no;
    Button btn_logout;
    ImageView iv_dp;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile, container, false);
        initViews(view);

        return view;
    }

    private void initViews(final View view) {
        iv_dp = view.findViewById(R.id.iv_profile_pic);
        iv_dp.setImageBitmap(ImageLoader.getBitmapFromString(ImageLoader.getInitialsFormString(Uv.currUser.getName()), 200, Color.argb(255, 200, 0, 0)));
        ImageLoader imageLoader = new ImageLoader(getActivity());
        imageLoader.displayImage(Uv.currUser.getDpLink(), iv_dp, Uv.currUser.getName(), ImageLoader.QUALITY_MEDIUM);

        name = view.findViewById(R.id.tv_p_name);
        type = view.findViewById(R.id.tv_p_type);
        username = view.findViewById(R.id.tv_p_username);
        email = view.findViewById(R.id.tv_p_email);
        password = view.findViewById(R.id.tv_p_password);
        mobile_no = view.findViewById(R.id.tv_p_mobile_no);

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

        //setting details
        name.setText(Uv.currUser.getName());
        username.setText(Uv.currUser.getUsername());
        email.setText(Uv.currUser.getEmail());
        mobile_no.setText(Uv.currUser.getMobileNo());
        switch (Uv.currUser.getType()) {
            case User.TYPE_STUDENT:
                type.setText("(Student)");
                break;
            case User.TYPE_TEACHER:
                type.setText("(Teacher)");
                break;
            case User.TYPE_ADMIN:
                type.setText("(Admin)");
                break;
            case User.TYPE_GUEST:
                type.setText("(Guest User)");
                break;
            default:
                type.setText("(Type Unknown)");
        }

        btn_logout = view.findViewById(R.id.btn_logout);
        if (Uv.currUser.getType() != User.TYPE_GUEST) {
            btn_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                    adb.setTitle("Really Logout");
                    adb.setMessage("It will delete all your details and notifications from this device." +
                            "\n\nNote: You will still receive notifications until the app is uninstalled.");
                    adb.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            logout();
                        }
                    });
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    adb.show();
                }
            });
        } else {
            btn_logout.setText("Login/Register");
            btn_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    login();
                }
            });
        }

        iv_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statics.viewFullScreenImage(getActivity(), Uv.currUser);
            }
        });
        getWritePermission();
    }

    private void login() {
        startActivity(new Intent(getActivity(), LoginSignUp.class));
    }

    private void logout() {
        // TODO: 12/21/2017 what the fuck am i gonna do with this shit
        Statics.saveNewAuditRecord(new AuditRecord("LoggedOut",
                "deviceId#:" + Statics.getDeviceId(getActivity())));
        Statics.removeAllSubscriptions();
        SQLiteHandler db = new SQLiteHandler(getActivity().getApplicationContext());
        db.deleteUser();
        //db.deleteStudent();
        db.resetNotyTable();
        db.close();

        Uv.isLoggedIn = false;
        Uv.currUser = null;
        //Uv.currentStudent = null;
        Toast.makeText(getActivity().getApplicationContext(), "You have been successfully logged out.", Toast.LENGTH_SHORT).show();
        Statics.loginAsGuest(getActivity());
        Statics.restartApp(getActivity());
    }

    private void changePassword() {
        if (Uv.currUser.getType() != User.TYPE_GUEST) {
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            llp.setMargins(60, 20, 60, 0);

            final TextInputLayout oldPass = new TextInputLayout(getActivity());
            final TextInputEditText ti_oldPass = new TextInputEditText(getActivity());
            oldPass.addView(ti_oldPass);
            ti_oldPass.setHint("Old Password");
            ti_oldPass.setMaxLines(1);
            ti_oldPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            oldPass.setLayoutParams(llp);

            final TextInputLayout newPass = new TextInputLayout(getActivity());
            final TextInputEditText ti_newPass = new TextInputEditText(getActivity());
            newPass.addView(ti_newPass);
            ti_newPass.setHint("New Password");
            ti_newPass.setMaxLines(1);
            ti_newPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            newPass.setLayoutParams(llp);

            final TextInputLayout confirmPass = new TextInputLayout(getActivity());
            final TextInputEditText ti_confirmPass = new TextInputEditText(getActivity());
            confirmPass.addView(ti_confirmPass);
            ti_confirmPass.setHint("Confirm Password");
            ti_confirmPass.setMaxLines(1);
            ti_confirmPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            confirmPass.setLayoutParams(llp);

            LinearLayout ll1 = new LinearLayout(getActivity());
            ll1.addView(oldPass);
            ll1.addView(newPass);
            ll1.addView(confirmPass);
            ll1.setOrientation(LinearLayout.VERTICAL);

            adb.setView(ll1);

            adb.setTitle("Change Password");
            adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            adb.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            adb.setCancelable(false);
            final AlertDialog dialog = adb.create();
            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String old_password = ti_oldPass.getText().toString();
                    final String new_password = ti_newPass.getText().toString();
                    final String confirm_password = ti_confirmPass.getText().toString();
                    newPass.setError("");
                    confirmPass.setError("");
                    if (new_password.length() < 8) {
                        newPass.setError("Password must be atleast 8 characters long.");
                    } else if (!new_password.equals(confirm_password)) {
                        confirmPass.setError("Passwords do not match.");
                    } else {
                        //----------------------------------------
                        StringRequest strReq = new StringRequest(Request.Method.POST, Uv.sURL_LOGIN, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "Password Update Response : " + response);
                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    boolean error = jObj.getBoolean("error");

                                    if (!error) {
                                        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                                        adb.setTitle("Password Successfully Changed");
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
                                        adb.setTitle("ERROR");
                                        adb.setMessage(errorMsg);
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
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "Password Change Error: " + error.getMessage());
                                Toast.makeText(getActivity(), "Password Change Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("username", Uv.currUser.getUsername());
                                params.put("old_password", old_password);
                                params.put("new_password", new_password);
                                return params;
                            }
                        };
                        VolleyInit.getInstance().addToRequestQueue(strReq, "password_change_request");
                        //----------------------------------------
                        dialog.dismiss();
                    }
                }
            });
        } else {
            Snackbar.make(password, "Guest Accounts have no passwords to change.", BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    private void getWritePermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Write Permission Required");
                adb.setCancelable(false);
                adb.setMessage("The 'Write External Storage' is required to create and use image cache to minimize data usage " +
                        "while downloading profile images.");
                adb.setPositiveButton("Grant Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                7);
                    }
                });

                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                adb.show();
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        7);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
}
