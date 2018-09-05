package in.ac.bkbiet.bkbiet.models;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import in.ac.bkbiet.bkbiet.R;

/**
 * Adapter template created by Ashish Mahla on 14/2/2017.
 */

@SuppressWarnings("FieldCanBeLocal")
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> {

    private NoticeClickListener noticeClickListener;
    private List<Notice> noticeList;

    public NoticeAdapter(List<Notice> noticeList, NoticeClickListener noticeClickListener) {
        this.noticeList = noticeList;
        this.noticeClickListener = noticeClickListener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_notice, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Notice notice = noticeList.get(position);

        holder.nTitle.setText(notice.getTitle());
        holder.nDesc.setText(notice.getDesc());
        Glide.with(holder.nImage.getContext())
                .load(notice.getImgUrl()).into(holder.nImage);
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
        return noticeList.size();
    }

    public interface NoticeClickListener {
        void onNoticeClickListener(View view, int position);

        void onNoticeLongClickListener(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView nImage;
        TextView nTitle, nDesc;

        MyViewHolder(final View view) {
            super(view);

            //--------------------------------------------------------
            nImage = view.findViewById(R.id.iv_mn_image);
            nTitle = view.findViewById(R.id.tv_mn_title);
            nDesc = view.findViewById(R.id.tv_mn_desc);
            //--------------------------------------------------------

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (noticeClickListener == null)
                return;

            if (nDesc.getMaxLines() > 4) {
                nDesc.setMaxLines(100);
            } else {
                nDesc.setMaxLines(4);
            }

            noticeClickListener.onNoticeClickListener(v, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (noticeClickListener == null)
                return false;

            noticeClickListener.onNoticeLongClickListener(view, getLayoutPosition());
            return false;
        }
    }
}