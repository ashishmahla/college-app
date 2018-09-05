package in.ac.bkbiet.bkbiet.models;

/**
 * Projects Created by Ashish on 12/4/2017.
 */

public class Project {
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_WAITING_REVIEW = 0;
    public static final int STATUS_REJECTED = 2;

    private int id;
    private String name;
    private String adviser;
    private String tech;
    private User[] developers;
    private String desc;
    private int status;
    private String createdOn;

    public Project(){
        this("","","", null ,"");
    }

    public Project(String name, String adviser, String tech, User[] developers, String desc) {
        this(-1, name, adviser, tech, developers, desc, -1, "");
    }

    public Project(int id, String name, String adviser, String tech, User[] developers, String desc, int status) {
        this(id, name, adviser, tech, developers, desc, status, "");
    }

    public Project(int id, String name, String adviser, String tech, User[] developers, String desc, int status, String createdOn) {
        this.id = id;
        this.name = name;
        this.adviser = adviser;
        this.tech = tech;
        this.developers = developers;
        this.desc = desc;
        this.status = status;
        this.createdOn = createdOn;
    }

    public int getId() {
        return id;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdviser() {
        return adviser;
    }

    public void setAdviser(String adviser) {
        this.adviser = adviser;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech;
    }

    public User[] getDevelopers() {
        return developers;
    }

    public void setDevelopers(User[] developers) {
        this.developers = developers;
    }
}
