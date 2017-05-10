package ch.hes.foreignlanguageschool.DAO;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by patrickclivaz on 11.04.17.
 */

public class Student implements Serializable {

    private int id;
    private String firstName;
    private String lastName;
    private String address;
    private String country;
    private String mail;
    private String startDate;
    private String endDate;
    private ArrayList<Lecture> lecturesList;
    private String imageName;

    public Student(int id, String firstName, String lastName, String address, String country, String mail, String startDate, String endDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.country = country;
        this.mail = mail;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Student() {
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public ArrayList<Lecture> getLecturesList() {
        return lecturesList;
    }

    public void setLecturesList(ArrayList lecturesList) {
        this.lecturesList = lecturesList;
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

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Student)) {
            return false;
        }

        Student student = (Student) obj;

        if (id == student.getId()) {
            return true;
        }

        return false;
    }
}
