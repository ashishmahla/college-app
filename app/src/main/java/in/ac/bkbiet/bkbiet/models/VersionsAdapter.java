package in.ac.bkbiet.bkbiet.models;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.ac.bkbiet.bkbiet.BuildConfig;
import in.ac.bkbiet.bkbiet.R;

/**
 * Created by Ashish on 9/5/2017.
 */

public class VersionsAdapter extends RecyclerView.Adapter<VersionsAdapter.VersionsViewHolder> {
    private final LayoutInflater inflater;
    View view;
    VersionsViewHolder vViewHolder;
    private ArrayList<Versions> _versions = new ArrayList<>();
    private Context context;

    public VersionsAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return _versions.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setListContent(ArrayList<Versions> _versions) {
        this._versions = _versions;
    }

    @Override
    public VersionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.model_version, parent, false);
        vViewHolder = new VersionsViewHolder(view);
        return vViewHolder;
    }

    @Override
    public void onBindViewHolder(VersionsViewHolder holder, int position) {
        Versions version = _versions.get(position);

        if (version.getvCode() > BuildConfig.VERSION_CODE) {
            holder.ll_versions_card.setBackgroundColor(Color.rgb(174, 213, 129));
            holder.tv_status.setVisibility(View.VISIBLE);
            holder.tv_status.setText("New");

        } else if (version.getvCode() == BuildConfig.VERSION_CODE) {
            holder.ll_versions_card.setBackgroundColor(Color.rgb(2,136, 209));
            holder.tv_status.setVisibility(View.VISIBLE);
            holder.tv_status.setBackgroundColor(Color.argb(0,0,0,0));
        }else{
            //holder.ll_versions_card.setBackgroundColor(Color.rgb(230, 230, 230));
        }

        holder.tv_version.setText(version.getvName());
        holder.tv_info.setText(version.getvWhatsNew());
        holder.tv_release_date.setText("Released on "+version.getvReleaseDate());
    }

    class VersionsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_version, tv_info,tv_status,tv_release_date;
        LinearLayout ll_versions_card = (LinearLayout) itemView.findViewById(R.id.ll_version_card);

        private VersionsViewHolder(View itemView) {
            super(itemView);
            tv_version = itemView.findViewById(R.id.version_name);
            tv_info = itemView.findViewById(R.id.version_changes);
            tv_status= itemView.findViewById(R.id.tv_vStatus);
            tv_release_date= itemView.findViewById(R.id.version_release_date);
        }
    }
}
