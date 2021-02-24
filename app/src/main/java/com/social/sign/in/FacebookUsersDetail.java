package com.social.sign.in;

import android.os.Parcel;
import android.os.Parcelable;

public class FacebookUsersDetail implements Parcelable {

    String name;
    String firstName;
    String lastName;
    String email;
    String facebookId;
    String photoUrl;

    public FacebookUsersDetail() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public static Creator<FacebookUsersDetail> getCREATOR() {
        return CREATOR;
    }

    protected FacebookUsersDetail(Parcel in) {
        name        = in.readString();
        firstName   = in.readString();
        lastName    = in.readString();
        email       = in.readString();
        facebookId  = in.readString();
        photoUrl    = in.readString();;
    }

    public static final Creator<FacebookUsersDetail> CREATOR = new Creator<FacebookUsersDetail>() {
        @Override
        public FacebookUsersDetail createFromParcel(Parcel in) {
            return new FacebookUsersDetail(in);
        }

        @Override
        public FacebookUsersDetail[] newArray(int size) {
            return new FacebookUsersDetail[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeString(facebookId);
        parcel.writeString(photoUrl);
    }
}
