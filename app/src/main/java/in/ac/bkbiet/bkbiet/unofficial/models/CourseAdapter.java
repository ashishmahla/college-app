package in.ac.bkbiet.bkbiet.unofficial.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Courses;

/**
 * CourseAdapter Created by Ashish on 12/20/2017.
 */

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.MyViewHolder> {

    private List<Courses> courseList;
    private CourseAdapter.CourseClickListener courseClickListener;

    public CourseAdapter(List<Courses> courseList, CourseAdapter.CourseClickListener courseClickListener) {
        this.courseList = courseList;
        this.courseClickListener = courseClickListener;
        setHasStableIds(true);
    }

    @Override
    public CourseAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.model_courses, parent, false);
        return new CourseAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CourseAdapter.MyViewHolder holder, int position) {
        final Syllabus.Courses course = courseList.get(position);
        holder.name.setText(course.cName);
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
        return courseList.size();
    }

    public interface CourseClickListener {
        void onCourseClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;

        MyViewHolder(final View view) {
            super(view);
            name = view.findViewById(R.id.tv_mc_name);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            courseClickListener.onCourseClick(v, getLayoutPosition());
        }
    }
}