package in.ac.bkbiet.bkbiet.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

import in.ac.bkbiet.bkbiet.BuildConfig;
import in.ac.bkbiet.bkbiet.models.User;
import in.ac.bkbiet.bkbiet.utils.SQLiteHandler;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Sv;
import in.ac.bkbiet.bkbiet.utils.Uv;

import static in.ac.bkbiet.bkbiet.utils.Statics.refreshUserDeviceToken;
import static in.ac.bkbiet.bkbiet.utils.Uv.isDevOn;

/**
 * Splash Created by Ashish on 9/4/2017.
 */

public class Splash extends AppCompatActivity {
    FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSettings();
        setIsDevOn();

        if (isUpdateRequired()) {
            showUpdateReq();
        } else if (!checkLoggedIn()) {
            Statics.loginAsGuest(Splash.this);
        }

        refreshUserDeviceToken();

        if (Sv.getBooleanSetting(Sv.dIS_FIRST_LAUNCH, true)) {
            launchActivity(Intro.class);
        } else {
            /*Uv.currUser = new User("strawhat", "Monkey D. Luffy", "luffy@strawhat.com", "", User.TYPE_ADMIN, "+00 1423668878",
            "http://awswallpapershd.com/wp-content/uploads/2016/10/Monkey-D-Luffy-One-Piece-Free-Wallpaper-Background.jpg");*/
            launchActivity(MainActivity.class);
        }
        finish();
    }

    private boolean checkLoggedIn() {
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        User currUser = db.getCurrentUser();
        db.close();

        if (currUser.getId() >= 0) {
            Uv.isLoggedIn = true;
            Uv.currUser = currUser;
        } else {
            Uv.isLoggedIn = false;
        }
        return Uv.isLoggedIn;
    }

    private boolean isUpdateRequired() {
        remoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(isDevOn).build());
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("mandatory_version_name", "1.0");
        defaults.put("mandatory_version_code", 1);
        defaults.put("latest_version_name", "1.0");
        defaults.put("latest_version_code", 1);
        defaults.put("update_message", "A newer version of this app is available. Goto 'About' section to update.");
        defaults.put("update_link", "no-link-available");

        remoteConfig.setDefaults(defaults);
        final Task<Void> fetch = remoteConfig.fetch(BuildConfig.DEBUG ? 0 : 24);
        fetch.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                remoteConfig.activateFetched();
                remoteConfig.getString("mandatory_version_code");
                remoteConfig.getString("mandatory_version_name");
                remoteConfig.getString("latest_version_code");
                remoteConfig.getString("latest_version_name");
                remoteConfig.getString("update_message");
                remoteConfig.getString("update_link");
            }
        });

        int mandatoryVersionCode = Integer.parseInt(remoteConfig.getString("mandatory_version_code"));
        String mandatoryVersionName = remoteConfig.getString("mandatory_version_name");
        int latestVersionCode = Integer.parseInt(remoteConfig.getString("latest_version_code"));
        String latestVersionName = remoteConfig.getString("latest_version_name");
        String updateMessage = remoteConfig.getString("update_message");
        String updateLink = remoteConfig.getString("update_link");

        int versionCode = 1;
        String versionName = "1.0";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (versionCode < latestVersionCode && updateMessage.length() > 0) {
            Uv.isNewVersionAvailable = true;
        }

        if (!updateLink.equals("no-link-available")) {
            Uv.isUpdateLinkAvailable = true;
            Uv.updateLink = updateLink;
        }
        Uv.updateMsg = updateMessage;
        return (versionCode < mandatoryVersionCode);
    }

    private void showUpdateReq() {
        AlertDialog.Builder updateReq = new AlertDialog.Builder(Splash.this);
        if (Uv.updateMsg.contains("Notice")) {
            updateReq.setTitle("Notice");
            updateReq.setMessage(Uv.updateMsg);
        } else {
            updateReq.setTitle("Update Required");
            updateReq.setMessage(Uv.updateMsg);
            if (Uv.isUpdateLinkAvailable) {
                updateReq.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String url = Uv.updateLink;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                });
            }
        }

        updateReq.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        updateReq.show();
    }

    private void launchActivity(final Class<? extends Activity> activity) {
        startActivity(new Intent(Splash.this, activity));
        finish();
    }

    private void setSettings() {
        Sv.refresh(Splash.this);
    }

    private void setIsDevOn() {
        Sv.setBooleanSetting(Splash.this, Sv.dIS_DEV_ON, Uv.isDevOn);
    }
}

