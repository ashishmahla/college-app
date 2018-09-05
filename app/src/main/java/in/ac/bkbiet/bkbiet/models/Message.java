package in.ac.bkbiet.bkbiet.models;

import in.ac.bkbiet.bkbiet.utils.Statics;
import in.ac.bkbiet.bkbiet.utils.Sv;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * Message model Created by Ashish on 12/7/2017.
 */

public class Message implements Comparable<Message> {
    public boolean showReceiptsToSender = false;
    private String mId;
    private String senderUsername;
    private String senderName;
    //receipts
    private String sentAt;
    private String receivedAt;
    private String seenAt;
    private String message;

    public Message() {
        //for firebase
    }

    public Message(String mId, String message) {
        this.mId = mId;
        this.message = message;
        senderUsername = Uv.currUser.getUsername();
        senderName = Uv.currUser.getName();
        sentAt = Statics.getTimeStamp();
        receivedAt = "not_yet_received";
        seenAt = "not_yet_seen";
        if (Sv.getBooleanSetting(Sv.dSHOW_RECEIPTS, false)) {
            showReceiptsToSender = true;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getSeenAt() {
        return seenAt;
    }

    public void setSeenAt(String seenAt) {
        this.seenAt = seenAt;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setShowReceiptsToSender(boolean showReceiptsToSender) {
        this.showReceiptsToSender = showReceiptsToSender;
    }

    @Override
    public int compareTo(Message m) {
        try {
            int year, month, day, hour, min, sec;
            day = Integer.parseInt(sentAt.substring(0, 2));
            month = Integer.parseInt(sentAt.substring(3, 5));
            year = Integer.parseInt(sentAt.substring(6, 10));
            hour = Integer.parseInt(sentAt.substring(11, 13));
            min = Integer.parseInt(sentAt.substring(14, 16));
            sec = Integer.parseInt(sentAt.substring(17, 19));

            int cyear, cmonth, cday, chour, cmin, csec;
            cday = Integer.parseInt(m.sentAt.substring(0, 2));
            cmonth = Integer.parseInt(m.sentAt.substring(3, 5));
            cyear = Integer.parseInt(m.sentAt.substring(6, 10));
            chour = Integer.parseInt(m.sentAt.substring(11, 13));
            cmin = Integer.parseInt(m.sentAt.substring(14, 16));
            csec = Integer.parseInt(m.sentAt.substring(17, 19));


            if (year != cyear)
                return cyear - year;
            else if (month != cmonth)
                return cmonth - month;
            else if (cday != day)
                return cday - day;
            else if (hour != chour)
                return chour - hour;
            else if (min != cmin)
                return cmin - min;
            else return csec - sec;
        } catch (NumberFormatException ignored) {
            ignored.printStackTrace();
        }
        return 0;
    }
}