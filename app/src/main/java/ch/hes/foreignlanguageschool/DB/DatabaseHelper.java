package ch.hes.foreignlanguageschool.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Database Version
    private static final int DATABASE_VERSION = 1;
    //Database Name
    private static final String DATABASE_NAME = "DBForeignSchool";
    //Singleton
    private static DatabaseHelper db;
    //Logcat tag
    private final String LOG = "DatabaseHelper";
    //Table Names
    private final String TABLE_ASSIGNEMENT = "Assignment";
    private final String TABLE_STUDENT = "Student";
    private final String TABLE_TEACHER = "Teacher";
    private final String TABLE_LECTURE = "Lecture";
    private final String TABLE_DAY = "Day";
    private final String TABLE_LECTUREDATE = "LectureDate";
    private final String TABLE_LECTURESTUDENT = "LectureStudent";

    //Common column names
    private final String KEY_ID = "id";



    private final String CLOUD_ID = "Cloud_id";

    private final String IMAGE_NAME = "ImageName";

    //Assignment Table - column names
    private final String ASSIGNMENT_TITLE = "Title";
    private final String ASSIGNMENT_DESCRIPTION = "Description";
    private final String ASSIGNMENT_DATE = "Date";
    private final String ASSIGNMENT_FKTEACHER = "idTeacher";
    private final String ASSIGNMENT_ADDTOCALENDAR = "Added_To_Calendar";

    //Student Table - column names
    private final String STUDENT_FIRSTNAME = "FirstName";
    private final String STUDENT_LASTNAME = "LastName";
    private final String STUDENT_ADDRESS = "Address";
    private final String STUDENT_COUNTRY = "Country";
    private final String STUDENT_MAIL = "Mail";
    private final String STUDENT_STARTDATE = "StartDate";
    private final String STUDENT_ENDDATE = "EndDate";

    public final String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENT + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + STUDENT_FIRSTNAME + " TEXT NOT NULL, "
            + STUDENT_LASTNAME + " TEXT NOT NULL, "
            + STUDENT_ADDRESS + " TEXT, "
            + STUDENT_COUNTRY + " TEXT NOT NULL, "
            + STUDENT_MAIL + " TEXT NOT NULL, "
            + STUDENT_STARTDATE + " TEXT NOT NULL, "
            + STUDENT_ENDDATE + " TEXT, "
            + IMAGE_NAME + " TEXT, "
            + CLOUD_ID + " INTEGER);";

    //Lecture Table - column names
    private final String LECTURE_NAME = "Name";
    private final String LECTURE_DESCRIPTION = "Description";
    private final String LECTURE_FKTEACHER = "idTeacher";
    //LectureStudent Table - column names
    private final String LECTURESTUDENT_FKLECTURE = "idLecture";
    private final String LECTURESTUDENT_FKSTUDENT = "idStudent";
    //Teacher Table - column names
    private final String TEACHER_FIRSTNAME = "FirstName";
    private final String TEACHER_LASTNAME = "LastName";
    private final String TEACHER_MAIL = "Mail";
    //Day Table - column names
    private final String DAY_NAME = "Name";
    //LectureDate Table - column names
    private final String LECTUREDATE_FKLECTURE = "idLecture";
    private final String LECTUREDATE_FKDAY = "idDay";
    private final String LECTUREDATE_STARTTIME = "StartTime";
    private final String LECTUREDATE_ENDTIME = "EndTime";
    private final String CREATE_TEACHER_TABLE = "CREATE TABLE " + TABLE_TEACHER + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TEACHER_FIRSTNAME + " TEXT NOT NULL, "
            + TEACHER_LASTNAME + " TEXT NOT NULL, "
            + TEACHER_MAIL + " TEXT NOT NULL, "
            + IMAGE_NAME + " TEXT);";

    private final String CREATE_LECTURE_TABLE = "CREATE TABLE " + TABLE_LECTURE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LECTURE_NAME + " TEXT NOT NULL, "
            + LECTURE_DESCRIPTION + " TEXT NOT NULL, "
            + IMAGE_NAME + " TEXT NOT NULL, "
            + LECTURE_FKTEACHER + " TEXT NOT NULL, "
            + CLOUD_ID + " INTEGER, "
            + "FOREIGN KEY(" + LECTURE_FKTEACHER + ") REFERENCES " + TABLE_TEACHER + "(" + KEY_ID + "));";

    private final String CREATE_ASSIGNMENT_TABLE = "CREATE TABLE " + TABLE_ASSIGNEMENT + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ASSIGNMENT_TITLE + " TEXT NOT NULL, "
            + ASSIGNMENT_DESCRIPTION + " TEXT, "
            + ASSIGNMENT_DATE + " TEXT, "
            + IMAGE_NAME + " TEXT NOT NULL, "
            + ASSIGNMENT_FKTEACHER + " TEXT NOT NULL, "
            + ASSIGNMENT_ADDTOCALENDAR + " INTEGER NOT NULL, "
            + CLOUD_ID + " INTEGER, "
            + "FOREIGN KEY(" + ASSIGNMENT_FKTEACHER + ") REFERENCES " + TABLE_TEACHER + "(" + KEY_ID + "));";

    private final String CREATE_DAY_TABLE = "CREATE TABLE " + TABLE_DAY + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DAY_NAME + " TEXT NOT NULL );";


    private final String CREATE_LECTURESTUDENT_TABLE = "CREATE TABLE " + TABLE_LECTURESTUDENT + "("
            + LECTURESTUDENT_FKLECTURE + " INTEGER NOT NULL, "
            + LECTURESTUDENT_FKSTUDENT + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + LECTURESTUDENT_FKLECTURE + ") REFERENCES " + TABLE_LECTURE + "(" + KEY_ID + "), "
            + "FOREIGN KEY(" + LECTURESTUDENT_FKSTUDENT + ") REFERENCES " + TABLE_STUDENT + "(" + KEY_ID + "), "
            + "PRIMARY KEY(" + LECTURESTUDENT_FKLECTURE + "," + LECTURESTUDENT_FKSTUDENT + "));";


    private final String CREATE_LECTUREDATE_TABLE = "CREATE TABLE " + TABLE_LECTUREDATE + "("
            + LECTUREDATE_FKLECTURE + " INTEGER NOT NULL, "
            + LECTUREDATE_FKDAY + " INTEGER NOT NULL, "
            + LECTUREDATE_STARTTIME + " TEXT NOT NULL, "
            + LECTUREDATE_ENDTIME + " TEXT NOT NULL, "
            + "FOREIGN KEY(" + LECTUREDATE_FKLECTURE + ") REFERENCES " + TABLE_LECTURE + "(" + KEY_ID + "), "
            + "FOREIGN KEY(" + LECTUREDATE_FKDAY + ") REFERENCES " + TABLE_DAY + "(" + KEY_ID + "), "
            + "PRIMARY KEY(" + LECTUREDATE_FKLECTURE + "," + LECTUREDATE_FKDAY + "));";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (db == null) {
            db = new DatabaseHelper(context);
            return db;
        }

        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_TEACHER_TABLE);
        db.execSQL(CREATE_LECTURE_TABLE);
        db.execSQL(CREATE_ASSIGNMENT_TABLE);
        db.execSQL(CREATE_DAY_TABLE);
        db.execSQL(CREATE_LECTUREDATE_TABLE);
        db.execSQL(CREATE_LECTURESTUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //If we want to modify the db, we delete the old one and recreate a new one
        db.execSQL("DROP TABLE " + TABLE_STUDENT + ", " + TABLE_TEACHER + ", " + TABLE_LECTURE + ", " +
                TABLE_ASSIGNEMENT + ", " + TABLE_DAY + ", " + TABLE_LECTUREDATE + ", " + TABLE_LECTURESTUDENT + ";");
        onCreate(db);
    }


    // all getter and setter
    public String getTableAssignement() {
        return TABLE_ASSIGNEMENT;
    }

    public String getTableStudent() {
        return TABLE_STUDENT;
    }

    public String getTableTeacher() {
        return TABLE_TEACHER;
    }

    public String getTableLecture() {
        return TABLE_LECTURE;
    }

    public String getTableDay() {
        return TABLE_DAY;
    }

    public String getTableLecturedate() {
        return TABLE_LECTUREDATE;
    }

    public String getTableLecturestudent() {
        return TABLE_LECTURESTUDENT;
    }

    public String getKeyId() {
        return KEY_ID;
    }

    public String getASSIGNMENT_TITLE() {
        return ASSIGNMENT_TITLE;
    }

    public String getASSIGNMENT_DESCRIPTION() {
        return ASSIGNMENT_DESCRIPTION;
    }

    public String getASSIGNMENT_DATE() {
        return ASSIGNMENT_DATE;
    }

    public String getASSIGNMENT_FKTEACHER() {
        return ASSIGNMENT_FKTEACHER;
    }

    public String getSTUDENT_FIRSTNAME() {
        return STUDENT_FIRSTNAME;
    }

    public String getSTUDENT_LASTNAME() {
        return STUDENT_LASTNAME;
    }

    public String getSTUDENT_ADDRESS() {
        return STUDENT_ADDRESS;
    }

    public String getSTUDENT_COUNTRY() {
        return STUDENT_COUNTRY;
    }

    public String getSTUDENT_MAIL() {
        return STUDENT_MAIL;
    }

    public String getSTUDENT_STARTDATE() {
        return STUDENT_STARTDATE;
    }

    public String getSTUDENT_ENDDATE() {
        return STUDENT_ENDDATE;
    }

    public String getLECTURE_NAME() {
        return LECTURE_NAME;
    }

    public String getLECTURE_DESCRIPTION() {
        return LECTURE_DESCRIPTION;
    }

    public String getLECTURE_FKTEACHER() {
        return LECTURE_FKTEACHER;
    }

    public String getLECTURESTUDENT_FKLECTURE() {
        return LECTURESTUDENT_FKLECTURE;
    }

    public String getLECTURESTUDENT_FKSTUDENT() {
        return LECTURESTUDENT_FKSTUDENT;
    }

    public String getTEACHER_FIRSTNAME() {
        return TEACHER_FIRSTNAME;
    }

    public String getTEACHER_LASTNAME() {
        return TEACHER_LASTNAME;
    }

    public String getTEACHER_MAIL() {
        return TEACHER_MAIL;
    }

    public String getDAY_NAME() {
        return DAY_NAME;
    }

    public String getLECTUREDATE_FKLECTURE() {
        return LECTUREDATE_FKLECTURE;
    }

    public String getLECTUREDATE_FKDAY() {
        return LECTUREDATE_FKDAY;
    }

    public String getLECTUREDATE_STARTTIME() {
        return LECTUREDATE_STARTTIME;
    }

    public String getLECTUREDATE_ENDTIME() {
        return LECTUREDATE_ENDTIME;
    }

    public String getIMAGE_NAME() {
        return IMAGE_NAME;
    }

    public String getASSIGNMENT_ADDTOCALENDAR() {
        return ASSIGNMENT_ADDTOCALENDAR;
    }

    public String getCLOUD_ID() {
        return CLOUD_ID;
    }
}
