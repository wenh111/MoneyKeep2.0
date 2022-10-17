package com.org.moneykeep.Activity.OCRView;

import com.org.moneykeep.retrofitBean.PayEventBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OCRInertPayEventAPI {
    @POST("/OCRInsert/wechat")
    Call<Integer> OCRInsertPayEvent(@Body List<PayEventBean> payEventBeanList);

}
