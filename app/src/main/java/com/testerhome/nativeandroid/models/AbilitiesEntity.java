package com.testerhome.nativeandroid.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bin Li on 2015/9/15.
 */
public class AbilitiesEntity implements Parcelable {
    /**
     * update : false
     * destroy : false
     */

    private boolean update;
    private boolean destroy;

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public boolean getUpdate() {
        return update;
    }

    public boolean getDestroy() {
        return destroy;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(update ? (byte) 1 : (byte) 0);
        dest.writeByte(destroy ? (byte) 1 : (byte) 0);
    }

    public AbilitiesEntity() {
    }

    protected AbilitiesEntity(Parcel in) {
        this.update = in.readByte() != 0;
        this.destroy = in.readByte() != 0;
    }

    public static final Parcelable.Creator<AbilitiesEntity> CREATOR = new Parcelable.Creator<AbilitiesEntity>() {
        @Override
        public AbilitiesEntity createFromParcel(Parcel source) {
            return new AbilitiesEntity(source);
        }

        @Override
        public AbilitiesEntity[] newArray(int size) {
            return new AbilitiesEntity[size];
        }
    };
}