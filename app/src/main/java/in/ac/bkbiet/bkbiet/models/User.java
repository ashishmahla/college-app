package in.ac.bkbiet.bkbiet.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * Created by Ashish Mahla on 9/8/2017.
 * Custom class which stores user related data.
 */

public class User implements Parcelable {
    public static final int TYPE_OTHER = 0;
    public static final int TYPE_GUEST = 7;
    public static final int TYPE_STUDENT = 17;
    public static final int TYPE_TEACHER = 27;
    public static final int TYPE_ADMIN = 37;
    public static final int TYPE_DEVELOPER = 171717;
    public static Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };
    private static String DEFAULT_DP_LINK = "no-link";
    private static final User DEFAULT_USER = new User("GENERATE_RANDOM", "John Doe",
            "", "********", TYPE_GUEST,
            "", DEFAULT_DP_LINK);
    private int id;
    private int type;
    private String username;
    private String name;
    private String email;
    private String mobileNo;
    private String dpLink;
    private String password;
    private String createdAt;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String name, String email, String password, int type) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.type = type;
    }

    public User(String username, String name, String email, String password, int type, String mobileNo, String dpLink) {
        this(username, name, email, password, type);
        this.mobileNo = mobileNo;
        this.dpLink = dpLink;
    }

    public User(Parcel in) {
        this.id = in.readInt();
        type = in.readInt();
        username = in.readString();
        name = in.readString();
        email = in.readString();
        mobileNo = in.readString();
        dpLink = in.readString();
        password = in.readString();
        createdAt = in.readString();
    }

    public static User getDefaultUser(Context context) {
        return getDefaultUser(context, "John Doe");
    }

    public static User getDefaultUser(Context context, String name) {
        DEFAULT_USER.setUsername(Statics.getDeviceId(context));
        DEFAULT_USER.setName(name);
        return DEFAULT_USER;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getEffectiveType() {
        if (Uv.isDevOn)
            return User.TYPE_DEVELOPER;
        return type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDpLink() {
        return dpLink;
    }

    public void setDpLink(String dpLink) {
        this.dpLink = dpLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(type);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(mobileNo);
        dest.writeString(dpLink);
        dest.writeString(password);
        dest.writeString(createdAt);
    }
}