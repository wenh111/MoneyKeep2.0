package com.org.moneykeep.Activity.ui.home;

import android.annotation.SuppressLint;
import android.util.Log;

import com.org.moneykeep.Until.RetrofitUntil;
import com.org.moneykeep.retrofitBean.PayEventBean;
import com.org.moneykeep.retrofitBean.PayEventListBean;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragmentModelImplements implements HomeFragmentInterface.IModel{


    private final HomeFragmentInterface.IPresenter iPresenter;
    private final HomeFragmentAPI api;
    public HomeFragmentModelImplements(HomeFragmentInterface.IPresenter iPresenter) {
        this.iPresenter = iPresenter;
        Retrofit retrofit = RetrofitUntil.getRetrofit();
        api = retrofit.create(HomeFragmentAPI.class);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void SelectDayMessage(PayEventBean payEventBean) {
        Call<PayEventListBean> payEventListBeanCall = api.SelectDayPayMessage(payEventBean.getAccount(),
                payEventBean.getCategory(), payEventBean.getDate(),"","");
        Log.i("SelectDayMessageSuccessfulCallBack =====>", String.valueOf(payEventBean.getAccount()));
        Log.i("SelectDayMessageSuccessfulCallBack =====>", String.valueOf(payEventBean.getCategory()));
        Log.i("SelectDayMessageSuccessfulCallBack =====>", String.valueOf(payEventBean.getDate()));
        payEventListBeanCall.enqueue(new Callback<PayEventListBean>() {
            @Override
            public void onResponse(Call<PayEventListBean> call, Response<PayEventListBean> response) {
                Log.i("SelectDayMessageSuccessfulCallBack =====>", String.valueOf(response.code()));
                if(response.code() == HttpURLConnection.HTTP_OK){

                    iPresenter.SelectDayMessageSuccessfulCallBack(response.body());

                }
            }

            @Override
            public void onFailure(Call<PayEventListBean> call, Throwable t) {
                iPresenter.SelectDayMessageUnSuccessfulCallBack("查询失败:" + t.getMessage());
            }
        });
    }

/*    @Override
    public void SelectMonthMessage(PayEventBean payEventBean) {
        Call<PayEventListBean> payEventListBeanCall = api.SelectDayPayMessage(payEventBean.getAccount(),
                payEventBean.getCategory(), "", payEventBean.getMonth(), payEventBean.getYear());
        payEventListBeanCall.enqueue(new Callback<PayEventListBean>() {
            @Override
            public void onResponse(Call<PayEventListBean> call, Response<PayEventListBean> response) {
                if(response.code() == HttpURLConnection.HTTP_OK){

                    iPresenter.SelectMonthAndYearMessageSuccessfulCallBack(response.body());

                }
            }

            @Override
            public void onFailure(Call<PayEventListBean> call, Throwable t) {
                iPresenter.SelectMonthAndYearMessageUnSuccessfulCallBack("查询错误:" + t.getMessage());
            }
        });
    }

    @Override
    public void SelectYearMessage(PayEventBean payEventBean) {
        Call<PayEventListBean> payEventListBeanCall = api.SelectDayPayMessage(payEventBean.getAccount(),
                payEventBean.getCategory(), "", "", payEventBean.getYear());
        payEventListBeanCall.enqueue(new Callback<PayEventListBean>() {
            @Override
            public void onResponse(Call<PayEventListBean> call, Response<PayEventListBean> response) {
                if(response.code() == HttpURLConnection.HTTP_OK){

                    iPresenter.SelectMonthAndYearMessageSuccessfulCallBack(response.body());

                }
            }

            @Override
            public void onFailure(Call<PayEventListBean> call, Throwable t) {
                iPresenter.SelectMonthAndYearMessageUnSuccessfulCallBack("查询错误:" + t.getMessage());
            }
        });
    }*/

    @Override
    public void SelectAMonthMessage(PayEventBean payEventBean, int since, int perPage,int selectType) {
        Call<PayEventListBean> payEventListBeanCall = api.getPayEvent(payEventBean.getAccount(),
                payEventBean.getCategory(), payEventBean.getMonth(), payEventBean.getYear(),since,perPage, selectType, payEventBean.getDate());
        payEventListBeanCall.enqueue(new Callback<PayEventListBean>() {
            @Override
            public void onResponse(Call<PayEventListBean> call, Response<PayEventListBean> response) {
                if(response.code() == HttpURLConnection.HTTP_OK){
                    PayEventListBean listBean = response.body();
                    iPresenter.SelectAMonthOrYearMessageSuccessfulCallBack(listBean,selectType);
                    Log.i("payListDTOS", "count ------------------>" + listBean.getCount());
                    Log.i("payListDTOS", "since ------------------>" + listBean.getSince());
                    Log.i("payListDTOS", "pages ------------------>" + listBean.getPerPage());
                }
            }

            @Override
            public void onFailure(Call<PayEventListBean> call, Throwable t) {
                iPresenter.SelectAMonthOrYearMessageUnSuccessfulCallBack("查询错误:" + t.getMessage());
            }
        });
    }

    @Override
    public void deletePayEvent(int id) {
        Call<Integer> payEventListBeanCall = api.DeleteDayPayMessage(id);
        payEventListBeanCall.enqueue(new Callback<Integer>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Log.i("deletePayEventSuccessfulCallBack =======>", String.valueOf(response.code()));
                if(response.code() == HttpURLConnection.HTTP_OK){
                    iPresenter.deletePayEventSuccessfulCallBack("删除成功...");
                    Log.i("deletePayEventSuccessfulCallBack =======>", "id");
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                iPresenter.deletePayEventUnSuccessfulCallBack("删除失败:" + t.getMessage());
                Log.i("deletePayEventSuccessfulCallBack =======>", t.getMessage());
            }
        });
    }
}
