package ch.hes.foreignlanguageschool;

import java.io.Serializable;
import java.util.List;

import ch.hes.foreignlanguageschool.DB.DBTeacher;
import ch.hes.foreignlanguageschool.DB.DatabaseHelper;

/**
 * Created by Aleksandar on 06.04.2017.
 */

public class Teacher implements Serializable {

    private long id;
    private String firstName;
    private String lastName;
    private String mail;
    private String imageName;

    public Teacher() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
