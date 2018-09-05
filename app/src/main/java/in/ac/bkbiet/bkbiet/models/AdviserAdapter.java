package in.ac.bkbiet.bkbiet.models;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.utils.ImageLoading.ImageLoader;
import in.ac.bkbiet.bkbiet.utils.Uv;

import static in.ac.bkbiet.bkbiet.utils.Statics.getTimeStamp;

/**
 * AdviserAdapter Created by Ashish on 12/2/2017.
 */

public class AdviserAdapter extends RecyclerView.Adapter<AdviserAdapter.MyViewHolder> {

    private List<User> adviserList;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;

    public AdviserAdapter(List<User> adviserList, RecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.adviserList = adviserList;
        this.recyclerViewItemClickListener = recyclerViewItemClickListener;
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_adviser, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final User user = adviserList.get(position);

        holder.tv_name.setText(user.getName());
        holder.tv_email.setText(user.getEmail());

        final ImageLoader imageLoader = new ImageLoader(holder.iv_dp.getContext());
        imageLoader.displayImage(user.getDpLink(), holder.iv_dp, user.getName());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return adviserList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv_name, tv_email;
        public ImageView iv_dp;

        public MyViewHolder(final View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_am_name);
            tv_email = view.findViewById(R.id.tv_am_email);

            iv_dp = view.findViewById(R.id.iv_am_dp);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i("AdviserAdapter", "Click detected on " + this.getItemId());
            recyclerViewItemClickListener.onRecyclerViewItemClicked(v, this.getLayoutPosition());
        }
    }

    public interface RecyclerViewItemClickListener{
        void onRecyclerViewItemClicked(View view, int position);
    }
}