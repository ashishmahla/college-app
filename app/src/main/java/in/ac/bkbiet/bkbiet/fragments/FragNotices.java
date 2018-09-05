package in.ac.bkbiet.bkbiet.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.activities.NewNoticeActivity;
import in.ac.bkbiet.bkbiet.models.Notice;
import in.ac.bkbiet.bkbiet.models.NoticeAdapter;

/**
 * Created by Ashish on 4/21/2018.
 */

public class FragNotices extends Fragment {

    View _rootView;
    RecyclerView rv_notices;
    NoticeAdapter mAdapter;
    List<Notice> noticeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (_rootView == null) {
            // Inflate the layout for this fragment
            _rootView = inflater.inflate(R.layout.frag_notices, container, false);
            // Find and setup subviews
            initViews(_rootView);
        }
        /*else {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove _rootView from the existing parent view group
            // in onDestroyView() (it will be added back).
        }*/
        return _rootView;
    }

    private void initViews(View parent) {
        rv_notices = parent.findViewById(R.id.rv_fn_notices);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_notices.setLayoutManager(mLayoutManager);
        rv_notices.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new NoticeAdapter(noticeList, null);
        rv_notices.setAdapter(mAdapter);

        prepareChatData();

        parent.findViewById(R.id.fab_fn_new_notice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewNoticeActivity.class));
            }
        });
    }

    private void prepareChatData() {
        DatabaseReference noticeListRef = FirebaseDatabase.getInstance().getReference("notices");
        noticeListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                noticeList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Notice notice = ds.getValue(Notice.class);
                    if (notice == null) continue;
                    notice.setUid(ds.getKey());

                    noticeList.add(notice);
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        if (_rootView.getParent() != null) {
            ((ViewGroup) _rootView.getParent()).removeView(_rootView);
        }
        super.onDestroyView();
    }
}
