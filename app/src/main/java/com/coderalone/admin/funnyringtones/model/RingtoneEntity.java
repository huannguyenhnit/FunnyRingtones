package com.coderalone.admin.funnyringtones.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RingtoneEntity implements Parcelable {
    @SerializedName("id")
    private int id;

    @SerializedName("sub_category_id")
    private int subCategoryId;

    @SerializedName("sub_category")
    private String subCategory;

    @SerializedName("name")
    private String name;

    @SerializedName("mp3")
    private String urlFile;

    @SerializedName("totalItem")
    private int totalItem;

    private boolean adsItem;
    private boolean isSelected;

    public RingtoneEntity() {
    }

    public RingtoneEntity(boolean adsItem) {
        this.adsItem = adsItem;
    }

    public RingtoneEntity(int id, int subCategoryId, String subCategory, String name, String urlFile, int totalItem, boolean adsItem, boolean isSelected) {
        this.id = id;
        this.subCategoryId = subCategoryId;
        this.subCategory = subCategory;
        this.name = name;
        this.urlFile = urlFile;
        this.totalItem = totalItem;
        this.adsItem = adsItem;
        this.isSelected = isSelected;
    }

    protected RingtoneEntity(Parcel in) {
        id = in.readInt();
        subCategoryId = in.readInt();
        subCategory = in.readString();
        name = in.readString();
        urlFile = in.readString();
        totalItem = in.readInt();
        adsItem = in.readByte() != 0;
        isSelected = in.readByte() != 0;
    }

    public static final Creator<RingtoneEntity> CREATOR = new Creator<RingtoneEntity>() {
        @Override
        public RingtoneEntity createFromParcel(Parcel in) {
            return new RingtoneEntity(in);
        }

        @Override
        public RingtoneEntity[] newArray(int size) {
            return new RingtoneEntity[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(subCategoryId);
        parcel.writeString(subCategory);
        parcel.writeString(name);
        parcel.writeString(urlFile);
        parcel.writeInt(totalItem);
        parcel.writeByte((byte) (adsItem ? 1 : 0));
        parcel.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RingtoneEntity that = (RingtoneEntity) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
