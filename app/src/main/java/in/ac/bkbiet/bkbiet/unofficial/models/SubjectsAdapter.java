package in.ac.bkbiet.bkbiet.unofficial.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Subject;

/**
 * SubjectsAdapter Created by Ashish on 12/17/2017.
 */

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.MyViewHolder> {

    private List<Syllabus.Subject> subjectsList;
    private SubjectClickListener subjectClickListener;

    public SubjectsAdapter(List<Subject> subjectsList, SubjectClickListener subjectClickListener) {
        this.subjectsList = subjectsList;
        this.subjectClickListener = subjectClickListener;
        setHasStableIds(true);
    }

    @Override
    public SubjectsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_subject, parent, false);
        return new SubjectsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SubjectsAdapter.MyViewHolder holder, int position) {
        final Subject subject = subjectsList.get(position);
        holder.name.setText(subject.subName);
        holder.code.setText(subject.subCode);
        String marks = "M.M. " + subject.subMarks;
        holder.maxMarks.setText(marks);

        if (subject.subRatings > 0) {
            float ratings = subject.subRatings / ((float) Math.ceil((float) subject.subRatings / 5));
            holder.pb_ratings.setProgress((int) ratings);
            holder.pb_ratings.setMax(5);
        } else {
            holder.pb_ratings.setVisibility(View.GONE);
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
        return subjectsList.size();
    }

    public interface SubjectClickListener {
        void onSubjectClick(View view, int position);

        void onSubjectLongClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView name, code, maxMarks;
        ProgressBar pb_ratings;

        MyViewHolder(final View view) {
            super(view);
            name = view.findViewById(R.id.tv_ms_name);
            code = view.findViewById(R.id.tv_ms_code);
            maxMarks = view.findViewById(R.id.tv_ms_marks);
            pb_ratings = view.findViewById(R.id.pb_ms_ratings);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            subjectClickListener.onSubjectClick(v, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            subjectClickListener.onSubjectLongClick(v, getLayoutPosition());
            return false;
        }
    }
}