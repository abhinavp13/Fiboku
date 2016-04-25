package com.pabhinav.fiboku.models;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author pabhinav
 */
@Setter
@Getter
@ToString
public class UserData implements Parcelable {

    /** Name of the user who wants to login **/
    private String userName;

    /** Email Id of the user who wants to login **/
    private String emailId;

    /** Phone Number of the user who wants to login **/
    private String phoneNumber;

    public UserData(String userName, String emailId, String phoneNumber) {
        this.userName = userName;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
    }

    /*******************************************************/
    /****** Below methods make this class Parcelable *******/
    /******************************************************/

    protected UserData(Parcel in) {
        userName = in.readString();
        emailId = in.readString();
        phoneNumber = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(emailId);
        dest.writeString(phoneNumber);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserData> CREATOR = new Parcelable.Creator<UserData>() {
        @Override
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
        }

        @Override
        public UserData[] newArray(int size) {
            return new UserData[size];
        }
    };
}
