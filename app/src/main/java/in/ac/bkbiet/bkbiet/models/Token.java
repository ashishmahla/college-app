package in.ac.bkbiet.bkbiet.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.iid.FirebaseInstanceId;

import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * Token Created by Ashish on 9/6/2017.
 */

@SuppressWarnings("unused")
public class Token {
    @Exclude
    private static Token singleton;
    private String name;
    private String token;
    private String lastRefresh;

    private Token() {
        // for firebase
    }

    public static Token getInstance() {
        if (singleton == null)
            singleton = new Token();
        return singleton;
    }

    public Token refresh() {
        this.name = Uv.currUser.getName();
        this.token = FirebaseInstanceId.getInstance().getToken();
        this.lastRefresh = Statics.getTimeStamp();

        return this;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(String lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}