package in.ac.bkbiet.bkbiet.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import in.ac.bkbiet.bkbiet.utils.Statics;

/**
 * Chat Created by Ashish on 12/7/2017.
 */

public class Chat implements Parcelable, Comparable<Chat> {
    public static final Parcelable.Creator<Chat> CREATOR = new Parcelable.Creator<Chat>() {

        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    private String id;
    private Person firstPerson;
    private Person secondPerson;
    private String last_msg;
    private Message lastMessage;

    // TODO: 12/9/2017 remove it in next update (NOTE : if another way to call update in convo for updateChatReceipts
    private String last_msg_sent_at;

    public Chat() {
        // required for firebase
    }

    public Chat(String id, Person firstPerson, Person secondPerson, String last_msg) {
        this.id = id;
        this.firstPerson = firstPerson;
        this.secondPerson = secondPerson;
        this.last_msg = last_msg;
        this.last_msg_sent_at = Statics.getTimeStamp();
    }

    public Chat(Parcel in) {
        id = in.readString();
        last_msg = in.readString();
        last_msg_sent_at = in.readString();
        firstPerson = in.readParcelable(Person.class.getClassLoader());
        secondPerson = in.readParcelable(Person.class.getClassLoader());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Person getFirstPerson() {
        return firstPerson;
    }

    public void setFirstPerson(Person firstPerson) {
        this.firstPerson = firstPerson;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(String last_msg) {
        this.last_msg = last_msg;
    }

    public String getLast_msg_sent_at() {
        return last_msg_sent_at;
    }

    public void setLast_msg_sent_at(String last_msg_sent_at) {
        this.last_msg_sent_at = last_msg_sent_at;
    }

    public Person getSecondPerson() {
        return secondPerson;
    }

    public void setSecondPerson(Person secondPerson) {
        this.secondPerson = secondPerson;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(last_msg);
        dest.writeString(last_msg_sent_at);
        dest.writeParcelable(firstPerson, flags);
        dest.writeParcelable(secondPerson, flags);
    }

    @Override
    public int compareTo(Chat c) {
        try {
            int year, month, day, hour, min, sec;
            day = Integer.parseInt(last_msg_sent_at.substring(0, 2));
            month = Integer.parseInt(last_msg_sent_at.substring(3, 5));
            year = Integer.parseInt(last_msg_sent_at.substring(6, 10));
            hour = Integer.parseInt(last_msg_sent_at.substring(11, 13));
            min = Integer.parseInt(last_msg_sent_at.substring(14, 16));
            sec = Integer.parseInt(last_msg_sent_at.substring(17, 19));

            int cyear, cmonth, cday, chour, cmin, csec;
            cday = Integer.parseInt(c.getLast_msg_sent_at().substring(0, 2));
            cmonth = Integer.parseInt(c.getLast_msg_sent_at().substring(3, 5));
            cyear = Integer.parseInt(c.getLast_msg_sent_at().substring(6, 10));
            chour = Integer.parseInt(c.getLast_msg_sent_at().substring(11, 13));
            cmin = Integer.parseInt(c.getLast_msg_sent_at().substring(14, 16));
            csec = Integer.parseInt(c.getLast_msg_sent_at().substring(17, 19));


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

    @Exclude
    public Message getLastMessage() {
        return lastMessage;
    }

    @Exclude
    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public static class Person implements Parcelable {
        public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {

            public Person createFromParcel(Parcel in) {
                return new Person(in);
            }

            public Person[] newArray(int size) {
                return new Person[size];
            }
        };
        private String username;
        private String name;
        private String dp_link;
        //private int unreadMsgCount;
        private boolean blockingChat;

        public Person() {
            // for firebase
        }

        public Person(String username, String name, String dp_link, boolean blockingChat) {
            this.username = username;
            this.name = name;
            this.dp_link = dp_link;
            this.blockingChat = blockingChat;
        }

        public Person(Parcel in) {
            this.username = in.readString();
            this.name = in.readString();
            this.dp_link = in.readString();
            this.blockingChat = in.readInt() == 1;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDp_link() {
            return dp_link;
        }

        public void setDp_link(String dp_link) {
            this.dp_link = dp_link;
        }

        public boolean isBlockingChat() {
            return blockingChat;
        }

        public void setBlockingChat(boolean blockingChat) {
            this.blockingChat = blockingChat;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(username);
            dest.writeString(name);
            dest.writeString(dp_link);
            dest.writeInt(blockingChat ? 1 : 0);
        }
    }
}
