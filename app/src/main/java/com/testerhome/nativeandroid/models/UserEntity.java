package com.testerhome.nativeandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bin Li on 2015/9/15.
 */
public class UserEntity implements Parcelable {
    /**
     * id : 104
     * login : seveniruby
     * name : 思寒
     * avatar_url : /user/large_avatar/104.jpg
     */

    private int id;
    private String login;
    private String name;
    private String avatar_url;

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.login);
        dest.writeString(this.name);
        dest.writeString(this.avatar_url);
    }

    public UserEntity() {
    }

    protected UserEntity(Parcel in) {
        this.id = in.readInt();
        this.login = in.readString();
        this.name = in.readString();
        this.avatar_url = in.readString();
    }

    public static final Parcelable.Creator<UserEntity> CREATOR = new Parcelable.Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel source) {
            return new UserEntity(source);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };
}
