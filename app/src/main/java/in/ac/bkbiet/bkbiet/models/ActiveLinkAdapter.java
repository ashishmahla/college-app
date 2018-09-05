package in.ac.bkbiet.bkbiet.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.ac.bkbiet.bkbiet.R;

/**
 * Adapter template created by Ashish Mahla on 14/2/2017.
 */

@SuppressWarnings("FieldCanBeLocal")
public class ActiveLinkAdapter extends RecyclerView.Adapter<ActiveLinkAdapter.MyViewHolder> {

    private ActiveLinkClickListener ActiveLinkClickListener;
    private List<ActiveLink> ActiveLinkList;

    public ActiveLinkAdapter(List<ActiveLink> ActiveLinkList, ActiveLinkClickListener ActiveLinkClickListener) {
        this.ActiveLinkList = ActiveLinkList;
        this.ActiveLinkClickListener = ActiveLinkClickListener;
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_active_link, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ActiveLink activeLink = ActiveLinkList.get(position);
        holder.name.setText(activeLink.getName());
        holder.url.setText(activeLink.getUrl());

        if (activeLink.getDesc() == null || activeLink.getDesc().isEmpty()) {
            holder.desc.setVisibility(View.GONE);
        } else {
            holder.desc.setText(activeLink.getDesc());
        }

        if (activeLink.getAuthorName() == null || activeLink.getAuthorName().isEmpty()) {
            holder.authorName.setVisibility(View.GONE);
        } else {
            holder.authorName.setText(activeLink.getAuthorName());
        }

        if (activeLink.isExpirable()) {
            holder.expiryDate.setText(activeLink.getExpiryDate());
        } else {
            holder.expiryDate.setVisibility(View.GONE);
        }
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
        return ActiveLinkList.size();
    }

    public interface ActiveLinkClickListener {
        void onActiveLinkClickListener(View view, int position);

        boolean onActiveLinkLongClickListener(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView name, url, desc, expiryDate, authorName;

        MyViewHolder(final View view) {
            super(view);

            //--------------------------------------------------------
            name = view.findViewById(R.id.tv_mal_name);
            url = view.findViewById(R.id.tv_mal_url);
            desc = view.findViewById(R.id.tv_mal_desc);
            expiryDate = view.findViewById(R.id.tv_mal_expiry_date);
            authorName = view.findViewById(R.id.tv_mal_author_name);
            //--------------------------------------------------------

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ActiveLinkClickListener.onActiveLinkClickListener(v, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            return ActiveLinkClickListener.onActiveLinkLongClickListener(view, getLayoutPosition());
        }
    }
}