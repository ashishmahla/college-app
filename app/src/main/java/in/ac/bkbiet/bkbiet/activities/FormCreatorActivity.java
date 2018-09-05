package in.ac.bkbiet.bkbiet.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import in.ac.bkbiet.bkbiet.R;

/**
 * FCA Created by Ashish on 9/3/2017.
 */

public class FormCreatorActivity extends AppCompatActivity {
    private static String TAG = "FormCreatorActivity";
    FloatingActionButton fab_add_field;
    LinearLayout creatorWindow;
    int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_creator);
        Log.i("FormCreatorActivity", "onCreate called.");

        initViews();
    }

    private void initViews() {
        creatorWindow = findViewById(R.id.creatorWindow);

        fab_add_field = findViewById(R.id.fab_add_field);

        fab_add_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*count++;
                notifier("#"+count+count+count);*/
                addNewForm();
            }
        });
    }

    private boolean addNewForm() {
        AlertDialog.Builder ad = new AlertDialog.Builder(FormCreatorActivity.this);
        ad.setTitle("Choose Field Type");
        ad.setSingleChoiceItems(R.array.field_types, 0, null);
        ad.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                int pos = ((AlertDialog) dialog).getListView().getSelectedItemPosition();
                createField(pos);
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
        return false;
    }

    private void createField(int pos) {
        RelativeLayout rl_container = findViewById(R.id.rl_container);
        rl_container.setVisibility(View.VISIBLE);

        Context context = FormCreatorActivity.this;
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextInputLayout.LayoutParams tilsLp = new TextInputLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputEditText tiets = new TextInputEditText(context);
        tiets.setInputType(InputType.TYPE_CLASS_TEXT);
        tiets.setHint("Label");

        TextInputLayout tils = new TextInputLayout(context);
        tils.addView(tiets, tilsLp);
        creatorWindow.addView(tils, lllp);

        switch (pos) {
            case 0://Edit Text

        }
    }

    private void createInputField(String hint, int inputType) {
        Context context = FormCreatorActivity.this;
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextInputLayout.LayoutParams tilsLp = new TextInputLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputEditText tiets = new TextInputEditText(context);
        tiets.setInputType(inputType);
        tiets.setHint(hint);

        TextInputLayout tils = new TextInputLayout(context);
        tils.addView(tiets, tilsLp);
        creatorWindow.addView(tils, lllp);
    }
}
