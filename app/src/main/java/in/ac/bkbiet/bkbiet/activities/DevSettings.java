package in.ac.bkbiet.bkbiet.activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.utils.Sv;

import static in.ac.bkbiet.bkbiet.utils.Statics.restartApp;
import static in.ac.bkbiet.bkbiet.utils.Sv.dAD_TYPE_TO_SHOW;
import static in.ac.bkbiet.bkbiet.utils.Sv.dDOWNLOAD_IMAGES_ON_LONG_PRESS;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSAVE_AUDITS;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSEND_RECEIPTS;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSHOW_AUDITS;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSHOW_DEV_SETTINGS;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSHOW_IMAGE_DOWNLOAD_BUTTON;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSHOW_RECEIPTS;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSHOW_STUDENT_ACCESS;
import static in.ac.bkbiet.bkbiet.utils.Sv.dSHOW_TEACHER_ACCESS;
import static in.ac.bkbiet.bkbiet.utils.Sv.getBooleanSetting;
import static in.ac.bkbiet.bkbiet.utils.Sv.getIntSetting;
import static in.ac.bkbiet.bkbiet.utils.Sv.setBooleanSetting;
import static in.ac.bkbiet.bkbiet.utils.Sv.setIntSetting;

/**
 * DevSettings Created by Ashish on 12/16/2017.
 */

public class DevSettings extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_settings);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initShowInNav();
        initChats();
        initImages();
        initAudits();
        initAds();
    }

    private void initShowInNav() {
        CheckBox studentAccess = findViewById(R.id.cb_ds_studentAccess);
        CheckBox teacherAccess = findViewById(R.id.cb_ds_teacherAccess);
        CheckBox audits = findViewById(R.id.cb_ds_showNavAudits);
        CheckBox devSettings = findViewById(R.id.cb_ds_showNavDevSettings);

        studentAccess.setChecked(getBooleanSetting(Sv.dSHOW_STUDENT_ACCESS, false));
        teacherAccess.setChecked(getBooleanSetting(Sv.dSHOW_TEACHER_ACCESS, false));
        audits.setChecked(getBooleanSetting(Sv.dSHOW_AUDITS, false));
        devSettings.setChecked(getBooleanSetting(Sv.dSHOW_DEV_SETTINGS, true));

        studentAccess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBooleanSetting(DevSettings.this, dSHOW_STUDENT_ACCESS, isChecked);
            }
        });
        teacherAccess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBooleanSetting(DevSettings.this, dSHOW_TEACHER_ACCESS, isChecked);
            }
        });

        audits.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBooleanSetting(DevSettings.this, dSHOW_AUDITS, isChecked);
            }
        });

        devSettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBooleanSetting(DevSettings.this, dSHOW_DEV_SETTINGS, isChecked);
            }
        });
    }

    private void initChats() {
        CheckBox sendReceipts = findViewById(R.id.cb_ds_sendReceipts);
        CheckBox showReceipts = findViewById(R.id.cb_ds_showReceipts);

        sendReceipts.setChecked(getBooleanSetting(Sv.dSEND_RECEIPTS, true));
        showReceipts.setChecked(getBooleanSetting(Sv.dSHOW_RECEIPTS, false));

        sendReceipts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBooleanSetting(DevSettings.this, dSEND_RECEIPTS, isChecked);
            }
        });

        showReceipts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBooleanSetting(DevSettings.this, dSHOW_RECEIPTS, isChecked);
            }
        });
    }

    private void initImages() {
        CheckBox showImgDownBtn = findViewById(R.id.cb_ds_showImgDownBtn);
        CheckBox downImgOnLong = findViewById(R.id.cb_ds_downImgOnLong);

        showImgDownBtn.setChecked(getBooleanSetting(Sv.dSHOW_IMAGE_DOWNLOAD_BUTTON, false));
        downImgOnLong.setChecked(getBooleanSetting(Sv.dDOWNLOAD_IMAGES_ON_LONG_PRESS, false));

        showImgDownBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBooleanSetting(DevSettings.this, dSHOW_IMAGE_DOWNLOAD_BUTTON, isChecked);
            }
        });

        downImgOnLong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBooleanSetting(DevSettings.this, dDOWNLOAD_IMAGES_ON_LONG_PRESS, isChecked);
            }
        });
    }

    private void initAudits() {
        CheckBox saveAudits = findViewById(R.id.cb_ds_saveAudits);
        saveAudits.setChecked(getBooleanSetting(Sv.dSAVE_AUDITS, true));
        saveAudits.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBooleanSetting(DevSettings.this, dSAVE_AUDITS, isChecked);
            }
        });
    }

    private void initAds() {
        RadioGroup adType = findViewById(R.id.rg_ds_adType);
        RadioButton adTypeAds = findViewById(R.id.rb_ds_adTypeAds);
        RadioButton adTypeDevAds = findViewById(R.id.rb_ds_adTypeDevAds);
        RadioButton adTypeNoAds = findViewById(R.id.rb_ds_adTypeNoAds);

        switch (getIntSetting(dAD_TYPE_TO_SHOW, 1)) {
            case 1:
                adTypeAds.setChecked(true);
                break;
            case 2:
                adTypeDevAds.setChecked(true);
                break;
            case 3:
                adTypeNoAds.setChecked(true);
                break;
            default:
                adTypeAds.setChecked(true);
                break;
        }

        adType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_ds_adTypeAds: // ads
                        setIntSetting(DevSettings.this, Sv.dAD_TYPE_TO_SHOW, 1);
                        break;
                    case R.id.rb_ds_adTypeDevAds: // development ads
                        setIntSetting(DevSettings.this, Sv.dAD_TYPE_TO_SHOW, 2);
                        break;
                    case R.id.rb_ds_adTypeNoAds: // no ads
                        setIntSetting(DevSettings.this, Sv.dAD_TYPE_TO_SHOW, 3);
                        break;
                    default: // ads
                        setIntSetting(DevSettings.this, Sv.dAD_TYPE_TO_SHOW, 1);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart_main_activity:
                restartApp(DevSettings.this);
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}