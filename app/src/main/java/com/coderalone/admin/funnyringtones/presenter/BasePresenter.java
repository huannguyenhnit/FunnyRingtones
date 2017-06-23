package com.coderalone.admin.funnyringtones.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.coderalone.admin.funnyringtones.model.CategoryEntity;
import com.coderalone.admin.funnyringtones.model.RingtoneEntity;
import com.coderalone.admin.funnyringtones.model.SubCategoryEntity;
import com.coderalone.admin.funnyringtones.reponsitory.BaseReponsitory;
import com.coderalone.admin.funnyringtones.reponsitory.ReponsitoryListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public abstract class BasePresenter<T> implements ReponsitoryListener {
    // Init presenter.
    public abstract void init(BaseReponsitory reponsitory, PresenterListener presenterListener, Context context);

    /**
     * Parse json String to Object list.
     *
     * @param json   json String.
     * @param entity Class Entity.
     * @return ArrayList<T> data list.
     */
    protected ArrayList<T> loadDataSuccess(@NonNull String json, @NonNull Class<T> entity) throws IllegalAccessException, InstantiationException {
        Gson gson = new Gson();
        Object obj = entity.newInstance();
        if (obj instanceof CategoryEntity) {
            ArrayList<CategoryEntity> categoryList = gson.fromJson(json, new TypeToken<ArrayList<CategoryEntity>>() {
            }.getType());
            return (ArrayList<T>) categoryList;
        } else if (obj instanceof SubCategoryEntity) {
            ArrayList<SubCategoryEntity> subCategoryList = gson.fromJson(json, new TypeToken<ArrayList<SubCategoryEntity>>() {
            }.getType());
            return (ArrayList<T>) subCategoryList;
        } else if (obj instanceof RingtoneEntity) {
            ArrayList<RingtoneEntity> ringtoneList = gson.fromJson(json, new TypeToken<ArrayList<RingtoneEntity>>() {
            }.getType());
            return (ArrayList<T>) ringtoneList;
        }

        return null;
    }
}
