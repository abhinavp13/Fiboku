package com.pabhinav.fiboku.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author pabhinav
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Message implements Parcelable {

    private final String phoneNumber;
    private final String message;
    private final String timeForMessage;
    private final boolean messageRead;

    private Message(String phoneNumber, String message, String timeForMessage, boolean messageRead){
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.timeForMessage = timeForMessage;
        this.messageRead = messageRead;
    }

    public static class MessageBuilder {

        private String phoneNumber;
        private String message;
        private String timeForMessage;
        private boolean messageRead;

        public MessageBuilder addPhoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }

        public MessageBuilder addMessage(String message){
            this.message = message;
            return this;
        }

        public MessageBuilder addTimeForMessage(String timeForMessage){
            this.timeForMessage = timeForMessage;
            return this;
        }

        public MessageBuilder addMessageRead(boolean messageRead){
            this.messageRead = messageRead;
            return this;
        }

        public Message build(){
            return new Message(phoneNumber, message, timeForMessage, messageRead);
        }
    }



    /*******************************************************/
    /****** Below methods make this class Parcelable *******/
    /******************************************************/

    protected Message(Parcel in) {
        phoneNumber = in.readString();
        message = in.readString();
        timeForMessage = in.readString();
        messageRead = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phoneNumber);
        dest.writeString(message);
        dest.writeString(timeForMessage);
        dest.writeByte((byte) (messageRead ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

}
