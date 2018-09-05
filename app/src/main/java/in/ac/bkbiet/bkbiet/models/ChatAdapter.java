package in.ac.bkbiet.bkbiet.models;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.utils.ImageLoading.ImageLoader;
import in.ac.bkbiet.bkbiet.utils.Uv;

import static in.ac.bkbiet.bkbiet.utils.Statics.getTimeStamp;
import static in.ac.bkbiet.bkbiet.utils.Statics.viewFullScreenImage;

/**
 * ChatAdapter Created by Ashish on 12/7/2017.
 */

@SuppressWarnings("FieldCanBeLocal")
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private ChatClickListener chatClickListener;
    private List<Chat> chatsList;
    private String newMessageColor = "#0091ea";
    private String oldMessageColor = "#777777";

    public ChatAdapter(List<Chat> chatsList, ChatClickListener chatClickListener) {
        this.chatsList = chatsList;
        this.chatClickListener = chatClickListener;
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_chat, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Chat chat = chatsList.get(position);

        if (chat.getFirstPerson().isBlockingChat() || chat.getSecondPerson().isBlockingChat()) {
            holder.name.setTextColor(Color.parseColor("#d50000"));
        }else{
            holder.name.setTextColor(Color.parseColor("#000000"));
        }

        String timestamp = chat.getLast_msg_sent_at().substring(0, 10);
        if (getTimeStamp().substring(0, 10).equals(timestamp)) {
            timestamp = chat.getLast_msg_sent_at().substring(11, 16);
        }

        ImageLoader imageLoader = new ImageLoader(holder.dp.getContext());
        imageLoader.displayImage(chat.getSecondPerson().getDp_link(), holder.dp, chat.getSecondPerson().getName(), ImageLoader.QUALITY_MEDIUM);
        holder.name.setText(chat.getSecondPerson().getName());

        if (!chat.getLastMessage().getSenderUsername().equals(Uv.currUser.getUsername()) && chat.getLastMessage().getSeenAt().equals("not_yet_seen")) {
            holder.last_msg.setTypeface(null, Typeface.BOLD);
            holder.last_msg.setTextColor(Color.parseColor(newMessageColor));
        } else {
            holder.last_msg.setTypeface(null, Typeface.NORMAL);
            holder.last_msg.setTextColor(Color.parseColor(oldMessageColor));
        }

        holder.last_msg.setText(chat.getLastMessage().getMessage());
        holder.last_msg_sent_at.setText(timestamp);

        holder.dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFullScreenImage(holder.dp.getContext(), new User(chat.getSecondPerson().getUsername(), chat.getSecondPerson().getName()
                        , "", "", -1, "", chat.getSecondPerson().getDp_link()));
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
        return chatsList.size();
    }

    public interface ChatClickListener {
        void onChatClickListener(View view, int position);

        void onChatLongClickListener(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView dp;
        TextView name, last_msg, last_msg_sent_at;

        MyViewHolder(final View view) {
            super(view);
            dp = view.findViewById(R.id.iv_chat_dp);
            name = view.findViewById(R.id.tv_chat_name);
            last_msg = view.findViewById(R.id.tv_chat_last_msg);
            last_msg_sent_at = view.findViewById(R.id.tv_chat_last_msg_time);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            chatClickListener.onChatClickListener(v, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            chatClickListener.onChatLongClickListener(view, getLayoutPosition());
            return false;
        }
    }
}