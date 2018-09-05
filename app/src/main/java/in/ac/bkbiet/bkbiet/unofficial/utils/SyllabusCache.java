package in.ac.bkbiet.bkbiet.unofficial.utils;

import android.content.Context;

import java.util.ArrayList;

import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Courses;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Subject;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Topic;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Unit;
import in.ac.bkbiet.bkbiet.utils.SQLiteHandler;

import static in.ac.bkbiet.bkbiet.utils.Statics.logDebug;

/**
 * SyllabusCache Created by Ashish on 12/20/2017.
 */

public class SyllabusCache {

    private static SyllabusCache singleton;
    private SQLiteHandler db;

    private SyllabusCache() {

    }

    public static SyllabusCache getInstance() {
        if (singleton == null) {
            singleton = new SyllabusCache();
        }
        return singleton;
    }

    public ArrayList<Courses> getAllCoursesList(Context context) {
        db = new SQLiteHandler(context);
        ArrayList<Courses> courses = new ArrayList<>();
        try {
            courses = db.getAllCourses();
            for (Courses course : courses) {
                course.availSyllabi = getAllSyllabiForCourse(context, course.cName);
            }
        } catch (Exception ignored) {
        }
        db.close();
        return courses;
    }

    public ArrayList<Syllabus> getAllSyllabiForCourse(Context context, String courseName) {
        db = new SQLiteHandler(context);
        ArrayList<Syllabus> syllabi = db.getAllSyllabiForCourse(courseName);
        db.close();
        return syllabi;
    }

    public Syllabus getSyllabusById(Context context, String syllabusId) {
        db = new SQLiteHandler(context);
        Syllabus syllabus = db.getSyllabusById(syllabusId);
        db.close();
        return syllabus;
    }

    public ArrayList<Subject> getAllSubjectsForSyllabus(Context context, String syllabusId) {
        logDebug("Getting all subjects for " + syllabusId);
        ArrayList<Subject> subjects;
        db = new SQLiteHandler(context);
        subjects = db.getAllSubjectsForSyllabus(syllabusId);
        db.close();
        logDebug("returning all subjects list" + subjects);
        return subjects;
    }

    public ArrayList<Unit> getAllUnitsForSubject(Context context, String subCode) {
        logDebug("Getting all units for " + subCode);
        ArrayList<Unit> units;
        db = new SQLiteHandler(context);
        units = db.getAllUnitsForSub(subCode);
        db.close();
        return units;
    }

    public ArrayList<Unit> getAllUnitsForSubjectWithTopics(Context context, String subCode) {
        logDebug("Getting all units for " + subCode);
        ArrayList<Unit> units = new ArrayList<>();
        try {
            db = new SQLiteHandler(context);
            units = db.getAllUnitsForSub(subCode);
            db.close();
            for (Unit unit : units) {
                unit.topics = getAllTopicsForUnitAndSub(context, unit.uNumber, subCode);
            }
        } catch (Exception ignored) {
        }
        return units;
    }

    public ArrayList<Topic> getAllTopicsForUnitAndSub(Context context, int unitNo, String subCode) {
        logDebug("Getting all topics for " + subCode + "_U" + unitNo);
        db = new SQLiteHandler(context);
        ArrayList<Topic> topics;
        topics = db.getAllTopicsForUnitAndSub(unitNo, subCode);
        db.close();
        return topics;
    }

    public void saveCourse(Context context, Courses course) {
        db = new SQLiteHandler(context);
        try {
            db.saveCourse(course.cName);
            for (Syllabus syllabus : course.availSyllabi) {
                saveSyllabus(context, syllabus);
            }
        } catch (Exception ignored) {
        } finally {
            db.close();
        }
    }

    public void saveSyllabus(Context context, Syllabus syllabus) {
        db = new SQLiteHandler(context);
        try {
            db.saveSyllabus(syllabus);
        } catch (Exception ignored) {
        } finally {
            db.close();
        }
    }

    public void saveSubjectsForSyllabus(Context context, ArrayList<Subject> subjects, String syllabusId) {
        db = new SQLiteHandler(context);
        try {
            for (Subject subject : subjects) {
                db.saveSubForSyllabus(subject, syllabusId);
            }
        } catch (Exception ignored) {
        } finally {
            db.close();
        }
    }

    public void saveUnitsForSub(Context context, ArrayList<Unit> units, String subCode) {
        db = new SQLiteHandler(context);
        try {
            for (Unit unit : units) {
                db.saveUnitForSub(unit, subCode);
            }
        } catch (Exception ignored) {
        } finally {
            db.close();
        }
    }

    public void saveTopicsForUnitAndSub(Context context, ArrayList<Topic> topics, int unitNo, String subCode) {
        db = new SQLiteHandler(context);
        try {
            for (Topic topic : topics) {
                db.saveTopicForUnitAndSub(topic, unitNo, subCode);
            }
        } catch (Exception ignored) {
        } finally {
            db.close();
        }
    }

    public void clearCache(Context context) {
        db = new SQLiteHandler(context);
        try {
            db.deleteAllSyllabusRecords();
        } catch (Exception ignored) {
        } finally {
            db.close();
        }
    }
}
