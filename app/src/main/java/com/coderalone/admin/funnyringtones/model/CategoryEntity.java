package com.coderalone.admin.funnyringtones.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class CategoryEntity implements Parcelable {

    @SerializedName("id")
    private int categoryId;

    @SerializedName("name")
    private String categoryName;

    @SerializedName("image")
    private String categoryImage;

    @SerializedName("ads")
    private int isAds;

    public CategoryEntity() {
    }

    public CategoryEntity(int categoryId, String categoryName, String categoryImage, int isAds) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.isAds = isAds;
    }

    protected CategoryEntity(Parcel in) {
        categoryId = in.readInt();
        categoryName = in.readString();
        categoryImage = in.readString();
        isAds = in.readInt();
    }

    public static final Creator<CategoryEntity> CREATOR = new Creator<CategoryEntity>() {
        @Override
        public CategoryEntity createFromParcel(Parcel in) {
            return new CategoryEntity(in);
        }

        @Override
        public CategoryEntity[] newArray(int size) {
            return new CategoryEntity[size];
        }
    };

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public int getIsAds() {
        return isAds;
    }

    public void setIsAds(int isAds) {
        this.isAds = isAds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(categoryId);
        parcel.writeString(categoryName);
        parcel.writeString(categoryImage);
        parcel.writeInt(isAds);
    }
}
