package com.coderalone.admin.funnyringtones.reponsitory;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.coderalone.admin.funnyringtones.app.MyApplication;
import com.coderalone.admin.funnyringtones.util.Constant;

public class BaseReponsitory {

    /**
     * Using volley library request to URL to get data from server.
     */
    public void sendStringRequest(final ReponsitoryListener reponsitoryListener, String url) {
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                reponsitoryListener.loadDataSuccess(json);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                reponsitoryListener.loadDataError();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.MAX_TIMEOUT_REQUEST,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().getRequestQueue().add(request);
    }
}
