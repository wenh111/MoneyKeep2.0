package com.org.moneykeep.Activity.DetailsView;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.org.moneykeep.Until.RetrofitUntil;
import com.org.moneykeep.retrofitBean.PayEventBean;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailsModelImplements implements DetailsInterface.IModel {
    private final DetailsInterface.IPresenter iPresenter;
    private final DetailsAPI api;

    public DetailsModelImplements(DetailsInterface.IPresenter iPresenter) {
        this.iPresenter = iPresenter;
        Retrofit retrofit = RetrofitUntil.getRetrofit();
        api = retrofit.create(DetailsAPI.class);
    }

    @Override
    public void getDetailsMessage(int id) {
        Call<PayEventBean> payEventBeanCall = api.SelectDetailsPayMessage(id);
        payEventBeanCall.enqueue(new Callback<PayEventBean>() {
            @Override
            public void onResponse(Call<PayEventBean> call, Response<PayEventBean> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    iPresenter.getDetailsMessageSuccessfulCallBack(response.body());
                }
            }

            @Override
            public void onFailure(Call<PayEventBean> call, Throwable t) {
                iPresenter.getDetailsMessageUnSuccessfulCallBack("查询失败:" + t.getMessage());
            }
        });
    }

    @Override
    public void UpdateMessage(PayEventBean allPay) {
        Call<Integer> integerCall = api.UpdatePayMessage(allPay);
        integerCall.enqueue(new Callback<Integer>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    iPresenter.UpdateMessageSuccessfulCallBack("更新成功...");
                    Log.i("UpdateMessageSuccessfulCallBack =====>", String.valueOf(response.body()));
                } else {
                    iPresenter.UpdateMessageUnSuccessfulCallBack(String.valueOf(response.code()));
                    Log.i("UpdateMessageUnSuccessfulCallBack =====>", String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                iPresenter.UpdateMessageUnSuccessfulCallBack("更新失败:" + t.getMessage());
            }
        });
    }

    @Override
    public void deleteMessage(int id) {
        Call<Integer> integerCall = api.DeletePayMessage(id);
        integerCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    iPresenter.deleteMessageSuccessfulCallBack("删除成功...");
                } else {
                    iPresenter.deleteMessageUnSuccessfulCallBack("删除失败:" + "code = " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                iPresenter.deleteMessageUnSuccessfulCallBack("删除失败:" + t.getMessage());
            }
        });
    }
}
