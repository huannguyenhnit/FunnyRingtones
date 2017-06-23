package com.coderalone.admin.funnyringtones.presenter;

import java.util.ArrayList;

public interface PresenterListener<E> {

    //Called when load data error.
    void loadDataError();

    interface CategoryPresenterListener<E> extends PresenterListener {

        //Show progress when loading data from server.
        void showLoadingProgress();

        //Called when load data success.
        void loadDataSuccess(ArrayList<E> categoryList);

        //Called when click to category item.
        void onClickItem(E item);

    }

    interface SubCategoryPresenterListener<E> extends PresenterListener {

        //Show progress when loading data from server.
        void showLoadingProgress();

        //Called when load data success.
        void loadDataSuccess(ArrayList<E> subCategoryList);

        //Called when click to sub-category item.
        void onClickItem(E item);
    }

    interface RingtonePresenterListener<E> extends PresenterListener {

        //Show progress when loading data from server.
        void showLoadingProgress();

        //Called when load data success.
        void loadDataSuccess(ArrayList<E> ringtoneList);

        //Called when click to ringtone item.
        void onClickItem(E item);

    }
}
