package in.ac.bkbiet.bkbiet.models;

import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * Audit Record Created by Ashish on 12/7/2017.
 */

public class AuditRecord {
    private String username;
    private String name;
    private String transactionType;
    private String timeStamp;
    private String desc;

    public AuditRecord() {

    }

    public AuditRecord(String transaction_type, String desc) {
        this.username = Uv.currUser.getUsername();
        this.name = Uv.currUser.getName();
        this.timeStamp = Statics.getTimeStamp();
        this.transactionType = transaction_type;
        this.desc = desc;
    }

    public AuditRecord(String username, String name, String transactionType, String desc) {
        this.username = username;
        this.name = name;
        this.transactionType = transactionType;
        this.desc = desc;
        this.timeStamp = Statics.getTimeStamp();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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
}