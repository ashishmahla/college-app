package in.ac.bkbiet.bkbiet.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import in.ac.bkbiet.bkbiet.models.Noty;
import in.ac.bkbiet.bkbiet.models.User;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Subject;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Topic;
import in.ac.bkbiet.bkbiet.unofficial.models.Syllabus.Unit;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;   // TODO: 12/26/2017 set as 2

    // Database Name
    private static final String DATABASE_NAME = "bkbiet";

    private static final String sTABLE_USER = "user";
    private static final String sUSER_ID = "user_id";
    private static final String sUSER_EMAIL = "user_email";
    private static final String sUSER_USERNAME = "user_username";
    private static final String sUSER_NAME = "user_name";
    private static final String sUSER_MOBILE_NO = "mobile_no";
    private static final String sUSER_DP_LINK = "dp_link";
    private static final String sUSER_CREATED_AT = "user_created_at";
    private static final String sUSER_TYPE = "user_type";

    /*private static final String sTABLE_STUDENT = "student";
    private static final String sSTU_COLLEGE_ID = "stu_college_id";
    private static final String sSTU_NAME = "stu_name";
    private static final String sSTU_EMAIL = "stu_email";
    private static final String sSTU_CONTACT = "stu_contact";
    private static final String sSTU_MOTHERS_NAME = "stu_mothers_name";
    private static final String sSTU_FATHERS_NAME = "stu_fathers_name";
    private static final String sSTU_FATHERS_EMAIL = "stu_fathers_email";
    private static final String sSTU_FATHERS_CONTACT = "stu_fathers_contact";
    private static final String sSTU_DOB = "stu_dob";
    private static final String sSTU_ADDRESS = "stu_address";*/


    // Notification
    private static final String sTABLE_NOTY = "noty";
    private static final String sNOTY_ID = "noty_id";
    private static final String sNOTY_NID = "noty_d";
    private static final String sNOTY_TITLE = "noty_title";
    private static final String sNOTY_BODY = "noty_body";
    private static final String sNOTY_COLOR = "noty_color";
    private static final String sNOTY_SENDER_ID = "noty_sender_id";
    private static final String sNOTY_SENDER = "noty_sender";
    private static final String sNOTY_RECEIVED_AT = "noty_received_at";
    private static final String sNOTY_SENT_AT = "noty_sent_at";
    private static final String sNOTY_IS_READ = "is_read";
    private static final String sNOTY_READ_AT = "read_at";

    // Prefs
    private static final String sTABLE_PREFS = "table_prefs";
    private static final String sPREF_KEY = "pref_key";
    private static final String sPREF_VALUE = "pref_value";

    // Courses
    private static final String sTABLE_COURSES = "courses";
    private static final String sCOURSE_NAME = "course_name";

    // Syllabus
    private static final String sTABLE_SYLLABUS = "syllabus";
    private static final String sSYLLABUS_ID = "syllabus_id";
    private static final String sSYLLABUS_YEAR = "syllabus_year";
    private static final String sSYLLABUS_SEM = "syllabus_sem";
    private static final String sSYLLABUS_COURSE = "syllabus_course";

    //Subjects
    private static final String sTABLE_SUB = "table_sub";
    private static final String sSUB_SYLLABUS_ID = "sub_syllabus_id";
    private static final String sSUB_CODE = "sub_code";
    private static final String sSUB_NAME = "sub_name";
    private static final String sSUB_MARKS = "sub_marks";
    private static final String sSUB_RATINGS = "sub_ratings";

    //Units
    private static final String sTABLE_UNITS = "table_units";
    private static final String sUNIT_SUB_CODE = "unit_sub_code";
    private static final String sUNIT_NO = "unit_no";
    private static final String sUNIT_NAME = "unit_name";
    private static final String sUNIT_RATINGS = "unit_ratings";

    //topics
    private static final String sTABLE_TOPICS = "table_topics";
    private static final String sTOPIC_UNIT_NO = "topic_unit_no";
    private static final String sTOPIC_SUB_CODE = "topic_sub_code";
    private static final String sTOPIC_ID = "topic_id";
    private static final String sTOPIC_NAME = "topic_name";
    private static final String sTOPIC_RATINGS = "topic_ratings";

    private static final String CREATE_NOTY_TABLE = "create table " + sTABLE_NOTY + " ("
            + sNOTY_ID + " integer primary key, "
            + sNOTY_NID + " text unique, "
            + sNOTY_TITLE + " text, "
            + sNOTY_BODY + " text, "
            + sNOTY_COLOR + " text, "
            + sNOTY_SENDER_ID + " text, "
            + sNOTY_SENDER + " text, "
            + sNOTY_RECEIVED_AT + " text, "
            + sNOTY_SENT_AT + " text, "
            + sNOTY_IS_READ + " integer default 0, "
            + sNOTY_READ_AT + " text "
            + ")";

    private static final String CREATE_COURSES_TABLE = "create table " + sTABLE_COURSES + " (" +
            sCOURSE_NAME + " text primary key)";

    private static final String CREATE_SYLLABUS_TABLE = "create table " + sTABLE_SYLLABUS + " (" +
            sSYLLABUS_ID + " text primary key, " +
            sSYLLABUS_YEAR + " integer, " +
            sSYLLABUS_SEM + " integer, " +
            sSYLLABUS_COURSE + " text)";

    private static final String CREATE_SUB_TABLE = "create table " + sTABLE_SUB + " ( " +
            sSUB_SYLLABUS_ID + " text, " +
            sSUB_CODE + " text primary key, " +
            sSUB_NAME + " text, " +
            sSUB_MARKS + " integer, " +
            sSUB_RATINGS + " integer)";

    private static final String CREATE_UNIT_TABLE = "create table " + sTABLE_UNITS + " ( " +
            sUNIT_SUB_CODE + " text, " +
            sUNIT_NO + " integer, " +
            sUNIT_NAME + " text, " +
            sUNIT_RATINGS + " integer, " +
            "primary key (" +
            sUNIT_SUB_CODE + "," +
            sUNIT_NO + "))";

    private static final String CREATE_TOPICS_TABLE = "create table " + sTABLE_TOPICS + " ( " +
            sTOPIC_UNIT_NO + " integer, " +
            sTOPIC_SUB_CODE + " text, " +
            sTOPIC_ID + " text primary key, " +
            sTOPIC_NAME + " text, " +
            sTOPIC_RATINGS + " integer)";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating TablesCo
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_USER_TABLE = "create table " + sTABLE_USER + "(" +
                sUSER_ID + " integer primary key, " + sUSER_USERNAME + " text, " +
                sUSER_NAME + " text, " + sUSER_EMAIL + " text, " +
                sUSER_MOBILE_NO + " text, " + sUSER_DP_LINK + " text, " +
                sUSER_TYPE + " integer, " + sUSER_CREATED_AT + " text)";

        db.execSQL(CREATE_USER_TABLE);

        db.execSQL(CREATE_NOTY_TABLE);

        //      db.execSQL(CREATE_STU_TABLE);

        String CREATE_PREF_TABLE = "create table " + sTABLE_PREFS + "(" +
                sPREF_KEY + " text primary key, " + sPREF_VALUE + " text)";
        db.execSQL(CREATE_PREF_TABLE);

        db.execSQL(CREATE_SYLLABUS_TABLE);
        db.execSQL(CREATE_SUB_TABLE);
        db.execSQL(CREATE_UNIT_TABLE);
        db.execSQL(CREATE_TOPICS_TABLE);
        db.execSQL(CREATE_COURSES_TABLE);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //provide backward compatibility
        switch (oldVersion) {
            // after version v17.12.7
            case 1:
                db.execSQL("DROP TABLE IF EXISTS " + sTABLE_PREFS);
                final String CREATE_PREF_TABLE = "create table " + sTABLE_PREFS + "(" +
                        sPREF_KEY + " text primary key, " + sPREF_VALUE + " text)";

                db.execSQL(CREATE_PREF_TABLE);
                db.execSQL(CREATE_SYLLABUS_TABLE);
                db.execSQL(CREATE_SUB_TABLE);
                db.execSQL(CREATE_UNIT_TABLE);
                db.execSQL(CREATE_TOPICS_TABLE);
                db.execSQL(CREATE_COURSES_TABLE);
            case 2:
                db.execSQL("DROP TABLE IF EXISTS " + sTABLE_NOTY);
                db.execSQL(CREATE_NOTY_TABLE);
        }
    }

    public long addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(sUSER_USERNAME, user.getUsername());
        values.put(sUSER_NAME, user.getName());
        values.put(sUSER_EMAIL, user.getEmail());
        values.put(sUSER_MOBILE_NO, user.getMobileNo());
        values.put(sUSER_DP_LINK, user.getDpLink());
        values.put(sUSER_TYPE, user.getType());
        values.put(sUSER_CREATED_AT, user.getCreatedAt());

        SQLiteDatabase db = this.getWritableDatabase();
        long insertId = db.insert(sTABLE_USER, null, values);
        close();
        Log.i(TAG, "ID : " + insertId + "[ Added user with username : " + user.getUsername() + " ]");
        return insertId;
    }

    /*public long addStudent(Student student) {
        ContentValues values = new ContentValues();
        values.put(sSTU_COLLEGE_ID, student.getCollegeId());
        values.put(sSTU_NAME, student.getName());
        values.put(sSTU_EMAIL, student.getEmail());
        values.put(sSTU_CONTACT, student.getContact());
        values.put(sSTU_MOTHERS_NAME, student.getMothersName());
        values.put(sSTU_FATHERS_NAME, student.getFathersName());
        values.put(sSTU_FATHERS_EMAIL, student.getFathersEmail());
        values.put(sSTU_FATHERS_CONTACT, student.getFathersContact());
        values.put(sSTU_DOB, student.getDob());
        values.put(sSTU_ADDRESS, student.getAddress());

        SQLiteDatabase db = this.getWritableDatabase();
        long insertId = db.insert(sTABLE_STUDENT, null, values);
        close();
        Log.i(TAG, "ID : " + insertId + "[ Added student with name : " + student.getName() + " ]");
        return insertId;
    }*/

    public long addNoty(Noty noty) {
        ContentValues values = new ContentValues();
        values.put(sNOTY_NID, noty.getSender());
        values.put(sNOTY_TITLE, noty.getTitle());
        values.put(sNOTY_BODY, noty.getBody());
        values.put(sNOTY_COLOR, noty.getSender());
        values.put(sNOTY_SENDER_ID, noty.getSenderId());
        values.put(sNOTY_SENDER, noty.getSender());
        values.put(sNOTY_RECEIVED_AT, noty.getReceivedAt());
        values.put(sNOTY_SENT_AT, noty.getSentAt());
        values.put(sNOTY_READ_AT, noty.isRead() ? 1 : 0);
        values.put(sNOTY_READ_AT, noty.getReadAt());

        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(sTABLE_NOTY, null, values);
        db.close();
        Log.d(TAG, "New notification inserted into sqlite : " + id);
        return id;
    }

    public User getCurrentUser() {
        User currUser = new User();
        currUser.setId(-1);
        String selectQuery = "select * from " + sTABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            currUser.setId(cursor.getInt(cursor.getColumnIndex(sUSER_ID)));
            currUser.setUsername(cursor.getString(cursor.getColumnIndex(sUSER_USERNAME)));
            currUser.setName(cursor.getString(cursor.getColumnIndex(sUSER_NAME)));
            currUser.setEmail(cursor.getString(cursor.getColumnIndex(sUSER_EMAIL)));
            currUser.setMobileNo(cursor.getString(cursor.getColumnIndex(sUSER_MOBILE_NO)));
            currUser.setDpLink(cursor.getString(cursor.getColumnIndex(sUSER_DP_LINK)));
            currUser.setType(cursor.getInt(cursor.getColumnIndex(sUSER_TYPE)));
            currUser.setCreatedAt(cursor.getString(cursor.getColumnIndex(sUSER_CREATED_AT)));

            Log.i(TAG, "Fetching user with username : " + currUser.getUsername());
        } else {
            Log.i(TAG, "NO USER FOUND IN DATABASE");
        }

        cursor.close();
        db.close();

        return currUser;
    }

    /*public Student getCurrentStudent() {
        Student currStudent = new Student();
        currStudent.setCollegeId("-1");
        String selectQuery = "select * from " + sTABLE_STUDENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            currStudent.setCollegeId(cursor.getString(cursor.getColumnIndex(sSTU_COLLEGE_ID)));
            currStudent.setName(cursor.getString(cursor.getColumnIndex(sSTU_NAME)));
            currStudent.setEmail(cursor.getString(cursor.getColumnIndex(sSTU_EMAIL)));
            currStudent.setContact(cursor.getString(cursor.getColumnIndex(sSTU_CONTACT)));
            currStudent.setMothersName(cursor.getString(cursor.getColumnIndex(sSTU_MOTHERS_NAME)));
            currStudent.setFathersName(cursor.getString(cursor.getColumnIndex(sSTU_FATHERS_NAME)));
            currStudent.setFathersEmail(cursor.getString(cursor.getColumnIndex(sSTU_FATHERS_EMAIL)));
            currStudent.setFathersContact(cursor.getString(cursor.getColumnIndex(sSTU_FATHERS_CONTACT)));
            currStudent.setDob(cursor.getString(cursor.getColumnIndex(sSTU_DOB)));
            currStudent.setAddress(cursor.getString(cursor.getColumnIndex(sSTU_ADDRESS)));

            Log.i(TAG, "Fetching student with collegeId : " + currStudent.getCollegeId());
        } else {
            Log.i(TAG, "NO USER FOUND IN DATABASE");
        }

        cursor.close();
        db.close();

        return currStudent;
    }*/

    public boolean deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleteCount = db.delete(sTABLE_USER, "1", null);
        db.close();
        Log.i(TAG, "Deleted " + deleteCount + " user from table");
        return deleteCount > 0;
    }

    /*public boolean deleteStudent() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleteCount = db.delete(sTABLE_STUDENT, "1", null);
        db.close();
        Log.i(TAG, "Deleted " + deleteCount + " student from table");
        return deleteCount > 0;
    }*/

    public ArrayList<Noty> getAllNotifications() {
        ArrayList<Noty> noties = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + sTABLE_NOTY;

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Uv.NOTIFICATION_DATE_FORMAT, Locale.ENGLISH);
        String readAt = dateFormat.format(date);

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0) {
                cursor.moveToLast();
                while (!cursor.isBeforeFirst()) {
                    Noty noty = new Noty();

                    noty.setId(cursor.getInt(cursor.getColumnIndex(sNOTY_ID)));
                    noty.setnId(cursor.getString(cursor.getColumnIndex(sNOTY_NID)));
                    noty.setTitle(cursor.getString(cursor.getColumnIndex(sNOTY_TITLE)));
                    noty.setBody(cursor.getString(cursor.getColumnIndex(sNOTY_BODY)));
                    noty.setSenderId(cursor.getString(cursor.getColumnIndex(sNOTY_SENDER_ID)));
                    noty.setSender(cursor.getString(cursor.getColumnIndex(sNOTY_SENDER)));
                    noty.setReceivedAt(cursor.getString(cursor.getColumnIndex(sNOTY_RECEIVED_AT)));
                    noty.setSentAt(cursor.getString(cursor.getColumnIndex(sNOTY_SENT_AT)));
                    noty.setIsRead(cursor.getInt(cursor.getColumnIndex(sNOTY_IS_READ)) == 1);
                    noty.setReadAt(cursor.getString(cursor.getColumnIndex(sNOTY_READ_AT)));

                    if (!noty.isRead()) {
                        noty.setReadAt(readAt);
                    }
                    noties.add(noty);
                    cursor.moveToPrevious();
                }
            }
            cursor.close();
            db.close();
        } catch (Exception ignored) {
        }
        Log.d(TAG, "Retrieving all notifications from sqlite ( Count = " + noties.size() + " )");
        return noties;
    }

    public int getUnreadNotyCount() {
        String query = "SELECT  * FROM " + sTABLE_NOTY + " where " + sNOTY_IS_READ + " = 0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        Log.i(TAG, "Total unread noties : " + count);
        return count;
    }

    public void markAllAsRead() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy hh:mm", Locale.ENGLISH);
        String readAt = dateFormat.format(date);

        ContentValues values = new ContentValues();
        values.put(sNOTY_IS_READ, 1);
        values.put(sNOTY_READ_AT, readAt);

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            int update = db.update(sTABLE_NOTY, values, sNOTY_IS_READ + "=?", new String[]{"0"});
            Log.i(TAG, "Marked " + update + " as read at " + readAt);
            db.close();
        } catch (Exception ignored) {
            Log.i(TAG, "Unable to update");
        }
    }

    public boolean isNotyPresent(Noty noty) {
        String query = "select * from " + sTABLE_NOTY + "where " + sNOTY_SENT_AT +
                " = '" + noty.getSentAt() + "' and " + sNOTY_SENDER + " = '" + noty.getSender() + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public Noty getNotyWithId(int id) {
        Noty noty = new Noty();
        String selectQuery = "SELECT  * FROM " + sTABLE_NOTY + " where " + sNOTY_ID + " = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            noty.setId(cursor.getInt(cursor.getColumnIndex(sNOTY_ID)));
            noty.setnId(cursor.getString(cursor.getColumnIndex(sNOTY_NID)));
            noty.setTitle(cursor.getString(cursor.getColumnIndex(sNOTY_TITLE)));
            noty.setBody(cursor.getString(cursor.getColumnIndex(sNOTY_BODY)));
            noty.setSenderId(cursor.getString(cursor.getColumnIndex(sNOTY_SENDER_ID)));
            noty.setSender(cursor.getString(cursor.getColumnIndex(sNOTY_SENDER)));
            noty.setReceivedAt(cursor.getString(cursor.getColumnIndex(sNOTY_RECEIVED_AT)));
            noty.setSentAt(cursor.getString(cursor.getColumnIndex(sNOTY_SENT_AT)));
            noty.setIsRead(cursor.getInt(cursor.getColumnIndex(sNOTY_IS_READ)) == 1);
            noty.setReadAt(cursor.getString(cursor.getColumnIndex(sNOTY_READ_AT)));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching noty from sqlite where id = " + id);
        return noty;
    }

    public boolean deleteNotyWithId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleteCount = db.delete(sTABLE_NOTY, sNOTY_ID + "= ?", new String[]{id + ""});
        db.close();
        Log.i(TAG, "deleted notification with id= " + id);
        return deleteCount > 0;
    }

    public int resetNotyTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleteCount = db.delete(sTABLE_NOTY, "1", null);
        db.close();
        Log.i(TAG, "Deleted ALL " + deleteCount + " notifications from table");
        return deleteCount;
    }

    public long createPref(String prefKey, String prefValue) {
        ContentValues values = new ContentValues();
        values.put(sPREF_KEY, prefKey);
        values.put(sPREF_VALUE, prefValue);

        SQLiteDatabase db = this.getWritableDatabase();
        long insertId = db.insert(sTABLE_PREFS, null, values);
        close();
        Log.i(TAG, "ID : " + insertId + "[ Added pref with key : " + prefKey + ", value : " + prefValue + " ]");
        return insertId;
    }

    public long setPref(String prefKey, String prefValue) {
        ContentValues values = new ContentValues();
        values.put(sPREF_VALUE, prefValue);

        SQLiteDatabase db = this.getWritableDatabase();
        long valuesChanged = db.update(sTABLE_PREFS, values, sPREF_KEY + " = ?", new String[]{String.valueOf(prefKey)});
        if (valuesChanged == 0) {
            createPref(prefKey, prefValue);
        }
        close();
        Log.i(TAG, "Values Updated : " + valuesChanged + "[ Updated pref with key : " + prefKey + ", value : " + prefValue + " ]");
        return valuesChanged;
    }

    public String getPref(String prefKey) {
        String prefValue = null;
        String selectQuery = "SELECT  * FROM " + sTABLE_PREFS + " where " + sPREF_KEY + " = " + prefKey;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            prefValue = cursor.getString(1);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching pref : (" + prefKey + " , " + prefValue + ")");
        return prefValue;
    }

    public HashMap<String, String> getAllSettings() {
        HashMap<String, String> hashMap = new HashMap<>();

        String selectQuery = "SELECT  * FROM " + sTABLE_PREFS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String key;
            String value;

            while (!cursor.isAfterLast()) {
                key = cursor.getString(0);
                value = cursor.getString(1);

                cursor.moveToNext();
                hashMap.put(key, value);
            }
        }
        cursor.close();
        db.close();

        return hashMap;
    }

    private void logInfo(String message) {
        Log.i(TAG, message);
    }

    // syllabus
    public long saveCourse(String course) {
        ContentValues values = new ContentValues();
        values.put(sCOURSE_NAME, course);

        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(sTABLE_COURSES, null, values);
        close();
        logInfo("Added new syllabus " + " [" + course + "] " + "at id " + id);
        return id;
    }

    public ArrayList<Syllabus.Courses> getAllCourses() {
        ArrayList<Syllabus.Courses> courses = new ArrayList<>();

        String selectQuery = "select * from " + sTABLE_COURSES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Syllabus.Courses sCourse = new Syllabus.Courses();
            sCourse.cName = cursor.getString(cursor.getColumnIndex(sCOURSE_NAME));

            courses.add(sCourse);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return courses;
    }

    public long saveSyllabus(Syllabus syllabus) {
        ContentValues values = new ContentValues();
        values.put(sSYLLABUS_ID, syllabus.sId);
        values.put(sSYLLABUS_YEAR, syllabus.sYear);
        values.put(sSYLLABUS_SEM, syllabus.sSem);
        values.put(sSYLLABUS_COURSE, syllabus.sCourse);

        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(sTABLE_SYLLABUS, null, values);
        close();
        logInfo("Added new syllabus " + " [" + syllabus.sId + "] " + "at id " + id);
        return id;
    }

    public ArrayList<Syllabus> getAllSyllabiForCourse(String courseName) {
        ArrayList<Syllabus> syllabi = new ArrayList<>();

        String selectQuery = "select * from " + sTABLE_SYLLABUS + " where " + sSYLLABUS_COURSE + " = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{courseName});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Syllabus syllabus = new Syllabus();
            syllabus.sId = cursor.getString(cursor.getColumnIndex(sSYLLABUS_ID));
            syllabus.sYear = cursor.getInt(cursor.getColumnIndex(sSYLLABUS_YEAR));
            syllabus.sSem = cursor.getInt(cursor.getColumnIndex(sSYLLABUS_SEM));
            syllabus.sCourse = cursor.getString(cursor.getColumnIndex(sSYLLABUS_COURSE));

            syllabi.add(syllabus);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return syllabi;
    }

    public long saveSubForSyllabus(Subject subject, String syllabusId) {
        ContentValues values = new ContentValues();
        values.put(sSUB_SYLLABUS_ID, syllabusId);
        values.put(sSUB_CODE, subject.subCode);
        values.put(sSUB_NAME, subject.subName);
        values.put(sSUB_MARKS, subject.subMarks);
        values.put(sSUB_RATINGS, subject.subRatings);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(sTABLE_SUB, null, values);
        db.close();

        logInfo("Saved subject [" + subject.subCode + "] to syllabus " + syllabusId + " at " + id);
        return id;
    }

    public ArrayList<Subject> getAllSubjectsForSyllabus(String syllabusId) {
        ArrayList<Subject> subjects = new ArrayList<>();

        String selectQuery = "select * from " + sTABLE_SUB + " where " + sSUB_SYLLABUS_ID + " = ? ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{syllabusId});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Subject subject = new Subject();
            subject.subCode = cursor.getString(cursor.getColumnIndex(sSUB_CODE));
            subject.subName = cursor.getString(cursor.getColumnIndex(sSUB_NAME));
            subject.subMarks = cursor.getInt(cursor.getColumnIndex(sSUB_MARKS));
            subject.subRatings = cursor.getInt(cursor.getColumnIndex(sSUB_RATINGS));

            subjects.add(subject);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return subjects;
    }

    public long saveUnitForSub(Unit unit, String subCode) {
        ContentValues values = new ContentValues();
        values.put(sUNIT_SUB_CODE, subCode);
        values.put(sUNIT_NO, unit.uNumber);
        values.put(sUNIT_NAME, unit.uName);
        values.put(sUNIT_RATINGS, unit.uRatings);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(sTABLE_UNITS, null, values);
        db.close();

        logInfo("Saved unit [" + unit.uName + "] to subject " + subCode + " at " + id);
        return id;
    }

    public ArrayList<Unit> getAllUnitsForSub(String subCode) {
        ArrayList<Unit> units = new ArrayList<>();

        String selectQuery = "select * from " + sTABLE_UNITS + " where " + sUNIT_SUB_CODE + " = ? ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{subCode});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Unit unit = new Unit();
            unit.uNumber = cursor.getInt(cursor.getColumnIndex(sUNIT_NO));
            unit.uName = cursor.getString(cursor.getColumnIndex(sUNIT_NAME));
            unit.uRatings = cursor.getInt(cursor.getColumnIndex(sUNIT_RATINGS));

            units.add(unit);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return units;
    }

    public long saveTopicForUnitAndSub(Topic topic, int unitNo, String subCode) {
        ContentValues values = new ContentValues();
        values.put(sTOPIC_UNIT_NO, unitNo);
        values.put(sTOPIC_SUB_CODE, subCode);
        values.put(sTOPIC_ID, topic.tId);
        values.put(sTOPIC_NAME, topic.tName);
        values.put(sTOPIC_RATINGS, topic.tRating);

        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(sTABLE_TOPICS, null, values);
        db.close();

        logInfo("Saved topic [" + topic.tId + "] to " + subCode + "_U" + unitNo + " at " + id);
        return id;
    }

    public ArrayList<Topic> getAllTopicsForUnitAndSub(int unitNo, String subCode) {
        ArrayList<Topic> topics = new ArrayList<>();

        String selectQuery = "select * from " + sTABLE_TOPICS +
                " where " + sTOPIC_SUB_CODE + " = ? and " + sTOPIC_UNIT_NO + " = ? ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{subCode, String.valueOf(unitNo)});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Topic topic = new Topic();
            topic.tName = cursor.getString(cursor.getColumnIndex(sTOPIC_NAME));
            topic.tRating = cursor.getInt(cursor.getColumnIndex(sTOPIC_RATINGS));

            topics.add(topic);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return topics;
    }

    public void deleteAllSyllabusRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(sTABLE_COURSES, "1", null);
        db.delete(sTABLE_SYLLABUS, "1", null);
        db.delete(sTABLE_SUB, "1", null);
        db.delete(sTABLE_UNITS, "1", null);
        db.delete(sTABLE_TOPICS, "1", null);
    }

    public Syllabus getSyllabusById(String syllabusId) {
        Syllabus syllabus = null;
        String selectQuery = "select * from " + sTABLE_SYLLABUS + " where " + sSYLLABUS_ID + " = ?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{syllabusId});

        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            syllabus = new Syllabus();
            syllabus.sId = cursor.getString(cursor.getColumnIndex(sSYLLABUS_ID));
            syllabus.sYear = cursor.getInt(cursor.getColumnIndex(sSYLLABUS_YEAR));
            syllabus.sSem = cursor.getInt(cursor.getColumnIndex(sSYLLABUS_SEM));
            syllabus.sCourse = cursor.getString(cursor.getColumnIndex(sSYLLABUS_COURSE));
        }
        cursor.close();
        db.close();

        return syllabus;
    }
}