package in.ac.bkbiet.bkbiet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import in.ac.bkbiet.bkbiet.activities.MainActivity;
import in.ac.bkbiet.bkbiet.models.AuditRecord;
import in.ac.bkbiet.bkbiet.models.Token;
import in.ac.bkbiet.bkbiet.models.User;
import in.ac.bkbiet.bkbiet.utils.ImageLoading.ImageLoader;

import static in.ac.bkbiet.bkbiet.utils.Sv.getBooleanSetting;

/**
 * Statics Created by Ashish on 12/3/2017.
 */

@SuppressWarnings({"unused", "SameParameterValue", "WeakerAccess"})
public class Statics {
    private final static String TAG = "Statics";

    public static boolean isCollegeIdValid(String cId) {
        cId = cId.toLowerCase();
        String regexp = "[0-9][0-9]ebk(cs|it|ec|ee|ex|me)[0-9][0-9][0-9]";
        return !cId.matches(regexp) || (Integer.parseInt(cId.substring(0, 2)) > (Calendar.getInstance().get(Calendar.YEAR) - 2000) ||
                Integer.parseInt(cId.substring(0, 2)) <= (Calendar.getInstance().get(Calendar.YEAR) - 2005));
    }

    public static String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return sdf.format(new Date());
    }

    public static String getDeviceId(Context context) {
        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceId == null) {
            deviceId = "NodId-" + generateRandomString(4);
        }
        return deviceId;
    }

    public static String generateRandomString(int length) {
        String seed = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
        StringBuilder builder = new StringBuilder();
        while (length-- != 0) {
            int character = (int) (Math.random() * seed.length());
            builder.append(seed.charAt(character));
        }

        builder.append(Statics.getTimeStamp("ddMMyy"));
        return builder.toString();
    }

    public static String getTimeStamp(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return sdf.format(new Date());
    }

    public static void openMailIntent(Context context, String[] email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.setType("text/plain");
        final PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        context.startActivity(emailIntent);
    }

    public static void openDialerIntent(Context context, String mobile_no) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobile_no));
        context.startActivity(intent);
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void saveNewAuditRecord(final AuditRecord auditRecord) {
        if (getBooleanSetting(Sv.dSAVE_AUDITS, true)) {
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference isSavingRecordsRef = dbRef.child("reports").child("0_is_saving_audits");
            isSavingRecordsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        @SuppressWarnings("ConstantConditions") boolean isSaving = dataSnapshot.getValue(Boolean.class);
                        if (isSaving) {
                            DatabaseReference auditRecordsRef = dbRef.child("reports").child(Uv.currUser.getUsername()).child(getTimeStamp());
                            auditRecordsRef.setValue(auditRecord);
                        }
                    } catch (NullPointerException ignored) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i(TAG, "Unable to save audits");
                }
            });
        }
    }

    public static void viewFullScreenImage(Context context, final User targetUser) {
        AuditRecord auditRecord = new AuditRecord("ViewFullScreenImage", "target_username#:" + targetUser.getUsername() + "#;" +
                "target_name#:" + targetUser.getName() + "#;" + "target_dpLink#:" + targetUser.getDpLink());
        Statics.saveNewAuditRecord(auditRecord);

        AlertDialog.Builder adb = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
        final ImageView imageView = new ImageView(context);
        adb.setView(imageView);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(llp);
        imageView.setAdjustViewBounds(true);
        final ImageLoader imageLoader = new ImageLoader(context);
        imageLoader.displayImage(targetUser.getDpLink(), imageView, targetUser.getName(), ImageLoader.QUALITY_MAX);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (Sv.getBooleanSetting(Sv.dSHOW_IMAGE_DOWNLOAD_BUTTON, false)) {
            adb.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AuditRecord auditRecord = new AuditRecord("DownloadImage", "target_username#:" + targetUser.getUsername() + "#;" +
                            "target_name#:" + targetUser.getName() + "#;" + "target_dpLink#:" + targetUser.getDpLink());
                    Statics.saveNewAuditRecord(auditRecord);
                    imageLoader.saveImage(imageView.getContext(), targetUser.getDpLink(), targetUser.getName() + "--" + getTimeStamp(), "BKB_PICS", ImageLoader.QUALITY_MAX);
                }
            });
        }
        adb.show();
    }

    public static void restartApp(Context c) {
        Intent intent = new Intent(c, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        c.startActivity(intent);
    }

    public static String getBranchCode() {
        if (Uv.currUser != null && Uv.currUser.getType() == User.TYPE_STUDENT)
            try {
                return Uv.currUser.getUsername().substring(6, 8);
            } catch (Exception ignored) {
                return null;
            }
        return null;
    }

    public static int getSemester() {
        if (Uv.currUser != null && Uv.currUser.getType() == User.TYPE_STUDENT) {
            try {
                int year = Integer.parseInt(Uv.currUser.getUsername().substring(0, 2));
                int sem = 2;
                SimpleDateFormat sdf = new SimpleDateFormat("yy", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                year = Integer.parseInt(sdf.format(new Date())) - year;

                sdf = new SimpleDateFormat("mm", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                if (Integer.parseInt(sdf.format(new Date())) > 6) {
                    year++;
                    sem = 1;
                }
                sem *= year;
                return sem;
            } catch (Exception ignored) {
                return -1;
            }
        }
        return -1;
    }

    public static int getYear() {
        if (Uv.currUser != null && Uv.currUser.getType() == User.TYPE_STUDENT) {
            try {
                int year = Integer.parseInt(Uv.currUser.getUsername().substring(0, 2));

                SimpleDateFormat sdf = new SimpleDateFormat("yy", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                year = Integer.parseInt(sdf.format(new Date())) - year;

                sdf = new SimpleDateFormat("mm", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                if (Integer.parseInt(sdf.format(new Date())) > 6) {
                    year++;
                }
                return year;
            } catch (Exception ignored) {
                return -1;
            }
        }
        return -1;
    }

    public static void logDebug(String message) {
        Log.e("debug log", message);
    }

    public static void logInfo(String message) {
        Log.i("info log", message);
    }

    static public void refreshUserDeviceToken() {
        if (Uv.currUser.getUsername() != null) {
            DatabaseReference tokensRef = FirebaseDatabase.getInstance().getReference().child("user_tokens");
            tokensRef.child(Uv.currUser.getUsername())
                    .setValue(Token.getInstance().refresh());

            subscribeToTopics();
        }
    }

    private static void subscribeToTopics() {
        if (Uv.currUser.getType() == User.TYPE_STUDENT) {
            String branch = getBranchCode();
            if (branch != null) {
                FirebaseMessaging.getInstance().subscribeToTopic(branch);
            }
            try {
                FirebaseMessaging.getInstance().subscribeToTopic(Uv.currUser.getUsername().substring(0, 2));
            } catch (Exception ignored) {
            }
        }
    }

    public static void removeAllSubscriptions() {
        if (Uv.currUser.getType() == User.TYPE_STUDENT) {
            String branch = getBranchCode();
            if (branch != null) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(branch);
            }
            try {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Uv.currUser.getUsername().substring(0, 2));
            } catch (Exception ignored) {
            }
        }
    }

    public static void loginAsGuest(Context context) {
        User newUser = User.getDefaultUser(context);
        SQLiteHandler db = new SQLiteHandler(context);
        db.addUser(newUser);
        Uv.isLoggedIn = true;
        Uv.currUser = newUser;
        Statics.saveNewAuditRecord(new AuditRecord("Login",
                "deviceId#:" + Statics.getDeviceId(context)));
    }

    public static void sendNotyCountBroadcast(Context context, int unreadCount) {
        Intent intent = new Intent(Uv.ACTION_NOTY_COUNT_BROADCAST);
        intent.putExtra("unread_count", unreadCount);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}