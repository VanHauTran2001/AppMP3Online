package com.example.appmp3online.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class BaiHat implements Parcelable {
        private String nameSong;
        private String nameSinger;
        private String linkSong;

    public BaiHat(String nameSong, String nameSinger, String linkSong) {
        this.nameSong = nameSong;
        this.nameSinger = nameSinger;
        this.linkSong = linkSong;
    }



    public BaiHat() {
    }

    protected BaiHat(Parcel in) {
        nameSong = in.readString();
        nameSinger = in.readString();
        linkSong = in.readString();
    }

    public static final Creator<BaiHat> CREATOR = new Creator<BaiHat>() {
        @Override
        public BaiHat createFromParcel(Parcel in) {
            return new BaiHat(in);
        }

        @Override
        public BaiHat[] newArray(int size) {
            return new BaiHat[size];
        }
    };

    public String getNameSong() {
        return nameSong;
    }

    public void setNameSong(String nameSong) {
        this.nameSong = nameSong;
    }

    public String getNameSinger() {
        return nameSinger;
    }

    public void setNameSinger(String nameSinger) {
        this.nameSinger = nameSinger;
    }

    public String getLinkSong() {
        return linkSong;
    }

    public void setLinkSong(String linkSong) {
        this.linkSong = linkSong;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nameSong);
        parcel.writeString(nameSinger);
        parcel.writeString(linkSong);
    }
}
