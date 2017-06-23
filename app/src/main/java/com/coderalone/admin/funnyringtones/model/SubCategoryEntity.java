package com.coderalone.admin.funnyringtones.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SubCategoryEntity implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("image")
    private String image;

    @SerializedName("image_avarta")
    private String imageAvarta;

    @SerializedName("name")
    private String name;

    @SerializedName("totalItem")
    private int totalItem;

    private boolean adsItem;

    private ArrayList<RingtoneEntity> ringtoneList;

    private boolean mInitiallyExpanded;

    public SubCategoryEntity() {
    }

    public SubCategoryEntity(int id, String image, String imageAvarta, String name, int totalItem, boolean adsItem, ArrayList<RingtoneEntity> ringtoneList, boolean mInitiallyExpanded) {
        this.id = id;
        this.image = image;
        this.imageAvarta = imageAvarta;
        this.name = name;
        this.totalItem = totalItem;
        this.adsItem = adsItem;
        this.ringtoneList = ringtoneList;
        this.mInitiallyExpanded = mInitiallyExpanded;
    }

    protected SubCategoryEntity(Parcel in) {
        id = in.readInt();
        image = in.readString();
        imageAvarta = in.readString();
        name = in.readString();
        totalItem = in.readInt();
        adsItem = in.readByte() != 0;
        ringtoneList = in.createTypedArrayList(RingtoneEntity.CREATOR);
        mInitiallyExpanded = in.readByte() != 0;
    }

    public static final Creator<SubCategoryEntity> CREATOR = new Creator<SubCategoryEntity>() {
        @Override
        public SubCategoryEntity createFromParcel(Parcel in) {
            return new SubCategoryEntity(in);
        }

        @Override
        public SubCategoryEntity[] newArray(int size) {
            return new SubCategoryEntity[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageAvarta() {
        return imageAvarta;
    }

    public void setImageAvarta(String imageAvarta) {
        this.imageAvarta = imageAvarta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public boolean isAdsItem() {
        return adsItem;
    }

    public void setAdsItem(boolean adsItem) {
        this.adsItem = adsItem;
    }

    public ArrayList<RingtoneEntity> getRingtoneList() {
        return ringtoneList;
    }

    public void setRingtoneList(ArrayList<RingtoneEntity> ringtoneList) {
        this.ringtoneList = ringtoneList;
    }

    public boolean ismInitiallyExpanded() {
        return mInitiallyExpanded;
    }

    public void setmInitiallyExpanded(boolean mInitiallyExpanded) {
        this.mInitiallyExpanded = mInitiallyExpanded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(image);
        parcel.writeString(imageAvarta);
        parcel.writeString(name);
        parcel.writeInt(totalItem);
        parcel.writeByte((byte) (adsItem ? 1 : 0));
        parcel.writeTypedList(ringtoneList);
        parcel.writeByte((byte) (mInitiallyExpanded ? 1 : 0));
    }
}
