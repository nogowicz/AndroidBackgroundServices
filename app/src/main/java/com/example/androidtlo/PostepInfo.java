package com.example.androidtlo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class PostepInfo implements Parcelable {
    public int mPobranychBajtow;
    public int mRozmiar;
    public String mStatus;





    public PostepInfo(int pobranychBajtow, int rozmiar, String status) {
        mPobranychBajtow = pobranychBajtow;
        mRozmiar = rozmiar;
        mStatus = status;
    }

    public PostepInfo(Parcel parcel) {
        mPobranychBajtow = parcel.readInt();
        mRozmiar = parcel.readInt();
        mStatus = parcel.readString();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPobranychBajtow);
        dest.writeInt(mRozmiar);
        dest.writeString(mStatus);
    }


    public static final Parcelable.Creator<PostepInfo> CREATOR = new Parcelable.Creator<PostepInfo>() {

        @Override
        public PostepInfo createFromParcel(Parcel source) {
            return new PostepInfo(source);
        }

        @Override
        public PostepInfo[] newArray(int size) {
            return new PostepInfo[size];
        }
    };

}
