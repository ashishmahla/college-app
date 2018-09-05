package in.ac.bkbiet.bkbiet.models;

import android.support.annotation.NonNull;

/**
 * Versions Created by Ashish on 9/5/2017.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class Versions implements Comparable<Versions> {
    private int vCode;
    private String vName;
    private String vWhatsNew;
    private String vReleaseDate;
    private int vType;

    public Versions() {

    }

    public Versions(int vCode, String vName, String vWhatsNew, String vReleaseDate, int vType) {
        this.vCode = vCode;
        this.vName = vName;
        this.vWhatsNew = vWhatsNew;
        this.vReleaseDate = vReleaseDate;
        this.vType = vType;
    }

    public int getvCode() {
        return vCode;
    }

    public void setvCode(int vCode) {
        this.vCode = vCode;
    }

    public String getvName() {
        return vName;
    }

    public void setvName(String vName) {
        this.vName = vName;
    }

    public String getvWhatsNew() {
        return vWhatsNew;
    }

    public void setvWhatsNew(String vWhatsNew) {
        this.vWhatsNew = vWhatsNew;
    }

    public String getvReleaseDate() {
        return vReleaseDate;
    }

    public void setvReleaseDate(String vReleaseDate) {
        this.vReleaseDate = vReleaseDate;
    }

    public int getvType() {
        return vType;
    }

    public void setvType(int vType) {
        this.vType = vType;
    }

    @Override
    public int compareTo(@NonNull Versions o) {
        return o.getvCode() - this.vCode;
    }
}