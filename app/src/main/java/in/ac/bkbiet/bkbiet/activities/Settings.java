package in.ac.bkbiet.bkbiet.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.unofficial.utils.SyllabusCache;
import in.ac.bkbiet.bkbiet.utils.ImageLoading.ImageLoader;
import in.ac.bkbiet.bkbiet.utils.Sv;

import static in.ac.bkbiet.bkbiet.utils.Statics.restartApp;
import static in.ac.bkbiet.bkbiet.utils.Sv.getBooleanSetting;
import static in.ac.bkbiet.bkbiet.utils.Sv.setBooleanSetting;

/**
 * Settings Created by Ashish on 12/16/2017.
 */

public class Settings extends AppCompatActivity {
    LinearLayout clearSyllabusCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initImages();
        initUnofficial();
    }

    private void initImages() {
        CheckBox loadImages = findViewById(R.id.cb_ss_loadImg);
        final LinearLayout clearImgCache = findViewById(R.id.ll_ss_clearImgCache);

        loadImages.setChecked(getBooleanSetting(Sv.sLOAD_IMAGES, true));
        loadImages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setBooleanSetting(Settings.this, Sv.sLOAD_IMAGES, isChecked);
            }
        });

        clearImgCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader imageLoader = new ImageLoader(Settings.this);
                imageLoader.clearCache();
                Snackbar.make(clearImgCache, "Image Cache Cleared.", BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }

    private void initUnofficial() {
        clearSyllabusCache = findViewById(R.id.ll_ss_clearSyllabusCache);
        clearSyllabusCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyllabusCache.getInstance().clearCache(Settings.this);
                Snackbar.make(clearSyllabusCache, "Syllabus Cache Cleared.", BaseTransientBottomBar.LENGTH_SHORT).show();
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
                restartApp(Settings.this);
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}