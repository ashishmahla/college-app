package in.ac.bkbiet.bkbiet.models;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.utils.SQLiteHandler;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * Created by Ashish on 9/6/2017.
 */

public class NotyAdapter extends RecyclerView.Adapter<in.ac.bkbiet.bkbiet.models.NotyAdapter.NotyViewHolder> {
    private final LayoutInflater inflater;
    private View view;
    private in.ac.bkbiet.bkbiet.models.NotyAdapter.NotyViewHolder nViewHolder;
    private ArrayList<Noty> _noty = new ArrayList<>();
    private Context context;

    public NotyAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return _noty.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setListContent(ArrayList<Noty> _noty) {
        this._noty = _noty;
    }

    @Override
    public in.ac.bkbiet.bkbiet.models.NotyAdapter.NotyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.model_noty, parent, false);
        nViewHolder = new in.ac.bkbiet.bkbiet.models.NotyAdapter.NotyViewHolder(view);
        return nViewHolder;
    }

    @Override
    public void onBindViewHolder(in.ac.bkbiet.bkbiet.models.NotyAdapter.NotyViewHolder holder, final int position) {
        final Noty noty = _noty.get(position);

        if (!noty.isRead()) {
            holder.ll_noty_card.setBackgroundColor(Color.argb(10, 0, 0, 0));
        }

        holder.tv_title.setText(noty.getTitle());
        holder.tv_body.setText(noty.getBody());
        holder.tv_sender.setText(noty.getSender());
        holder.tv_receivedAt.setText(noty.getReceivedAt().substring(noty.getReceivedAt().length()-5));

        holder.ll_noty_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(context);
                adb.setTitle(noty.getTitle());
                adb.setMessage("Id = "+ noty.getId() +"\nSent At : " + noty.getSentAt() + "\nGot At : " + noty.getReceivedAt() +
                        "\nIs Read : "+noty.isRead()+"\nRead At : " + noty.getReadAt());
                adb.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                if (Uv.isDevOn){
                    adb.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            SQLiteHandler db= new SQLiteHandler(context.getApplicationContext());
                            boolean deleted=false;
                            try {
                                deleted=db.deleteNotyWithId(noty.getId());
                            }catch (Exception e){
                                e.printStackTrace();
                            }finally {
                                db.close();
                            }
                            if (deleted) {
                                _noty.remove(position);
                                NotyAdapter.this.notifyDataSetChanged();
                                Toast.makeText(context,"Notification deleted.",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context,"Unable to delete notification.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                adb.show();
                return true;
            }
        });
    }

    class NotyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_body, tv_receivedAt, tv_sender;
        LinearLayout ll_noty_card = (LinearLayout) itemView.findViewById(R.id.ll_card_noty);

        private NotyViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_body = itemView.findViewById(R.id.tv_body);
            tv_receivedAt = itemView.findViewById(R.id.tv_receivedAt);
            tv_sender = itemView.findViewById(R.id.sender);
        }
    }
}
