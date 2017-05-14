package ch.hes.foreignlanguageschool.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.patrickclivaz.myapplication.backend.assignmentApi.model.Teacher;

import java.util.ArrayList;
import java.util.List;

import ch.hes.foreignlanguageschool.Assignment;
import ch.hes.foreignlanguageschool.AssignmentAsyncTask;
import ch.hes.foreignlanguageschool.Student;
import ch.hes.foreignlanguageschool.StudentAsyncTask;

/**
 * Created by patrickclivaz on 11.04.17.
 */

public class DBStudent {

    private DatabaseHelper db;

    public DBStudent(DatabaseHelper db) {
        this.db = db;
    }

    /**
     * Insert all values for teacher
     *
     * @param firstName
     * @param lastName
     * @param address
     * @param country
     * @param mail
     * @param startDate
     * @param endDate
     */
    public void insertValues(String firstName, String lastName, String address, String country, String mail, String startDate, String endDate) {

        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.getSTUDENT_FIRSTNAME(), firstName);
        values.put(db.getSTUDENT_LASTNAME(), lastName);
        values.put(db.getSTUDENT_ADDRESS(), address);
        values.put(db.getSTUDENT_COUNTRY(), country);
        values.put(db.getSTUDENT_MAIL(), mail);
        values.put(db.getSTUDENT_STARTDATE(), startDate);
        values.put(db.getSTUDENT_ENDDATE(), endDate);
        values.put(db.getIMAGE_NAME(), "student_icon");

        sql.insert(db.getTableStudent(), null, values);

    }

    /**
     * get a student by id
     *
     * @param idStudent
     * @return
     */
    public Student getStudentById(int idStudent) {
        SQLiteDatabase sql = db.getWritableDatabase();

        Student student = new Student();
        String selectQuery = "SELECT * FROM " + db.getTableStudent() + " WHERE " + db.getKeyId() + " = " + idStudent;

        Cursor cursor = sql.rawQuery(selectQuery, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        student.setId(Integer.parseInt(cursor.getString(0)));
        student.setFirstName(cursor.getString(1));
        student.setLastName(cursor.getString(2));
        student.setAddress(cursor.getString(3));
        student.setCountry(cursor.getString(4));
        student.setMail(cursor.getString(5));
        student.setStartDate(cursor.getString(6));
        student.setEndDate(cursor.getString(7));
        student.setImageName(cursor.getString(8));




        // return teacher
        return student;
    }

    public int getMaxId() {
        SQLiteDatabase sql = db.getWritableDatabase();

        int id = 0;
        String query = "SELECT MAX(" + db.getKeyId() + ") FROM " + db.getTableLecture();

        Cursor cursor = sql.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }



        return id;
    }

    /**
     * get all students for a lecture
     *
     * @param idLecture
     * @return
     */
    public ArrayList<Student> getStudentsListByLecture(int idLecture) {
        SQLiteDatabase sql = db.getWritableDatabase();

        ArrayList<Student> studentsList = new ArrayList<Student>();

        String selectQuery = "SELECT " + db.getLECTURESTUDENT_FKSTUDENT() + " FROM " + db.getTableLecturestudent() + " WHERE " + db.getLECTURESTUDENT_FKLECTURE() + " = " + idLecture;

        Cursor cursor = sql.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Student student = getStudentById(Integer.parseInt(cursor.getString(0)));

                // Adding student to list
                studentsList.add(student);
            } while (cursor.moveToNext());
        }




        // return teacher
        return studentsList;

    }

    /**
     * get all students
     *
     * @return
     */
    public ArrayList<Student> getAllStudents() {

        SQLiteDatabase sql = db.getWritableDatabase();

        ArrayList<Student> studentsList = new ArrayList<Student>();
        String selectQuery = "SELECT * FROM " + db.getTableStudent() + " ORDER BY " + db.getSTUDENT_FIRSTNAME() + ", " + db.getSTUDENT_LASTNAME();

        Cursor cursor = sql.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(Integer.parseInt(cursor.getString(0)));
                student.setFirstName(cursor.getString(1));
                student.setLastName(cursor.getString(2));
                student.setAddress(cursor.getString(3));
                student.setCountry(cursor.getString(4));
                student.setMail(cursor.getString(5));
                student.setStartDate(cursor.getString(6));
                student.setEndDate(cursor.getString(7));
                student.setImageName(cursor.getString(8));

                // Adding student to list
                studentsList.add(student);
            } while (cursor.moveToNext());
        }




        // return students list
        return studentsList;
    }

    /**
     * delete a student
     *
     * @param idStudent
     */
    public void deleteStudent(int idStudent) {
        deleteStudentById(idStudent);
        deleteStudentFromLectures(idStudent);
    }

    /**
     * delete the student in student table
     *
     * @param idStudent
     */
    private void deleteStudentById(int idStudent) {
        SQLiteDatabase sql = db.getWritableDatabase();

        sql.delete(db.getTableStudent(), db.getKeyId() + " = ?",
                new String[]{String.valueOf(idStudent)});


    }

    /**
     * delete the student in lecturestudent
     *
     * @param idStudent
     */
    private void deleteStudentFromLectures(int idStudent) {


        SQLiteDatabase sql = db.getWritableDatabase();

        sql.delete(db.getTableLecturestudent(), db.getLECTURESTUDENT_FKSTUDENT() + " = ?",
                new String[]{String.valueOf(idStudent)});

    }

    /**
     * update a student by id
     *
     * @param idStudent
     * @param firstName
     * @param lastName
     * @param address
     * @param country
     * @param mail
     * @param startDate
     * @param endDate
     * @return
     */
    public int updateStudentById(int idStudent, String firstName, String lastName, String address, String country, String mail, String startDate, String endDate) {

        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.getSTUDENT_FIRSTNAME(), firstName);
        values.put(db.getSTUDENT_LASTNAME(), lastName);
        values.put(db.getSTUDENT_ADDRESS(), address);
        values.put(db.getSTUDENT_COUNTRY(), country);
        values.put(db.getSTUDENT_MAIL(), mail);
        values.put(db.getSTUDENT_STARTDATE(), startDate);
        values.put(db.getSTUDENT_ENDDATE(), endDate);


        return sql.update(db.getTableStudent(),
                values,
                db.getKeyId() + " = ?",
                new String[]{String.valueOf(idStudent)});

    }

    public void syncStudentsToCloud() {

        List<Student> students = getAllStudents();

        for (Student s : students
                ) {
            com.example.patrickclivaz.myapplication.backend.studentApi.model.Student student = new com.example.patrickclivaz.myapplication.backend.studentApi.model.Student();

            student.setId((long) s.getId());
            student.setFirstName(s.getFirstName());
            student.setLastName(s.getLastName());
            student.setAddress(s.getAddress());
            student.setCountry(s.getCountry());
            student.setMail(s.getMail());
            student.setStartDate(s.getStartDate());
            student.setEndDate(s.getEndDate());
            student.setImageName(s.getImageName());


            new StudentAsyncTask(student).execute();

        }

        Log.e("Aleks", "All students into the cloud");
    }

    public void syncStudentToCloud(Student s){
        com.example.patrickclivaz.myapplication.backend.studentApi.model.Student student = new com.example.patrickclivaz.myapplication.backend.studentApi.model.Student();

        student.setId((long) s.getId());
        student.setFirstName(s.getFirstName());
        student.setLastName(s.getLastName());
        student.setAddress(s.getAddress());
        student.setCountry(s.getCountry());
        student.setMail(s.getMail());
        student.setStartDate(s.getStartDate());
        student.setEndDate(s.getEndDate());
        student.setImageName(s.getImageName());


        new StudentAsyncTask(student).execute();

    }

    public void deleteStudentInCloud(Student s){
        com.example.patrickclivaz.myapplication.backend.studentApi.model.Student student = new com.example.patrickclivaz.myapplication.backend.studentApi.model.Student();

        student.setId((long) s.getId());
        student.setFirstName(s.getFirstName());
        student.setLastName(s.getLastName());
        student.setAddress(s.getAddress());
        student.setCountry(s.getCountry());
        student.setMail(s.getMail());
        student.setStartDate(s.getStartDate());
        student.setEndDate(s.getEndDate());
        student.setImageName(s.getImageName());


        new StudentAsyncTask(student, true).execute();

    }

    public void retrieveStudents(List<com.example.patrickclivaz.myapplication.backend.studentApi.model.Student> students) {

        SQLiteDatabase sql = db.getReadableDatabase();

        for (com.example.patrickclivaz.myapplication.backend.studentApi.model.Student s : students
                ) {
            insertValues(s.getFirstName(), s.getLastName(), s.getAddress(), s.getCountry(), s.getMail(), s.getStartDate(), s.getEndDate());
        }
    }
}
