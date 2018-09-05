package in.ac.bkbiet.bkbiet.models;

import android.support.annotation.NonNull;

/**
 * +Created by Ashish on 2/16/2018.
 */

public class ActiveLink implements Comparable<ActiveLink> {
    private String id;
    private String name;
    private String url;
    private String desc;

    private boolean expirable;
    private String expiryDate;

    private String authorName;
    private String authorUsername;
    private String createdOn;

    public ActiveLink() {
    }

    public boolean isExpirable() {
        return expirable;
    }

    public void setExpirable(boolean expirable) {
        this.expirable = expirable;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "name : " + name + ", url : " + url + ", desc : " + desc + ", expirable : " + expirable + ", expiryDate : " + expiryDate;
    }

    @Override
    public int compareTo(@NonNull ActiveLink link) {
        final int cType = -1;
        try {
            int year, month, day;
            day = Integer.parseInt(expiryDate.substring(0, 2));
            month = Integer.parseInt(expiryDate.substring(3, 5));
            year = Integer.parseInt(expiryDate.substring(6, 10));

            int linkYear, linkMonth, linkDay;
            linkDay = Integer.parseInt(link.getExpiryDate().substring(0, 2));
            linkMonth = Integer.parseInt(link.getExpiryDate().substring(3, 5));
            linkYear = Integer.parseInt(link.getExpiryDate().substring(6, 10));

            if (year != linkYear)
                return cType * (linkYear - year);
            else if (month != linkMonth)
                return cType * (linkMonth - month);
            else if (linkDay != day)
                return cType * (linkDay - day);

        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }
        return 0;
    }
}