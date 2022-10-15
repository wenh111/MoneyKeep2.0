package com.org.moneykeep.Until;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.org.moneykeep.BmobTable.AllPay;
import com.org.moneykeep.retrofitBean.PayEventListBean;
import com.org.moneykeep.retrofitBean.PayOrIncomeEventListBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListGroupUntil {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Map<String, List<AllPay>> groupMonthList(List<AllPay> dateList) {
        Map<String, List<AllPay>> map = dateList.stream().collect(Collectors.groupingBy(AllPay::getDate));
        return map;
    }

    public static Map<String, List<PayEventListBean.AllPayListDTO>> groupRetrofitMonthList(List<PayEventListBean.AllPayListDTO> dateList) {
        Map<String, List<PayEventListBean.AllPayListDTO>> map = dateList.stream().collect(Collectors.groupingBy(PayEventListBean.AllPayListDTO::getDate));
        return map;
    }

    public static Map<String, List<AllPay>> groupYearList(List<AllPay> monthList) {
        Map<String, List<AllPay>> map = monthList.stream().collect(Collectors.groupingBy(AllPay::getMonth));
        return map;
    }
    public static Map<String, List<PayOrIncomeEventListBean.AllPayListDTO>> groupRetrofitYearPayDateList(List<PayOrIncomeEventListBean.AllPayListDTO> monthTypeList) {
        Map<String, List<PayOrIncomeEventListBean.AllPayListDTO>> map = monthTypeList.stream().collect(Collectors.groupingBy(PayOrIncomeEventListBean.AllPayListDTO::getMonth));
        return map;
    }
    public static Map<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> groupRetrofitYearIncomeDateList(List<PayOrIncomeEventListBean.AllIncomeListDTO> monthTypeList) {
        Map<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> map = monthTypeList.stream().collect(Collectors.groupingBy(PayOrIncomeEventListBean.AllIncomeListDTO::getMonth));
        return map;
    }
    public static Map<String, List<AllPay>> groupMonthTypeList(List<AllPay> monthTypeList) {
        Map<String, List<AllPay>> map = monthTypeList.stream().collect(Collectors.groupingBy(AllPay::getCategory));
        return map;
    }

    public static Map<String, List<PayOrIncomeEventListBean.AllPayListDTO>> groupRetrofitMonthTypeList(List<PayOrIncomeEventListBean.AllPayListDTO> monthTypeList) {
        Map<String, List<PayOrIncomeEventListBean.AllPayListDTO>> map = monthTypeList.stream().collect(Collectors.groupingBy(PayOrIncomeEventListBean.AllPayListDTO::getCategory));
        return map;
    }
    public static Map<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> groupRetrofitMonthIncomeTypeList(List<PayOrIncomeEventListBean.AllIncomeListDTO> monthTypeList) {
        Map<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> map = monthTypeList.stream().collect(Collectors.groupingBy(PayOrIncomeEventListBean.AllIncomeListDTO::getCategory));
        return map;
    }
    public static Map<String, List<PayOrIncomeEventListBean.AllPayListDTO>> groupRetrofitMonthPayDateList(List<PayOrIncomeEventListBean.AllPayListDTO> monthTypeList) {
        Map<String, List<PayOrIncomeEventListBean.AllPayListDTO>> map = monthTypeList.stream().collect(Collectors.groupingBy(PayOrIncomeEventListBean.AllPayListDTO::getDate));
        return map;
    }
    public static Map<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> groupRetrofitMonthIncomeDateList(List<PayOrIncomeEventListBean.AllIncomeListDTO> monthTypeList) {
        Map<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> map = monthTypeList.stream().collect(Collectors.groupingBy(PayOrIncomeEventListBean.AllIncomeListDTO::getDate));
        return map;
    }

    public static Map<String, List<AllPay>> groupDayTimeList(List<AllPay> timeTypeList) {
        Map<String, List<AllPay>> map = timeTypeList.stream().collect(Collectors.groupingBy(AllPay::getTime));
        return map;
    }


}
