package in.ac.bkbiet.bkbiet.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.ac.bkbiet.bkbiet.R;

/**
 * AuditAdapter Created by Ashish on 12/9/2017.
 */

public class AuditAdapter extends RecyclerView.Adapter<AuditAdapter.MyViewHolder> {

    private List<AuditRecord> auditsList;

    public AuditAdapter(List<AuditRecord> auditsList) {
        this.auditsList = auditsList;
        setHasStableIds(true);
    }

    @Override
    public AuditAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_audit, parent, false);
        return new AuditAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AuditAdapter.MyViewHolder holder, int position) {
        final AuditRecord audit = auditsList.get(position);
        String desc = audit.getDesc();
        String type = audit.getTransactionType();
        String username = audit.getUsername();
        String dateTimeStamp = audit.getTimeStamp();

        String target;
        switch (type) {
            case "OpenMobileNo":
            case "OpenEmail":
            case "ViewFullScreenImage":
                target = desc.substring(desc.indexOf("target_name#:") + 13, desc.lastIndexOf("#;"));
                break;
            case "ProjectReg":
                target = "Self";
                break;
            default:
                target = "Not applicable";
        }

        String placeholder = username + "  ->  " + target;
        holder.header.setText(placeholder);
        holder.tranType.setText(type);
        holder.dateTimeStamp.setText(dateTimeStamp);
        holder.tranType.setText(type);
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
        return auditsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tranType, header, dateTimeStamp;

        MyViewHolder(final View view) {
            super(view);
            tranType = view.findViewById(R.id.tv_ma_transaction_type);
            header = view.findViewById(R.id.tv_ma_transaction);
            dateTimeStamp = view.findViewById(R.id.tv_ma_dateTimeStamp);
        }
    }
}
