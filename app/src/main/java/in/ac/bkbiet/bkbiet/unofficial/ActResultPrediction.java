package in.ac.bkbiet.bkbiet.unofficial;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.utils.Sv;

import static in.ac.bkbiet.bkbiet.utils.Sv.getIntSetting;
import static in.ac.bkbiet.bkbiet.utils.Sv.setIntSetting;

/**
 * ResultPrediction Created by Ashish on 12/19/2017.
 */

public class ActResultPrediction extends AppCompatActivity {
    RelativeLayout screenPay;
    ScrollView screenEntries;
    LinearLayout screenProcessing, screenResult;
    ProgressBar processBar;
    TextView processStatus;
    TextView result;
    Button close;
    Button predict;
    Handler mHandler = new Handler();
    int counter = 0;
    float fCounter = 0;
    boolean launch = false;
    boolean hover = false;
    boolean isGoingUp = false;
    boolean initializing = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_prediction);

        screenPay = findViewById(R.id.screen_arl_pay);
        screenEntries = findViewById(R.id.screen_arl_entries);
        screenProcessing = findViewById(R.id.screen_arl_processing);
        screenResult = findViewById(R.id.screen_arl_result);

        initScreenPay();
        initEntries();
        initProcessing();
        initResult();
    }

    private void initScreenPay() {
        final TextView predictCost, currBal, getBerries;
        Button payAndCont;

        predictCost = findViewById(R.id.tv_ar_predictCost);
        currBal = findViewById(R.id.tv_arp_currBal);
        getBerries = findViewById(R.id.tv_b_arp_getBerries);
        payAndCont = findViewById(R.id.b_arp_payAndCont);

        final int iPredictCost = 6;
        String sPredictCost = "-" + iPredictCost + " ฿";
        predictCost.setText(sPredictCost);

        final int iCurrBal = getIntSetting(Sv.dBERRIES_COUNT, 0);
        String sCurrBal = "Balance: " + iCurrBal + " ฿";
        currBal.setText(sCurrBal);

        getBerries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActResultPrediction.this, ActPerksAndBerries.class));
                finish();
            }
        });

        if (iCurrBal < iPredictCost) {
            payAndCont.setText("Not enough berries");
            payAndCont.setEnabled(false);
        }

        payAndCont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iCurrBal >= iPredictCost) {
                    setIntSetting(ActResultPrediction.this, Sv.dBERRIES_COUNT, getIntSetting(Sv.dBERRIES_COUNT, 0) - iPredictCost);
                    balancePaid();
                }
            }
        });
    }

    private void initEntries() {
        predict = findViewById(R.id.b_arp_predict);

        final TextInputEditText[] sems = new TextInputEditText[7];
        sems[0] = findViewById(R.id.arp_sem1);
        sems[1] = findViewById(R.id.arp_sem2);
        sems[2] = findViewById(R.id.arp_sem3);
        sems[3] = findViewById(R.id.arp_sem4);
        sems[4] = findViewById(R.id.arp_sem5);
        sems[5] = findViewById(R.id.arp_sem6);
        sems[6] = findViewById(R.id.arp_sem7);

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] semsRes = new float[7];
                boolean error = false;
                String errorMsg = "";
                for (int i = 0; i < 7 && !error; i++) {
                    String value = sems[i].getText().toString();
                    if (value.isEmpty()) {
                        error = true;
                        errorMsg = "Enter percentage of semester " + (i + 1);
                        break;
                    } else {
                        try {
                            semsRes[i] = Float.parseFloat(value);
                            if (semsRes[i] < 0 || semsRes[i] > 100) {
                                error = true;
                                errorMsg = "Value out of bounds(0-100) at semester " + (i + 1);
                            }
                        } catch (Exception ignored) {
                            error = true;
                            errorMsg = "Invalid value of semester " + (i + 1);
                            break;
                        }
                    }
                }

                if (error) {
                    Snackbar.make(v, errorMsg, BaseTransientBottomBar.LENGTH_LONG).show();
                } else {
                    runPredictionAlgo(semsRes);
                }
            }
        });
    }

    private void initProcessing() {
        processBar = findViewById(R.id.pb_arp_processing);
        processStatus = findViewById(R.id.tv_arp_processing_status);
    }

    private void initResult() {
        result = findViewById(R.id.tv_arp_result);
        close = findViewById(R.id.b_arl_close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void balancePaid() {
        //screenEntries.animate().scaleX(2).scaleY(2).setDuration(500);
        screenEntries.setVisibility(View.VISIBLE);
        screenPay.animate().translationX(-2000).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                screenPay.setVisibility(View.GONE);
                //screenEntries.animate().rotationY(0).setDuration(1000);
            }
        });
    }

    private void runPredictionAlgo(float[] semsResult) {
        screenEntries.animate().scaleX(.2f).scaleY(.2f).setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                screenEntries.setVisibility(View.GONE);
                screenProcessing.setVisibility(View.VISIBLE);
            }
        });

        float[] matrix = new float[]{
                23.0575f,
                .0314f,
                .0334f,
                .0211f,
                .0476f,
                .0763f,
                .1875f,
                .3680f,
                .1808f
        };

        float sum = 0;
        for (float f : semsResult) {
            sum += f;
        }

        float mean = sum / 7;
        sum = 0;

        for (float f : semsResult) {
            sum += (f - mean) * (f - mean);
        }
        final float standardDeviation = (float) Math.pow(sum / 7, 0.5f);

        sum = matrix[0];
        for (int i = 1; i < 8; i++) {
            sum += matrix[i] * (semsResult[i - 1]);
        }
        sum += standardDeviation * matrix[8];

        final float fResult = sum;

        //run script
        final TextView processStatus = this.processStatus;
        final String[] statusTexts = new String[]{
                "Calculating Probability",
                "Uploading data sets", "Creating Matrix", "Creating space time distortion",
                "Calculating probability of space time continuum disintegration", "Minimizing space distortion complexity",

                "Killing two birds with one stone", "Have you met Ted?", "Ka-me-ha-me-ha", "Mind your sugar levels",
                "Mera juta hai japani", "Only one truth prevails", "Oh you wanna see whats flashing on this screen?",
                "Well keep trying until you do.", "But wait! That means more Berries.", "Nami will be happy.",
                "Is enough time wasted already?", "Okay, back to seriousness",

                "Connecting to outer world magnetic array time separation field",
                "Fetching output from Interstellar time scope magnifier",
                "Decrypting data cubes", "Minimizing output potential hazard", "Creating anti twin paradox Billiartz",
                "Finalizing output arrays to value"
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                {
                    if (counter < statusTexts.length) {
                        processStatus.setText(statusTexts[counter++]);
                        processBar.setProgress(counter);
                        processBar.setMax(statusTexts.length);

                        if (initializing) {
                            mHandler.postDelayed(this, 600);
                            initializing = false;
                        } else {
                            mHandler.postDelayed(this, 70);
                        }
                    } else {
                        if (launch) {
                            resultsReady(fResult);
                        } else {
                            processBar.setIndeterminate(true);
                            launch = true;
                            mHandler.postDelayed(this, 1000);
                        }
                    }
                }
            }
        };
        mHandler.post(runnable);
    }

    private void resultsReady(final float fResult) {
        fCounter = 0;
        screenProcessing.setVisibility(View.GONE);
        screenResult.setVisibility(View.VISIBLE);

        Handler textAnimationHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                {
                    if (launch) {
                        launch = false;
                        mHandler.postDelayed(this, 1000);
                    } else if (hover) {
                        hover();
                    } else {
                        String sResultPre;
                        if (fCounter < fResult) {
                            sResultPre = String.valueOf(fCounter);
                        } else {
                            sResultPre = String.valueOf(fResult);
                            hover = true;
                            mHandler.postDelayed(this, 100);
                        }
                        int endPoint = sResultPre.indexOf('.') + 3;
                        final String sResult = (sResultPre.substring(0, endPoint <= sResultPre.length() ? endPoint : sResultPre.length())) + " %";
                        result.setText(sResult);

                        if (fCounter < fResult) {
                            fCounter += 1.18f;
                            mHandler.postDelayed(this, (int) (fCounter / fResult * 100));
                        }
                    }
                }
            }
        };
        textAnimationHandler.post(runnable);
    }

    private void hover() {
        if (isGoingUp) {
            result.animate().translationY(-6).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    isGoingUp = false;
                    hover();
                }
            });
        } else {
            result.animate().translationY(6).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    isGoingUp = true;
                    hover();
                }
            });
        }
    }
}