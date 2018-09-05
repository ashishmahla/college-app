package in.ac.bkbiet.bkbiet.fragments;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.models.Noty;
import in.ac.bkbiet.bkbiet.models.NotyAdapter;
import in.ac.bkbiet.bkbiet.utils.SQLiteHandler;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * FragNoties Created by Ashish on 9/6/2017.
 */

public class FragNoties extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Noty> noties = new ArrayList<>();
    NotyAdapter notyAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.activity_notifications, container, false);

        initViews(parent);
        registerBroadcastListener();

        return parent;
    }

    @Override
    public void onResume() {
        super.onResume();
        NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        assert nm != null;
        nm.cancel(Uv.DEFAULT_NOTIFICATION_ID);
    }

    private void initViews(View parent) {
        //android.support.v7.app.ActionBar actionBar = parent.getSupportActionBar();
        /*if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/

        recyclerView = parent.findViewById(R.id.rv_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration did = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(did);
        notyAdapter = new NotyAdapter(getActivity());
        populateNotifications();
        Statics.sendNotyCountBroadcast(getActivity(), 0);

        if (noties.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            CardView card_no_noties = parent.findViewById(R.id.card_n_no_noties);
            card_no_noties.setVisibility(View.VISIBLE);
        }
    }

    private void populateNotifications() {
        SQLiteHandler db = new SQLiteHandler(getActivity());
        noties = db.getAllNotifications();
        db.markAllAsRead();
        db.close();

        notyAdapter.setListContent(noties);
        recyclerView.setAdapter(notyAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (Uv.isDevOn) {
            inflater.inflate(R.menu.notifications_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.noty_delete_all:
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Delete All").setMessage("Are you sure you wanna delete all notifications ?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                SQLiteHandler db = new SQLiteHandler(getActivity());
                                int entriesDeleted = db.resetNotyTable();
                                db.close();
                                populateNotifications();
                                Toast.makeText(getActivity(), "Deleted " + entriesDeleted + " notifications.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                adb.show();
                break;
        }
        return true;
    }

    private void registerBroadcastListener() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        populateNotifications();
                    }
                }, new IntentFilter(Uv.ACTION_NOTY_COUNT_BROADCAST)
        );
    }
}