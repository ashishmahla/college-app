package in.ac.bkbiet.bkbiet.unofficial;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.unofficial.models.CourseAdapter;
import in.ac.bkbiet.bkbiet.unofficial.models.SubjectsAdapter;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Courses;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Subject;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Topic;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Unit;
import in.ac.bkbiet.bkbiet.unofficial.models.UnitsAdapter;
import in.ac.bkbiet.bkbiet.unofficial.utils.SyllabusCache;
import in.ac.bkbiet.bkbiet.utils.Sv;
import in.ac.bkbiet.bkbiet.utils.Uv;

import static in.ac.bkbiet.bkbiet.utils.Sv.getIntSetting;
import static in.ac.bkbiet.bkbiet.utils.Sv.getSetting;
import static in.ac.bkbiet.bkbiet.utils.Sv.setSetting;

/**
 * ActivitySyllabus Created by Ashish on 12/17/2017.
 */

public class ActivitySyllabus extends AppCompatActivity implements SubjectsAdapter.SubjectClickListener, UnitsAdapter.UnitClickListener, CourseAdapter.CourseClickListener {

    static final int COURSES = 0;
    static final int SUBJECTS = 1;
    static final int UNITS = 2;
    static final int TOPICS = 3;
    final ArrayList<Subject> subjects = new ArrayList<>();
    final ArrayList<Unit> units = new ArrayList<>();
    RecyclerView rv_hybrid;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("syllabuses");
    SubjectsAdapter subjectsAdapter = new SubjectsAdapter(subjects, this);
    UnitsAdapter unitsAdapter = new UnitsAdapter(units, this);
    TextView header, subHeader;
    int currShowing = 0;
    Subject lastSub;
    Unit lastUnit;
    Syllabus lastSyllabus;
    boolean isSavingItems = false;
    //ArrayList<Syllabus> syllabusList = new ArrayList<>();
    ArrayList<Courses> courses = new ArrayList<>();
    CourseAdapter courseAdapter = new CourseAdapter(courses, this);
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);

        isSavingItems = Sv.getBooleanSetting(Sv.pIS_SAVING_SYLLABUSES, false);

        context = ActivitySyllabus.this;

        rv_hybrid = findViewById(R.id.rv_as_hybrid);
        rv_hybrid.setAdapter(subjectsAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_hybrid.setLayoutManager(mLayoutManager);
        rv_hybrid.setItemAnimator(new DefaultItemAnimator());

        header = findViewById(R.id.tv_as_heading);
        subHeader = findViewById(R.id.tv_as_sub_heading);

        /*Topic topic = new Topic("Mobile Computing: Definitions, adaptability...etc.", -1);
        Unit unit = new Unit(1, "Mobile Computing", -1);
        unit.topics = new ArrayList<>();
        unit.topics.add(topic);

        Book book = new Book("Mobile Communications", "Jochen, Pearson", "No desc yet.", "Standard", 345, -1);

        Subject subject = new Subject("Mobile Computing", "8CS1A", 100, -1);
        subject.units = new ArrayList<>();
        subject.units.add(unit);
        subject.books = new ArrayList<>();
        subject.books.add(book);

        Syllabus syllabus = new Syllabus("b-tech-cse-4-8", 4, 8, "BTech: Computer Science Engineering");
        syllabus.subjects = new ArrayList<>();
        syllabus.subjects.add(subject);

        addSyllabus(syllabus);*/

        Syllabus pinnedSyllabus = getPinnedSyllabus();
        if (pinnedSyllabus != null) {
            showSubjects(pinnedSyllabus);
        } else {
            showCoursesList();
        }
        loadAds();
    }

    private void showCoursesList() {
        currShowing = COURSES;
        String cat = "Available Courses";
        header.setText(cat);
        String placeholder = "Select a Course";
        subHeader.setText(placeholder);
        rv_hybrid.setAdapter(courseAdapter);

        courses.clear();
        courses.addAll(SyllabusCache.getInstance().getAllCoursesList(context));

        DatabaseReference coursesRef = db.child("courses");
        coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Courses course = new Courses();
                    course.cName = ds.getKey();

                    if (!isCoursePresent(course.cName, courses)) {
                        if (!courses.contains(course)) {
                            ArrayList<Syllabus> syllabi = new ArrayList<>();
                            for (DataSnapshot child : ds.getChildren()) {
                                Syllabus syllabus = new Syllabus();
                                syllabus.sCourse = course.cName;
                                syllabus.sId = child.getValue(String.class);
                                syllabus.sSem = Integer.parseInt(child.getKey());
                                syllabus.sYear = (int) Math.ceil(syllabus.sSem / 2);
                                syllabi.add(syllabus);
                            }
                            course.availSyllabi = syllabi;
                            courses.add(course);
                            if (isSavingItems) {
                                SyllabusCache.getInstance().saveCourse(context, course);
                            }
                            courseAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void pinSyllabus(Syllabus syllabus) {
        setSetting(context, Sv.sPINNED_SYLLABUS, syllabus.sId);
    }

    private Syllabus getPinnedSyllabus() {
        String syllabusId = getSetting(Sv.sPINNED_SYLLABUS, null);
        if (syllabusId == null) return null;

        return SyllabusCache.getInstance().getSyllabusById(context, syllabusId);
    }

    private boolean isCoursePresent(String courseName, ArrayList<Courses> courses) {
        boolean isCoursePresent = false;
        for (Courses c : courses) {
            if (c.cName.equals(courseName)) {
                isCoursePresent = true;
            }
        }
        return isCoursePresent;
    }

    /*private void showSyllabusList() {
        //HashMap<String, String> courses = new HashMap<>();

        syllabusList.addAll(new SyllabusCache(context)
                .getAllSyllabusList());

        if (!syllabusList.isEmpty()) {
            showSubjects(syllabusList.get(0));
        } else {
            DatabaseReference subRef = db.child("syllabus");
            subRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot shot : dataSnapshot.getChildren()) {
                        Syllabus syllabus = shot.getValue(Syllabus.class);
                        if (syllabus != null) {
                            showSubjects(syllabus);
                            new SyllabusCache(context)
                                    .saveSyllabus(syllabus);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }*/

    private void showSubjects(final Syllabus syllabus) {
        currShowing = SUBJECTS;
        String cat = syllabus.sYear + " Year, " + syllabus.sSem + " Semester";
        header.setText(cat);
        subHeader.setText(syllabus.sCourse);
        rv_hybrid.setAdapter(subjectsAdapter);

        if (lastSyllabus != syllabus || subjects.isEmpty()) {
            lastSyllabus = syllabus;
            subjects.clear();

            subjects.addAll(SyllabusCache.getInstance().getAllSubjectsForSyllabus(context, syllabus.sId));

            if (subjects.isEmpty()) {
                DatabaseReference subRef = db.child("subjects").child(syllabus.sId);
                subRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot subShot : dataSnapshot.getChildren()) {
                            Subject sub = subShot.getValue(Subject.class);
                            subjects.add(sub);
                        }

                        if (isSavingItems) {
                            SyllabusCache.getInstance().saveSubjectsForSyllabus(context, subjects, syllabus.sId);
                        }
                        subjectsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                subjectsAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showUnits(final Subject subject) {
        currShowing = UNITS;
        header.setText(subject.subName);
        subHeader.setText(subject.subCode);
        rv_hybrid.setAdapter(unitsAdapter);

        if (lastSub != subject || units.isEmpty()) {
            lastSub = subject;
            units.clear();

            units.addAll(SyllabusCache.getInstance().getAllUnitsForSubjectWithTopics(context, subject.subCode));

            if (units.isEmpty()) {
                DatabaseReference subRef = db.child("units").child(subject.subCode.replace('.', '_'));
                subRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (final DataSnapshot unitShot : dataSnapshot.getChildren()) {
                            final Unit unit = unitShot.getValue(Unit.class);

                            assert unit != null;
                            unit.topics = SyllabusCache.getInstance().getAllTopicsForUnitAndSub(context, unit.uNumber, subject.subCode);

                            if (unit.topics.isEmpty()) {
                                DatabaseReference subRef = db.child("topics").child(subject.subCode + "%%" + unit.uNumber);
                                final ArrayList<Topic> topics = new ArrayList<>();
                                subRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot topicShot : dataSnapshot.getChildren()) {
                                            Topic topic = topicShot.getValue(Topic.class);
                                            topics.add(topic);
                                        }
                                        unit.topics = topics;
                                        units.add(unit);
                                        unitsAdapter.notifyDataSetChanged();
                                        if (isSavingItems) {
                                            SyllabusCache.getInstance().saveTopicsForUnitAndSub(context, topics, unit.uNumber, subject.subCode);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                units.add(unit);
                            }
                        }

                        if (isSavingItems) {
                            SyllabusCache.getInstance().saveUnitsForSub(context, units, subject.subCode);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                unitsAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showTopics(final Unit unit) {
        lastUnit = unit;
    }

    /*private void addTopicsToUnit(ArrayList<Topic> topics, String root) {
        DatabaseReference topicsRef = dbRef.child("syllabuses").child("topics");

        for (Topic topic : topics) {
            String topicId = topic.tName.substring(0, topic.tName.length() > 10 ? 10 : topic.tName.length());
            topic.tId = topicId;
            topicsRef.child(root).child(topicId).setValue(topic);
        }
    }*/

    /*private void showAddUnitToSubject(Subject subject) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle("Add Subject to Syllabus");
        TextInputEditText tiet = new TextInputEditText(context);
        TextInputLayout til = new TextInputLayout(context);
    }*/

    /*private void addUnitsToSubject(ArrayList<Unit> units, String subjectCode) {
        DatabaseReference unitsRef = dbRef.child("syllabuses").child("units");

        for (Unit unit : units) {
            addTopicsToUnit(unit.topics, subjectCode + "%%" + unit.uNumber);
            unitsRef.child(subjectCode).child(("" + unit.uNumber)).setValue(unit);
        }
    }*/

    /*private void addBooksToSubject(ArrayList<Book> books) {
        DatabaseReference booksRef = dbRef.child("syllabuses").child("books");

        for (Book book : books) {
            String bookId = book.bName + "%%" + book.bAuthor;
            book.bId = bookId;
            booksRef.child(bookId).setValue(book);
        }
    }*/

    /*private void addSubjectsToSyllabus(ArrayList<Subject> subjects, String syllabusId) {
        DatabaseReference subjectsRef = dbRef.child("syllabuses").child("subjects");

        for (Subject subject : subjects) {
            addUnitsToSubject(subject.units, subject.subCode);
            //addBooksToSubject(subject.books);
            subjectsRef.child(syllabusId).child(subject.subCode).setValue(subject);
        }
    }*/

    /*private void addSyllabus(Syllabus syllabus) {
        DatabaseReference syllabusRef = dbRef.child("syllabuses").child("syllabus");

        addSubjectsToSyllabus(syllabus.subjects, syllabus.sId);
        syllabusRef.child(syllabus.sId).setValue(syllabus);
    }*/

    private void loadAds() {
        RelativeLayout rl_adContainer = findViewById(R.id.rl_as_ad_container);

        AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        rl_adContainer.addView(mAdView);

        switch (getIntSetting(Sv.dAD_TYPE_TO_SHOW, 1)) {
            case 1:
                Log.e("debug", "Loading ads");
                mAdView.setAdUnitId(Uv.sADMOB_BANNER_AD_ID);
                mAdView.loadAd(new AdRequest.Builder().build());
                break;
            case 2:
                Log.e("debug", "Loading devAds");
                mAdView.setAdUnitId(Uv.sADMOB_DEV_BANNER_AD_ID);
                mAdView.loadAd(new AdRequest.Builder().build());
                break;
            case 3:
                break;
            default:
                Log.e("debug", "Loading ads");
                mAdView.setAdUnitId(Uv.sADMOB_BANNER_AD_ID);
                mAdView.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    public void onBackPressed() {
        switch (currShowing) {
            case COURSES:
                super.onBackPressed();
                break;
            case SUBJECTS:
                showCoursesList();
                break;
            case UNITS:
                showSubjects(lastSyllabus);
                break;
            case TOPICS:
                showUnits(lastSub);
        }
    }

    @Override
    public void onSubjectClick(View view, int position) {
        showUnits(subjects.get(position));
    }

    @Override
    public void onSubjectLongClick(View view, int position) {
        //showAddUnitToSubject(subjects.get(position));
    }

    @Override
    public void onUnitClick(View view, int position) {
        showTopics(units.get(position));
    }

    @Override
    public void onCourseClick(View view, int position) {
        final Courses course = courses.get(position);
        String[] array = new String[course.availSyllabi.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = course.availSyllabi.get(i).sSem + "";
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setTitle("Select Semester");
        adb.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showSubjects(course.availSyllabi.get(which));
                dialog.dismiss();
            }
        });

        adb.setNegativeButton("Cancel", null);
        adb.show();
    }
}