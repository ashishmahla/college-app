package in.ac.bkbiet.bkbiet.unofficial.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;

/**
 * Syllabus Created by Ashish on 12/17/2017.
 */

public class Syllabus {
    public String sId;
    public int sYear;
    public int sSem;
    public String sCourse;
    @Exclude
    public ArrayList<Subject> subjects;

    public Syllabus() {
    }

    public Syllabus(String id, int year, int sem, String course) {
        sId = id;
        sYear = year;
        sSem = sem;
        sCourse = course;
    }

    public static class Topic {
        public String tId;
        public String tName;
        public int tRating;

        public Topic() {
        }

        public Topic(String name, int rating) {
            tName = name;
            tRating = rating;
        }
    }

    public static class Unit {
        public int uNumber;
        public String uName;
        public int uRatings;
        @Exclude
        public ArrayList<Topic> topics;

        public Unit() {
        }

        public Unit(int number, String name, int ratings) {
            uNumber = number;
            uName = name;
            uRatings = ratings;
        }
    }

    public static class Book {
        public String bId;
        public String bName;
        public String bAuthor;
        public String bDesc;
        public String bType;
        public int bPageCount;
        public int bRatings;

        public Book() {
        }

        public Book(String name, String author, String desc, String type, int pageCount, int ratings) {
            bName = name;
            bAuthor = author;
            bDesc = desc;
            bType = type;
            bPageCount = pageCount;
            bRatings = ratings;
        }
    }

    public static class Subject {
        public String subName;
        public String subCode;
        public int subMarks;
        public int subRatings;
        @Exclude
        public ArrayList<Unit> units;
        @Exclude
        public ArrayList<Book> books;

        public Subject() {
        }

        public Subject(String name, String code, int marks, int ratings) {
            subName = name;
            subCode = code;
            subMarks = marks;
            subRatings = ratings;
        }
    }

    public static class Courses {
        public String cName;
        public ArrayList<Syllabus> availSyllabi;

        public Courses() {
        }

        public Courses(String cName, ArrayList<Syllabus> availSyllabi) {
            this.cName = cName;
            this.availSyllabi = availSyllabi;
        }
    }
}