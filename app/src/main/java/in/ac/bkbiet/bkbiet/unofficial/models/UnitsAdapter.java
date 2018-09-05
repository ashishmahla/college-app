package in.ac.bkbiet.bkbiet.unofficial.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import in.ac.bkbiet.bkbiet.R;

/**
 * UnitsAdapter Created by Ashish on 12/17/2017.
 */

public class UnitsAdapter extends RecyclerView.Adapter<UnitsAdapter.MyViewHolder> {

    private List<Syllabus.Unit> unitsList;
    private UnitsAdapter.UnitClickListener unitClickListener;

    public UnitsAdapter(List<Syllabus.Unit> unitsList, UnitsAdapter.UnitClickListener unitClickListener) {
        this.unitsList = unitsList;
        this.unitClickListener = unitClickListener;
        setHasStableIds(true);
    }

    @Override
    public UnitsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_unit, parent, false);
        return new UnitsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UnitsAdapter.MyViewHolder holder, int position) {
        final Syllabus.Unit unit = unitsList.get(position);
        holder.name.setText(unit.uName);
        String number = "Unit # " + unit.uNumber;
        holder.number.setText(number);
        String sTopics = "";
        for (Syllabus.Topic topic : unit.topics) {
            sTopics += topic.tName + "\n\n";
        }
        holder.topics.setText(sTopics);
        if (unit.uRatings > 0) {
            holder.ratings.setRating(unit.uRatings);
        } else {
            holder.ratings.setVisibility(View.GONE);
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
        return unitsList.size();
    }

    public interface UnitClickListener {
        void onUnitClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, number, topics;
        RatingBar ratings;

        MyViewHolder(final View view) {
            super(view);
            name = view.findViewById(R.id.tv_mu_name);
            number = view.findViewById(R.id.tv_mu_number);
            topics = view.findViewById(R.id.tv_mu_topics);
            ratings = view.findViewById(R.id.rb_mu_rating);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            unitClickListener.onUnitClick(v, getLayoutPosition());
        }
    }
}