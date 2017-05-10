package ch.hes.foreignlanguageschool.DAO;

import java.io.Serializable;
import java.util.List;

import ch.hes.foreignlanguageschool.DB.DBTeacher;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;

/**
 * Created by Aleksandar on 06.04.2017.
 */

public class Teacher implements Serializable {

    //Singleton unique instance
    private static Teacher currentTeacher;
    private static DBTeacher dbTeacher;
    private int id;
    private String firstName;
    private String lastName;
    private String mail;
    private List<Assignment> assignmentList;
    private String imageName;
    private List<Lecture> lecturesList;

    private Teacher(int id, String firstName, String lastName, String mail) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;

    }

    public Teacher() {
    }

    public static Teacher getInstance(DatabaseHelper db, int idTeacher) {

        if (currentTeacher == null) {
            dbTeacher = new DBTeacher(db);
            currentTeacher = dbTeacher.getTeacherById(idTeacher);
            return currentTeacher;
        }

        return currentTeacher;


    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastname) {
        this.lastName = lastname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public void setAssignmentList(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String toString() {
        return firstName + " " + lastName;
    }

    public List<Lecture> getLecturesList() {
        return lecturesList;
    }

    public void setLecturesList(List<Lecture> lecturesList) {
        this.lecturesList = lecturesList;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Teacher)) {
            return false;
        }

        Teacher t = (Teacher) obj;

        if (id == t.getId()) {
            return true;
        }

        return false;
    }
}
