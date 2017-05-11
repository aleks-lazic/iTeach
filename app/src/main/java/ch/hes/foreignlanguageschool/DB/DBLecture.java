package ch.hes.foreignlanguageschool.DB;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.patrickclivaz.myapplication.backend.lectureApi.model.Day;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ch.hes.foreignlanguageschool.Lecture;
import ch.hes.foreignlanguageschool.LectureAsyncTask;
import ch.hes.foreignlanguageschool.Student;
import ch.hes.foreignlanguageschool.Teacher;
import ch.hes.foreignlanguageschool.TeacherAsyncTask;

/**
 * Created by patrickclivaz on 11.04.17.
 */

public class DBLecture {

    private DatabaseHelper db;
    private DBDay dbDay;
    private DBStudent dbStudent;

    public DBLecture(DatabaseHelper db) {
        this.db = db;
    }


    /**
     * insert values for lecture in db
     *
     * @param name
     * @param description
     * @param idTeacher
     */
    public void insertValues(String name, String description, long idTeacher) {

        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.getLECTURE_NAME(), name);
        values.put(db.getLECTURE_DESCRIPTION(), description);
        values.put(db.getIMAGE_NAME(), "lecture_icon");
        values.put(db.getLECTURE_FKTEACHER(), idTeacher);

        sql.insert(db.getTableLecture(), null, values);
        sql.close();
    }

    /**
     * get all lectures for a teacher
     *
     * @param idTeacher
     * @return
     */
    public ArrayList<Lecture> getLecturesForTeacher(int idTeacher) {
        SQLiteDatabase sql = db.getReadableDatabase();

        ArrayList<Lecture> lecturesList = new ArrayList<Lecture>();
        String selectQuery = "SELECT * "
                + "FROM " + db.getTableLecture()
                + " WHERE " + db.getLECTURE_FKTEACHER() + " = " + idTeacher;


        Cursor cursor = sql.rawQuery(selectQuery, null);

        DBTeacher teacher = new DBTeacher(db);
        DBStudent student = new DBStudent(db);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lecture lecture = new Lecture();
                lecture.setId(Integer.parseInt(cursor.getString(0)));
                lecture.setName(cursor.getString(1));
                lecture.setDescription(cursor.getString(2));
                lecture.setImageName(cursor.getString((3)));

                // Adding lecture to list
                lecturesList.add(lecture);
            } while (cursor.moveToNext());
        }

        sql.close();


        // return lectures list
        return lecturesList;
    }

    /**
     * get all lectures for a student
     *
     * @param idStudent
     * @return
     */
    public ArrayList<Lecture> getLecturesForStudent(int idStudent) {
        SQLiteDatabase sql = db.getReadableDatabase();

        ArrayList<Lecture> lecturesList = new ArrayList<Lecture>();
        String selectQuery = "SELECT " + db.getLECTURESTUDENT_FKLECTURE()
                + " FROM " + db.getTableLecturestudent()
                + " WHERE " + db.getLECTURESTUDENT_FKSTUDENT() + " = " + idStudent;


        Cursor cursor = sql.rawQuery(selectQuery, null);

        DBTeacher teacher = new DBTeacher(db);
        DBStudent student = new DBStudent(db);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lecture lecture = getLectureById(Integer.parseInt(cursor.getString(0)));

                // Adding lecture to list
                lecturesList.add(lecture);
            } while (cursor.moveToNext());
        }

        sql.close();


        // return lectures list
        return lecturesList;
    }

    /**
     * get lecture by id
     *
     * @param idLecture
     * @return
     */
    public Lecture getLectureById(int idLecture) {

        SQLiteDatabase sql = db.getReadableDatabase();

        String selectQuery = "SELECT * "
                + "FROM " + db.getTableLecture()
                + " WHERE " + db.getKeyId() + " = " + idLecture;


        Cursor cursor = sql.rawQuery(selectQuery, null);

        DBTeacher teacher = new DBTeacher(db);

        // looping through all rows and adding to list

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Lecture lecture = new Lecture();
        lecture.setId(Integer.parseInt(cursor.getString(0)));
        lecture.setName(cursor.getString(1));
        lecture.setDescription(cursor.getString(2));
        lecture.setImageName(cursor.getString((3)));


        sql.close();


        // return lectures
        return lecture;
    }

    /**
     * get lecture by id when there's an update
     *
     * @param idLecture
     * @return
     */
    public Lecture getLectureByIdForUpdate(int idLecture) {
        SQLiteDatabase sql = db.getReadableDatabase();

        ArrayList<Lecture> lecturesList = new ArrayList<Lecture>();
        String selectQuery = "SELECT "
                + db.getKeyId() + ", "
                + db.getLECTURE_NAME() + ", "
                + db.getLECTURE_DESCRIPTION() + ", "
                + db.getIMAGE_NAME() + ", "
                + db.getLECTURE_FKTEACHER() + ", "
                + db.getLECTUREDATE_FKDAY() + ", "
                + db.getLECTUREDATE_STARTTIME() + ", "
                + db.getLECTUREDATE_ENDTIME() + " "
                + "FROM " + db.getTableLecture()
                + " LEFT JOIN " + db.getTableLecturedate() + " ON " + db.getTableLecture() + "." + db.getKeyId() + " = " + db.getTableLecturedate() + "." + db.getLECTUREDATE_FKLECTURE()
                + " WHERE " + db.getKeyId() + " = " + idLecture;


        Cursor cursor = sql.rawQuery(selectQuery, null);

        DBTeacher teacher = new DBTeacher(db);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Lecture lecture = new Lecture();
        lecture.setId(Integer.parseInt(cursor.getString(0)));
        lecture.setName(cursor.getString(1));
        lecture.setDescription(cursor.getString(2));
        lecture.setImageName(cursor.getString((3)));
        lecture.setTeacher(teacher.getTeacherById(Integer.parseInt(cursor.getString(4))));
        lecture.setIdDay(Integer.parseInt(cursor.getString(5)));
        lecture.setStartTime(cursor.getString(6));
        lecture.setEndTime(cursor.getString(7));


        sql.close();


        // return lectures list
        return lecture;
    }

    /**
     * get alle lectures except the parameter id
     *
     * @param idLecture
     * @return
     */
    public ArrayList<Lecture> getAllLecturesExceptById(int idLecture) {
        SQLiteDatabase sql = db.getReadableDatabase();

        ArrayList<Lecture> lecturesList = new ArrayList<Lecture>();
        String selectQuery = "SELECT "
                + db.getKeyId() + ", "
                + db.getLECTURE_NAME() + ", "
                + db.getLECTURE_DESCRIPTION() + ", "
                + db.getIMAGE_NAME() + ", "
                + db.getLECTURE_FKTEACHER() + ", "
                + db.getLECTUREDATE_FKDAY() + ", "
                + db.getLECTUREDATE_STARTTIME() + ", "
                + db.getLECTUREDATE_ENDTIME() + " "
                + "FROM " + db.getTableLecture()
                + " LEFT JOIN " + db.getTableLecturedate() + " ON " + db.getTableLecture() + "." + db.getKeyId() + " = " + db.getTableLecturedate() + "." + db.getLECTUREDATE_FKLECTURE()
                + " WHERE " + db.getKeyId() + " != " + idLecture;


        Cursor cursor = sql.rawQuery(selectQuery, null);

        DBTeacher teacher = new DBTeacher(db);
        DBStudent student = new DBStudent(db);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lecture lecture = new Lecture();
                lecture.setId(Integer.parseInt(cursor.getString(0)));
                lecture.setName(cursor.getString(1));
                lecture.setDescription(cursor.getString(2));
                lecture.setImageName(cursor.getString((3)));
                lecture.setTeacher(teacher.getTeacherById(Integer.parseInt(cursor.getString(4))));
                lecture.setIdDay(Integer.parseInt(cursor.getString(5)));
                lecture.setStartTime(cursor.getString(6));
                lecture.setEndTime(cursor.getString(7));

                // Adding lecture to list
                lecturesList.add(lecture);
            } while (cursor.moveToNext());
        }

        sql.close();


        // return lectures list
        return lecturesList;
    }

    /**
     * get all lectures for db
     *
     * @return
     */
    public ArrayList<Lecture> getAllLectures() {
        SQLiteDatabase sql = db.getReadableDatabase();

        ArrayList<Lecture> lecturesList = new ArrayList<Lecture>();
        String selectQuery = "SELECT "
                + db.getKeyId() + ", "
                + db.getLECTURE_NAME() + ", "
                + db.getLECTURE_DESCRIPTION() + ", "
                + db.getIMAGE_NAME() + ", "
                + db.getLECTURE_FKTEACHER() + ", "
                + db.getLECTUREDATE_FKDAY() + ", "
                + db.getLECTUREDATE_STARTTIME() + ", "
                + db.getLECTUREDATE_ENDTIME() + " "
                + "FROM " + db.getTableLecture()
                + " LEFT JOIN " + db.getTableLecturedate() + " ON " + db.getTableLecture() + "." + db.getKeyId() + " = " + db.getTableLecturedate() + "." + db.getLECTUREDATE_FKLECTURE();


        Cursor cursor = sql.rawQuery(selectQuery, null);

        DBTeacher teacher = new DBTeacher(db);
        DBStudent student = new DBStudent(db);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lecture lecture = new Lecture();
                lecture.setId(Integer.parseInt(cursor.getString(0)));
                lecture.setName(cursor.getString(1));
                lecture.setDescription(cursor.getString(2));
                lecture.setImageName(cursor.getString((3)));
                lecture.setTeacher(teacher.getTeacherById(Integer.parseInt(cursor.getString(4))));
                lecture.setIdDay(Integer.parseInt(cursor.getString(5)));
                lecture.setStartTime(cursor.getString(6));
                lecture.setEndTime(cursor.getString(7));

                // Adding lecture to list
                lecturesList.add(lecture);
            } while (cursor.moveToNext());
        }

        sql.close();


        // return lectures list
        return lecturesList;
    }

    /**
     * add a student to a lecture
     *
     * @param idStudent
     * @param idLecture
     */
    public void addStudentToLecture(int idStudent, int idLecture) {

        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.getLECTURESTUDENT_FKLECTURE(), idLecture);
        values.put(db.getLECTURESTUDENT_FKSTUDENT(), idStudent);

        sql.insert(db.getTableLecturestudent(), null, values);

        sql.close();

    }

    /**
     * add students to lecture
     *
     * @param students
     * @param idLecture
     */
    public void addStudentsToLecture(ArrayList<Student> students, int idLecture) {

        for (Student s : students
                ) {
            addStudentToLecture(s.getId(), idLecture);

        }
    }

    /**
     * Add day and hours to the lecture
     *
     * @param idLecture
     * @param idDay
     * @param startTime
     * @param endTime
     */
    public void addDayAndHoursToLecture(int idLecture, int idDay, String startTime, String endTime) {
        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(db.getLECTUREDATE_FKLECTURE(), idLecture);
        values.put(db.getLECTUREDATE_FKDAY(), idDay);
        values.put(db.getLECTUREDATE_STARTTIME(), startTime);
        values.put(db.getLECTUREDATE_ENDTIME(), endTime);

        sql.insert(db.getTableLecturedate(), null, values);
        sql.close();
    }

    /**
     * Get lectures for special date used in calendar
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @return
     */
    public ArrayList<Lecture> getLecturesForSpecialDate(int year, int month, int dayOfMonth) {

        //adapt the current date to the sqlite format
        String monthString;

        if (month < 10)
            monthString = Integer.toString(month + 1);
        else {
            monthString = Integer.toString(month);
        }

        Calendar cal = new GregorianCalendar(year, Integer.parseInt(monthString), dayOfMonth);
        int result = cal.get(Calendar.DAY_OF_WEEK);
        int dayOfWeek = getIdOfDayWeek(result);

        SQLiteDatabase sql = db.getWritableDatabase();

        ArrayList<Lecture> lecturesList = new ArrayList<Lecture>();
        String selectQuery = "SELECT "
                + db.getKeyId() + ", "
                + db.getLECTURE_NAME() + ", "
                + db.getLECTURE_DESCRIPTION() + ", "
                + db.getIMAGE_NAME() + ", "
                + db.getLECTURE_FKTEACHER() + ", "
                + db.getLECTUREDATE_FKDAY() + ", "
                + db.getLECTUREDATE_STARTTIME() + ", "
                + db.getLECTUREDATE_ENDTIME() + " "
                + "FROM " + db.getTableLecture()
                + " LEFT JOIN " + db.getTableLecturedate() + " ON " + db.getTableLecture() + "." + db.getKeyId() + " = " + db.getTableLecturedate() + "." + db.getLECTUREDATE_FKLECTURE()
                + " WHERE " + db.getLECTUREDATE_FKDAY() + " = " + dayOfWeek;


        Cursor cursor = sql.rawQuery(selectQuery, null);

        DBTeacher teacher = new DBTeacher(db);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lecture lecture = new Lecture();
                lecture.setId(Integer.parseInt(cursor.getString(0)));
                lecture.setName(cursor.getString(1));
                lecture.setDescription(cursor.getString(2));
                lecture.setImageName(cursor.getString((3)));
                lecture.setTeacher(teacher.getTeacherById(Integer.parseInt(cursor.getString(4))));
                lecture.setIdDay(Integer.parseInt(cursor.getString(5)));
                lecture.setStartTime(cursor.getString(6));
                lecture.setEndTime(cursor.getString(7));

                // Adding lecture to list
                lecturesList.add(lecture);
            } while (cursor.moveToNext());
        }

        sql.close();


        // return lectures list
        return lecturesList;
    }

    /**
     * get Max Id for lecture's table
     *
     * @return
     */
    public int getMaxId() {
        SQLiteDatabase sql = db.getWritableDatabase();

        int id = 0;
        String query = "SELECT MAX(" + db.getKeyId() + ") FROM " + db.getTableLecture();

        Cursor cursor = sql.rawQuery(query, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }

        sql.close();

        return id;
    }

    /**
     * insert a lecture with all informations directly
     *
     * @param title
     * @param description
     * @param idTeacher
     * @param idDay
     * @param beginTime
     * @param endTime
     * @param students
     */
    public void insertLectureWithTeacherDayAndHoursAndStudents(String title, String description, long idTeacher, int idDay, String beginTime, String endTime, ArrayList<Student> students) {

        insertValues(title, description, idTeacher);

        int id = getMaxId();

        addDayAndHoursToLecture(id, idDay, beginTime, endTime);

        addStudentsToLecture(students, id);

    }

    /**
     * Get lectures for current date in today fragment
     *
     * @param date
     * @return
     */
    public ArrayList<Lecture> getLecturesForCurrentDateInHome(String date) {

        //25.04.2017

        int day = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(3, 5));
        int year = Integer.parseInt(date.substring(6, 10));


        Calendar cal = new GregorianCalendar(year, month, day);
        int result = cal.get(Calendar.DAY_OF_WEEK);
        int dayOfWeek = getIdOfDayWeek(result);

        SQLiteDatabase sql = db.getWritableDatabase();

        ArrayList<Lecture> lecturesList = new ArrayList<Lecture>();
        String selectQuery = "SELECT "
                + db.getKeyId() + ", "
                + db.getLECTURE_NAME() + ", "
                + db.getLECTURE_DESCRIPTION() + ", "
                + db.getIMAGE_NAME() + ", "
                + db.getLECTURE_FKTEACHER() + ", "
                + db.getLECTUREDATE_FKDAY() + ", "
                + db.getLECTUREDATE_STARTTIME() + ", "
                + db.getLECTUREDATE_ENDTIME() + " "
                + "FROM " + db.getTableLecture()
                + " LEFT JOIN " + db.getTableLecturedate() + " ON " + db.getTableLecture() + "." + db.getKeyId() + " = " + db.getTableLecturedate() + "." + db.getLECTUREDATE_FKLECTURE()
                + " WHERE " + db.getLECTUREDATE_FKDAY() + " = " + dayOfWeek + " ORDER BY " + db.getLECTUREDATE_STARTTIME();


        Cursor cursor = sql.rawQuery(selectQuery, null);

        DBTeacher teacher = new DBTeacher(db);
        DBStudent student = new DBStudent(db);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Lecture lecture = new Lecture();
                lecture.setId(Integer.parseInt(cursor.getString(0)));
                lecture.setName(cursor.getString(1));
                lecture.setDescription(cursor.getString(2));
                lecture.setImageName(cursor.getString((3)));
                lecture.setTeacher(teacher.getTeacherById(Integer.parseInt(cursor.getString(4))));
                lecture.setIdDay(Integer.parseInt(cursor.getString(5)));
                lecture.setStartTime(cursor.getString(6));
                lecture.setEndTime(cursor.getString(7));

                // Adding lecture to list
                lecturesList.add(lecture);
            } while (cursor.moveToNext());
        }

        sql.close();


        // return lectures list
        return lecturesList;
    }

    /**
     * delete a lecture
     *
     * @param idLecture
     */
    public void deleteLecture(int idLecture) {
        deleteLectureById(idLecture);
        deleteLectureFromLectureStudent(idLecture);
        deleteDayFromLecture(idLecture);
    }


    /**
     * delete the lecture in lecture table
     *
     * @param idLecture
     */
    private void deleteLectureById(int idLecture) {

        SQLiteDatabase sql = db.getWritableDatabase();

        sql.delete(db.getTableLecture(), db.getKeyId() + " = ?",
                new String[]{String.valueOf(idLecture)});
        sql.close();

    }

    /**
     * delete the lecture in lecturestudent table
     *
     * @param idLecture
     */
    public void deleteLectureFromLectureStudent(int idLecture) {
        SQLiteDatabase sql = db.getWritableDatabase();

        sql.delete(db.getTableLecturestudent(), db.getLECTURESTUDENT_FKLECTURE() + " = ?",
                new String[]{String.valueOf(idLecture)});
        sql.close();


    }

    /**
     * delete the lecture from lecturedate table
     *
     * @param idLecture
     */
    private void deleteDayFromLecture(int idLecture) {
        SQLiteDatabase sql = db.getWritableDatabase();

        sql.delete(db.getTableLecturedate(), db.getLECTUREDATE_FKLECTURE() + " = ?",
                new String[]{String.valueOf(idLecture)});
        sql.close();
    }


    /**
     * Update a lecture name and description
     *
     * @param idLecture
     * @param name
     * @param description
     * @return
     */
    public int updateLectureNameAndDescription(int idLecture, String name, String description) {
        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(db.getLECTURE_NAME(), name);
        values.put(db.getLECTURE_DESCRIPTION(), description);

        return sql.update(db.getTableLecture(), values, db.getKeyId() + " = ?",
                new String[]{String.valueOf(idLecture)});
    }

    /**
     * upadte day and time for a lecture
     *
     * @param idLecture
     * @param oldIdDay
     * @param idDay
     * @param beginTime
     * @param endTime
     * @return
     */
    public int updateDayTime(int idLecture, int oldIdDay, int idDay, String beginTime, String endTime) {
        SQLiteDatabase sql = db.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(db.getLECTUREDATE_FKDAY(), idDay);
        values.put(db.getLECTUREDATE_STARTTIME(), beginTime);
        values.put(db.getLECTUREDATE_ENDTIME(), endTime);


        return sql.update(db.getTableLecturedate(),
                values,
                db.getLECTUREDATE_FKLECTURE() + " = ? AND " + db.getLECTUREDATE_FKDAY() + " = ?",
                new String[]{String.valueOf(idLecture), String.valueOf(oldIdDay)});
    }


    /**
     * get the good day of the week depending on the calendar
     *
     * @param day
     * @return
     */
    private int getIdOfDayWeek(int day) {
        switch (day) {
            case 5:
                return 1;
            case 6:
                return 2;
            case 7:
                return 3;
            case 1:
                return 4;
            case 2:
                return 5;
            case 3:
                return 6;
        }

        return 7;
    }

    public void syncLecturesToCloud() {

        List<Lecture> lectures = getAllLectures();

        for (Lecture l : lectures
                ) {
            com.example.patrickclivaz.myapplication.backend.lectureApi.model.Lecture lecture = new com.example.patrickclivaz.myapplication.backend.lectureApi.model.Lecture();

            lecture.setId((long) l.getId());
            lecture.setName(l.getName());
            lecture.setDescription(l.getDescription());
            lecture.setImageName(l.getImageName());
            lecture.setStartTime(l.getStartTime());
            lecture.setEndTime((l.getEndTime()));

            //create the lecture's teacher
            com.example.patrickclivaz.myapplication.backend.lectureApi.model.Teacher teacher = new com.example.patrickclivaz.myapplication.backend.lectureApi.model.Teacher();
            teacher.setId((long) l.getTeacher().getId());
            teacher.setFirstName(l.getTeacher().getFirstName());
            teacher.setLastName(l.getTeacher().getLastName());
            teacher.setMail(l.getTeacher().getMail());
            teacher.setImageName(l.getTeacher().getImageName());
            lecture.setTeacher(teacher);

            //create the lecture's day
            Day day = new Day();
            dbDay = new DBDay(db);
            ch.hes.foreignlanguageschool.Day lectureDay = dbDay.getDayById(l.getIdDay());
            day.setId((long) lectureDay.getId());
            day.setName(lectureDay.getName());
            lecture.setDay(day);


            //create the lecture's students
            dbStudent = new DBStudent(db);
            l.setStudentsList(dbStudent.getStudentsListByLecture(l.getId()));
            ArrayList<com.example.patrickclivaz.myapplication.backend.lectureApi.model.Student> students = new ArrayList<com.example.patrickclivaz.myapplication.backend.lectureApi.model.Student>();
            for (Student s : l.getStudentsList()
                    ) {

                com.example.patrickclivaz.myapplication.backend.lectureApi.model.Student student = new com.example.patrickclivaz.myapplication.backend.lectureApi.model.Student();

                student.setId((long) s.getId());
                student.setFirstName(s.getFirstName());
                student.setLastName(s.getLastName());
                student.setAddress(s.getAddress());
                student.setCountry(s.getCountry());
                student.setMail(s.getMail());
                student.setStartDate(s.getStartDate());
                student.setEndDate(s.getEndDate());
                student.setImageName(s.getImageName());

                students.add(student);
            }

            lecture.setStudentsList(students);

            new LectureAsyncTask(lecture).execute();

        }

        Log.e("Aleks", "All letures into the cloud");
    }

    public void retrieveLecture(List<com.example.patrickclivaz.myapplication.backend.lectureApi.model.Lecture> lectures) {

        SQLiteDatabase sql = db.getReadableDatabase();

        sql.delete(db.getTableLecture(), null, null);
        sql.delete(db.getTableLecturedate(), null, null);
        sql.delete(db.getTableLecturestudent(), null, null);

        for (com.example.patrickclivaz.myapplication.backend.lectureApi.model.Lecture l : lectures
                ) {

            //insert the lecture's values
            insertValues(l.getName(), l.getDescription(), (Integer.parseInt(l.getTeacher().getId()+"")));

            //insert the student
            for (com.example.patrickclivaz.myapplication.backend.lectureApi.model.Student s : l.getStudentsList()
                 ) {
                addStudentToLecture(Integer.parseInt(s.getId()+""),Integer.parseInt(l.getId()+""));
            }

            //insert day values
            addDayAndHoursToLecture(Integer.parseInt(l.getId()+""),Integer.parseInt(l.getDay().getId()+""),l.getStartTime(), l.getEndTime());
        }
    }


}
