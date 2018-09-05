package in.ac.bkbiet.bkbiet.models;

/**
 * Created by Ashish on 4/21/2018.
 */

public class Notice {
    private String title;
    private String desc;
    private String uid;
    private String imgUrl;

    public Notice() {
        // for firebase
    }

    public Notice(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public Notice(String uid, String title, String desc) {
        this(title, desc);
        this.uid = uid;
    }

    public String getDesc() {
        return desc;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}