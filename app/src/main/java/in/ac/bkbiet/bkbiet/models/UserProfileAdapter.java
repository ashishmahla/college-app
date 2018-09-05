package in.ac.bkbiet.bkbiet.models;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.utils.ImageLoading.ImageLoader;
import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Sv;

import static in.ac.bkbiet.bkbiet.utils.Statics.getTimeStamp;
import static in.ac.bkbiet.bkbiet.utils.Statics.openDialerIntent;
import static in.ac.bkbiet.bkbiet.utils.Statics.openMailIntent;
import static in.ac.bkbiet.bkbiet.utils.Statics.viewFullScreenImage;

/**
 * UserProfileAdapter Created by Ashish on 12/2/2017.
 */

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.MyViewHolder> {

    private List<User> usersList;

    public UserProfileAdapter(List<User> usersList) {
        this.usersList = usersList;
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_user_profiles, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final User user = usersList.get(position);

        holder.tv_name.setText(user.getName());
        holder.tv_username.setText(user.getUsername());
        holder.tv_email.setText(user.getEmail());
        holder.tv_mobile_no.setText((user.getMobileNo().equalsIgnoreCase("null")) ? "" : user.getMobileNo());

        switch (user.getType()) {
            case User.TYPE_STUDENT:
                holder.tv_type.setText("Student");
                holder.tv_type.setTextColor(Color.argb(255, 24, 140, 30));
                break;
            case User.TYPE_TEACHER:
                holder.tv_type.setText("Teacher");
                holder.tv_type.setTextColor(Color.argb(255, 0, 0, 255));
                break;
            case User.TYPE_ADMIN:
                holder.tv_type.setText("Admin");
                holder.tv_type.setTextColor(Color.argb(255, 255, 0, 0));
                break;
            default:
                holder.tv_type.setText("Type Unknown");
                holder.tv_type.setTextColor(Color.argb(170, 0, 0, 0));
        }
        final ImageLoader imageLoader = new ImageLoader(holder.iv_dp.getContext());
        imageLoader.displayImage(user.getDpLink(), holder.iv_dp, user.getName());

        holder.iv_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFullScreenImage(holder.iv_dp.getContext(), user);
            }
        });

        if (Sv.getBooleanSetting(Sv.dDOWNLOAD_IMAGES_ON_LONG_PRESS, false)) {
            holder.iv_dp.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AuditRecord auditRecord = new AuditRecord("DownloadImage", "target_username#:" + user.getUsername() + "#;" +
                            "target_name#:" + user.getName() + "#;" + "target_dpLink#:" + user.getDpLink());
                    Statics.saveNewAuditRecord(auditRecord);
                    imageLoader.saveImage(holder.iv_dp.getContext(), user.getDpLink(), user.getName() + "--" + getTimeStamp(), "BKB_PICS", ImageLoader.QUALITY_MAX);
                    return true;
                }
            });
        }

        holder.tv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuditRecord auditRecord = new AuditRecord("OpenEmail", "target_username#:" + user.getUsername() + "#;" +
                        "target_name#:" + user.getName() + "#;" + "target_email#:" + user.getEmail());
                Statics.saveNewAuditRecord(auditRecord);
                openMailIntent(v.getContext(), new String[]{user.getEmail()});
            }
        });

        holder.tv_mobile_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuditRecord auditRecord = new AuditRecord("OpenMobileNo", "target_username#:" + user.getUsername() + "#;" +
                        "target_name#:" + user.getName() + "#;" + "target_mobileNo#:" + user.getDpLink());
                Statics.saveNewAuditRecord(auditRecord);
                openDialerIntent(v.getContext(), user.getMobileNo());
            }
        });
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
        return usersList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_username, tv_email, tv_mobile_no, tv_type;
        public ImageView iv_dp;

        public MyViewHolder(final View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_uvm_name);
            tv_username = view.findViewById(R.id.tv_uvm_username);
            tv_email = view.findViewById(R.id.tv_uvm_email);
            tv_mobile_no = view.findViewById(R.id.tv_uvm_mobile_no);
            tv_type = view.findViewById(R.id.tv_uvm_type);

            iv_dp = view.findViewById(R.id.iv_uvm_dp);
        }
    }
}