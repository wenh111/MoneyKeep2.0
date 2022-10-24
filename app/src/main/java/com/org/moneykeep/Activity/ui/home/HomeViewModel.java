package com.org.moneykeep.Activity.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.org.moneykeep.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private MutableLiveData<String> date;
    private MutableLiveData<String> type;

    public MutableLiveData<Integer> since;
    public MutableLiveData<Integer> perPage;
    public MutableLiveData<Boolean> isOver;
    private final Map<String, Integer> img;

    public MutableLiveData<String> getDate() {
        return date;
    }

    public MutableLiveData<String> getType() {
        return type;
    }

    public void changDate(String changDate) {
        date.setValue(changDate);
    }

    public void changType(String changType) {
        type.setValue(changType);
    }

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        date = new MutableLiveData<>();
        type = new MutableLiveData<>();
        since = new MutableLiveData<>();
        perPage = new MutableLiveData<>();
        isOver = new MutableLiveData<>();
        img = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        int now_year = calendar.get(Calendar.YEAR);
        int now_month = calendar.get(Calendar.MONTH) + 1;
        int now_day = calendar.get(Calendar.DAY_OF_MONTH);
        String now_date = now_year + "-" + now_month + "-" + now_day;
        date.setValue(now_date);
        type.setValue("全部类型");
        isOver.setValue(false);
        since.setValue(-1);
        perPage.setValue(-1);
        mText.setValue("This is home fragment");

        {
            img.put("餐饮", R.mipmap.restaurant_128);
            img.put("交通", R.mipmap.traffic_128);
            img.put("服饰", R.mipmap.clothes_128);
            img.put("购物", R.mipmap.shopping_128);
            img.put("服务", R.mipmap.service_128);
            img.put("教育", R.mipmap.teacher_128);
            img.put("娱乐", R.mipmap.entertainment_128);
            img.put("运动", R.mipmap.motion_128);
            img.put("生活缴费", R.mipmap.living_payment_128);
            img.put("旅行", R.mipmap.travel_128);
            img.put("宠物", R.mipmap.pets_128);
            img.put("医疗", R.mipmap.medical__128);
            img.put("保险", R.mipmap.insurance_128);
            img.put("公益", R.mipmap.welfare_128);
            img.put("发红包", R.mipmap.envelopes_128);
            img.put("转账", R.mipmap.transfer_accounts_128);
            img.put("亲属卡", R.mipmap.kinship_card_128);
            img.put("做人情", R.mipmap.human_128);
            img.put("其它支出", R.mipmap.others_128);
            img.put("生意", R.mipmap.business_128);
            img.put("工资", R.mipmap.wages_128);
            img.put("奖金", R.mipmap.bonus_128);
            img.put("收红包", R.mipmap.get_envelopes_128);
            img.put("收转账", R.mipmap.get_transfer_accounts_128);
            img.put("其它收入", R.mipmap.others_128);
            img.put("建设银行", R.mipmap.construction_bank_128);
            img.put("农业银行", R.mipmap.agricultural_bank_128);
            img.put("微信", R.mipmap.wechat_128);
            img.put("支付宝", R.mipmap.alipay_128);
            img.put("全部类型", R.mipmap.all_128);
        }


    }

    public MutableLiveData<Boolean> getIsOver() {
        return isOver;
    }

    public MutableLiveData<Integer> getSince() {
        return since;
    }

    public MutableLiveData<Integer> getPerPage() {
        return perPage;
    }

    public void dataChange(int cSince, int cPerPage) {
        since.setValue(cSince);
        perPage.setValue(cPerPage);
    }

    public void dataLoadOver(boolean cIsOver) {
        isOver.setValue(cIsOver);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public int getSrc(String type) {
        return img.getOrDefault(type, R.mipmap.others_128);
    }
}