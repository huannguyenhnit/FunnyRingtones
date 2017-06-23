package com.coderalone.admin.funnyringtones.reponsitory;

public interface ReponsitoryListener<E> {

    // Called when data loading success.
    void loadDataSuccess(String json);

    // Called when data loading error.
    void loadDataError();
}
