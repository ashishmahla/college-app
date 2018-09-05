package in.ac.bkbiet.bkbiet.models;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.utils.Sv;
import in.ac.bkbiet.bkbiet.utils.Uv;

import static in.ac.bkbiet.bkbiet.utils.Statics.getTimeStamp;

/**
 * MessageAdapter Created by Ashish on 12/7/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private Context context;
    private List<Message> msgsList;

    public MessageAdapter(Context context, List<Message> msgsList) {
        this.context = context;
        this.msgsList = msgsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_message, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Message msg = msgsList.get(position);
        String sender = msg.getSenderUsername();
        //holder.sentAt.setVisibility(View.GONE);

        if ((sender.equals(Uv.currUser.getUsername()))) {
            holder.rl_msg.setGravity(Gravity.END);
            holder.sentAt.setGravity(Gravity.END);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            holder.card_msgText.setLayoutParams(params);

            //RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.sentAt.setGravity(Gravity.END);
            //holder.iv_receipts.setLayoutParams(llParams);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(120, 2, 2, 0);
            holder.rl_msg.setLayoutParams(layoutParams);
            holder.msg.setBackgroundColor(0xffeeeeee);//0xfa5f5f5f);
            holder.msg.setTextColor(0xff000000);

            if (msg.showReceiptsToSender || Sv.getBooleanSetting(Sv.dSHOW_RECEIPTS, false)) {
                holder.iv_receipts.setVisibility(View.VISIBLE);
                if (!msg.getSeenAt().equals("not_yet_seen")) {
                    holder.iv_receipts.setImageResource(R.drawable.ic_message_received);
                    holder.iv_receipts.setColorFilter(Color.BLUE);
                } else if (!msg.getReceivedAt().equals("not_yet_received")) {
                    holder.iv_receipts.setImageResource(R.drawable.ic_message_received);
                    holder.iv_receipts.setColorFilter(Color.BLACK);
                } else {
                    holder.iv_receipts.setImageResource(R.drawable.ic_message_sent);
                    holder.iv_receipts.setColorFilter(Color.BLACK);
                }
            }
        }

        holder.msg.setText(msg.getMessage());

        String timestamp = msg.getSentAt().substring(0, 10);
        if (getTimeStamp().substring(0, 10).equals(timestamp)) {
            timestamp = msg.getSentAt().substring(11, 16);
        } else {
            timestamp = msg.getSentAt().substring(0, 5) + " ";
            timestamp += msg.getSentAt().substring(11, 16);
        }
        holder.sentAt.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        return msgsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView msg, sentAt;
        RelativeLayout rl_msg;
        CardView card_msgText;
        ImageView iv_receipts;

        public MyViewHolder(View view) {
            super(view);
            msg = view.findViewById(R.id.tv_mm_msg);
            sentAt = view.findViewById(R.id.tv_mm_sentAt);
            card_msgText = view.findViewById(R.id.card_mm_msgText);
            rl_msg = view.findViewById(R.id.rl_mm_msg);
            iv_receipts = view.findViewById(R.id.iv_mm_receipts);

            view.setOnClickListener(this);
            msg.setOnClickListener(this);
            card_msgText.setOnClickListener(this);
            sentAt.setOnClickListener(this);
            iv_receipts.setOnClickListener(this);
            rl_msg.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (sentAt.getVisibility() == View.VISIBLE) {
                sentAt.animate().alpha(0).translationY(-20f).setDuration(400).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        sentAt.setVisibility(View.INVISIBLE);
                    }
                });
            } else {
                sentAt.setVisibility(View.VISIBLE);
                sentAt.animate().alpha(1).translationY(0).setDuration(400);
            }
        }
    }
}
