package in.ac.bkbiet.bkbiet.unofficial;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.utils.Sv;

import static in.ac.bkbiet.bkbiet.utils.Sv.getIntSetting;
import static in.ac.bkbiet.bkbiet.utils.Sv.setIntSetting;

/**
 * ActivityRewards Created by Ashish on 12/18/2017.
 */

public class ActPerksAndBerries extends AppCompatActivity implements RewardedVideoAdListener {
    TextView berryCount;
    Button watchAd;
    RewardedVideoAd mRewardedVideoAd;
    int berries = 0;
    int pendingBerries = 0;
    boolean loadingAdFailed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perks_and_berries);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        berryCount = findViewById(R.id.tv_ar_berryCount);
        berries = getIntSetting(Sv.dBERRIES_COUNT, 0);
        String berry = berries + " ฿";
        berryCount.setText(berry);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(ActPerksAndBerries.this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        watchAd = findViewById(R.id.b_apb_watchAd);
        watchAd.setEnabled(false);
        if (getIntSetting(Sv.dAD_TYPE_TO_SHOW, 1) == 3) {
            watchAd.setText("Get 100 Berries");
        }

        watchAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntSetting(Sv.dAD_TYPE_TO_SHOW, 1) == 3) {
                    earnedBerries(100);
                } else {
                    if (mRewardedVideoAd.isLoaded()) {
                        mRewardedVideoAd.show();
                    } else if (loadingAdFailed) {
                        loadRewardedVideoAd();
                    }
                }
            }
        });

        loadRewardedVideoAd();
        initSaveSyllabus();
    }

    private void loadRewardedVideoAd() {
        loadingAdFailed = false;
        watchAd.setText("Loading Video Ad...");
        switch (getIntSetting(Sv.dAD_TYPE_TO_SHOW, 1)) {
            case 1:
                mRewardedVideoAd.loadAd("ca-app-pub-9780748488703696/2116376197", new AdRequest.Builder().build());
                break;
            case 2:
                mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
                break;
            case 3:
                watchAd.setEnabled(true);
                break;
            default:
                mRewardedVideoAd.loadAd("ca-app-pub-9780748488703696/2116376197", new AdRequest.Builder().build());
        }
    }

    private void earnedBerries(int count) {
        pendingBerries = 0;
        setIntSetting(ActPerksAndBerries.this, Sv.dBERRIES_COUNT, getIntSetting(Sv.dBERRIES_COUNT, 0) + count);
        berries = getIntSetting(Sv.dBERRIES_COUNT, count);
        String berry = berries + " ฿";
        Snackbar.make(berryCount, "You have earned " + count + " berries.", Snackbar.LENGTH_LONG).show();
        berryCount.setText(berry);
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        watchAd.setText("Earn Berries (Watch Ad)");
        watchAd.setEnabled(true);
    }

    @Override
    public void onRewardedVideoAdOpened() {
        watchAd.setText("Opening Ad");
        watchAd.setEnabled(false);
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        if (pendingBerries > 0) {
            earnedBerries(pendingBerries);
        } else {
            Snackbar.make(watchAd, "Ad closed in between. No earnings.", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        pendingBerries += rewardItem.getAmount();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        pendingBerries += 5;
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        loadingAdFailed = true;
        watchAd.setText("Failed to load Ad");
        Snackbar.make(watchAd, "Failed to load ad. Try again later.", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

    private void initSaveSyllabus() {
        if (Sv.getBooleanSetting(Sv.pIS_SAVING_SYLLABUSES, false)) {
            findViewById(R.id.cb_apb_saveSyllabuses).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_apb_saveSyllabusCost).setVisibility(View.GONE);
        } else {
            final CardView cardSaveSyllabus = findViewById(R.id.card_apb_saveSyllabuses);
            cardSaveSyllabus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Sv.getIntSetting(Sv.dBERRIES_COUNT, 0) > 20) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(ActPerksAndBerries.this);
                        adb.setTitle("Activate Perk?");
                        adb.setMessage("Are you sure you want to activate perk 'Save Syllabuses' for 20 berries?");
                        adb.setPositiveButton("Activate", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Sv.setBooleanSetting(ActPerksAndBerries.this, Sv.pIS_SAVING_SYLLABUSES, true);
                                Sv.setIntSetting(ActPerksAndBerries.this, Sv.dBERRIES_COUNT, Sv.getIntSetting(Sv.dBERRIES_COUNT, 0) - 20);
                                Sv.refresh(ActPerksAndBerries.this);
                                initSaveSyllabus();
                                Snackbar.make(cardSaveSyllabus, "Activated perk 'Save Syllabuses'", BaseTransientBottomBar.LENGTH_SHORT).show();
                            }
                        });

                        adb.setNegativeButton("Cancel", null);
                        adb.show();
                    } else {
                        Snackbar.make(cardSaveSyllabus, "Not enough berries.", BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}