package in.ac.bkbiet.bkbiet.models;

/**
 * Created by Ashish Mahla on 9/4/2017.
 * Custom class which stores student details.
 */

public class Student {
    public static final int iTOTAL_FIELDS = 10;

    private String name;
    private String collegeId;
    private String email;
    private String contact;
    private String mothersName;
    private String fathersName;
    private String fathersEmail;
    private String fathersContact;
    private String dob;
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String college_id) {
        this.collegeId = college_id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getMothersName() {
        return mothersName;
    }

    public void setMothersName(String mothersName) {
        this.mothersName = mothersName;
    }

    public String getFathersName() {
        return fathersName;
    }

    public void setFathersName(String fathersName) {
        this.fathersName = fathersName;
    }

    public String getFathersEmail() {
        return fathersEmail;
    }

    public void setFathersEmail(String fathersEmail) {
        this.fathersEmail = fathersEmail;
    }

    public String getFathersContact() {
        return fathersContact;
    }

    public void setFathersContact(String fathersContact) {
        this.fathersContact = fathersContact;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void resetFields() {
        name = name.equals("null") ? "" : name;
        collegeId = collegeId.equals("null") ? "" : collegeId;
        email = email.equals("null") ? "" : email;
        contact = contact.equals("null") ? "" : contact;
        mothersName = mothersName.equals("null") ? "" : mothersName;
        fathersName = fathersName.equals("null") ? "" : fathersName;
        fathersEmail = fathersEmail.equals("null") ? "" : fathersEmail;
        fathersContact = fathersContact.equals("null") ? "" : fathersContact;
        dob = dob.equals("null") ? "" : dob;
        address = address.equals("null") ? "" : address;
    }

    public int getNonEmptyFields() {
        int counter = 0;
        counter = name.isEmpty() ? counter : counter + 1;
        counter = collegeId.isEmpty() ? counter : counter + 1;
        counter = email.isEmpty() ? counter : counter + 1;
        counter = contact.isEmpty() ? counter : counter + 1;
        counter = mothersName.isEmpty() ? counter : counter + 1;
        counter = fathersName.isEmpty() ? counter : counter + 1;
        counter = fathersEmail.isEmpty() ? counter : counter + 1;
        counter = fathersContact.isEmpty() ? counter : counter + 1;
        counter = dob.isEmpty() ? counter : counter + 1;
        counter = address.isEmpty() ? counter : counter + 1;

        return counter;
    }
}
