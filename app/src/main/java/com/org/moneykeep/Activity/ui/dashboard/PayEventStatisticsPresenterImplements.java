package com.org.moneykeep.Activity.ui.dashboard;

import android.content.Context;

import androidx.annotation.NonNull;

import com.org.moneykeep.AAChartCoreLib.AAChartCreator.AAChartModel;
import com.org.moneykeep.AAChartCoreLib.AAChartCreator.AASeriesElement;
import com.org.moneykeep.AAChartCoreLib.AAChartEnum.AAChartType;
import com.org.moneykeep.AAChartCoreLib.AAOptionsModel.AAPie;
import com.org.moneykeep.Until.ChangeDouble;
import com.org.moneykeep.Until.ListGroupUntil;
import com.org.moneykeep.retrofitBean.PayOrIncomeEventListBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayEventStatisticsPresenterImplements implements PayEventStatisticsInterface.IPresenter {
    private PayEventStatisticsInterface.IView iView;
    private PayEventStatisticsInterface.IModel iModel;
    private AAChartModel PayChartModel;
    private AAChartModel IncomeChartModel;
    private AAChartModel ColumnModelPay;
    private String[] xDay;
    private Object[] incomeData;
    private Object[] payData;
    private Object[] PayColor;
    private Object[][] PayPieDate;
    private Object[] IncomeColor;
    private Object[][] IncomePieDate;
    private HashMap<String, String> map_color;

    public PayEventStatisticsPresenterImplements(PayEventStatisticsInterface.IView iView) {
        this.iView = iView;

        iModel = new PayEventStatisticsModelImplements(this);
        map_color = new HashMap<>();
        {
            map_color.put("餐饮", "#F3837B");
            map_color.put("交通", "#0B9A06");
            map_color.put("服饰", "#DBEA09");
            map_color.put("购物", "#28DC88");
            map_color.put("服务", "#FF018786");
            map_color.put("教育", "#F6AFAF");
            map_color.put("娱乐", "#9C27B0");
            map_color.put("运动", "#D500F9");
            map_color.put("生活缴费", "#EA002F");
            map_color.put("旅行", "#0091EA");
            map_color.put("宠物", "#F66898");
            map_color.put("医疗", "#FFC107");
            map_color.put("保险", "#4A148C");
            map_color.put("公益", "#9C7104");
            map_color.put("发红包", "#D50000");
            map_color.put("转账", "#B1033E");
            map_color.put("亲属卡", "#FFFF8D");
            map_color.put("做人情", "#F42E2A");
            map_color.put("其它支出", "#29B6F6");
            map_color.put("生意", "#26A69A");
            map_color.put("工资", "#1DE9B6");
            map_color.put("奖金", "#4527A0");
            map_color.put("收红包", "#D50000");
            map_color.put("收转账", "#B1033E");
            map_color.put("其它收入", "#0277BD");
            map_color.put("建设银行", "#103095");
            map_color.put("农业银行", "#0A9183");
            map_color.put("微信","#07C160");
        }
    }

    @Override
    public void GetDayPayStatistics(String user_account, String date) {
        iModel.SelectDayPayStatistics(user_account, date);
    }

    @Override
    public void GetMonthPayStatistics(String account, String month, String year) {
        iModel.SelectMonthPayStatistics(account,month,year);
    }

    @Override
    public void GetYearPayStatistics(String user_account, String selectYear) {
        iModel.SelectYearPayStatistics(user_account,selectYear);
    }

    @Override
    public void GetAllStatisticsPieSuccessfulCallBack(PayOrIncomeEventListBean body) {
        String message = null;

        if (body.getPayCount() == 0) {
            PayChartModel = new AAChartModel()
                    .chartType(AAChartType.Pie)
                    .title("支出构成")
                    .subtitle("总支出:" + 0)
                    .backgroundColor("#ffffff");
            message = "查询成功但没有支出数据...";
            //iView.GetDayPayStatisticsSuccessful("查询成功但今天没有支出数据...", PayChartModel);
        } else {
            PayChartModel =  getPayPieUntil(body.getAllPayList());
        }
        if (body.getIncomeCount() == 0) {
            IncomeChartModel = new AAChartModel()
                    .chartType(AAChartType.Pie)
                    .title("收入构成")
                    .subtitle("总收入:" + 0)
                    .backgroundColor("#ffffff");
            message = "查询成功但没有收入数据...";
            //iView.GetDayPayStatisticsSuccessful("查询成功但今天没有收入数据...", IncomeChartModel);
        }else{
            IncomeChartModel = getIncomePieUntil(body.getAllIncomeList());
        }
        if(body.getPayCount() == 0 && body.getIncomeCount() == 0){
            message = "查询成功但没有收支数据...";
        }
        iView.GetDayPayStatisticsSuccessful(message,PayChartModel,IncomeChartModel);

    }

    private AAChartModel getPayPieUntil(List<PayOrIncomeEventListBean.AllPayListDTO> list) {
        Map<String, List<PayOrIncomeEventListBean.AllPayListDTO>> monthTypeMap = ListGroupUntil.groupRetrofitMonthTypeList(list);
        double countCostPay = 0;
        PayColor = new String[monthTypeMap.size()];
        PayPieDate = new Object[monthTypeMap.size()][2];
        int i = 0;
        for (Map.Entry<String, List<PayOrIncomeEventListBean.AllPayListDTO>> entry : monthTypeMap.entrySet()) {
            PayColor[i] = map_color.getOrDefault(entry.getKey(), "#1296db");
            double thisCountCostPay = 0;
            for (PayOrIncomeEventListBean.AllPayListDTO typeList : entry.getValue()) {
                double cost = typeList.getCost();
                thisCountCostPay = ChangeDouble.addDouble(thisCountCostPay, cost);
            }
            PayPieDate[i][0] = entry.getKey();
            PayPieDate[i][1] = -thisCountCostPay;
            countCostPay = ChangeDouble.addDouble(countCostPay, thisCountCostPay);
            i++;
        }
        countCostPay = -countCostPay;
        PayChartModel = new AAChartModel()
                .chartType(AAChartType.Pie)
                .title("支出构成")
                .subtitle("总支出:" + countCostPay)
                .backgroundColor("#ffffff")
                .dataLabelsEnabled(true)//是否直接显示扇形图数据
                .colorsTheme(PayColor)
                .series(new AAPie[]{new AAPie()
                        .name("金额：")
                        .size(110f)
                        .data(PayPieDate)
                });
        return PayChartModel;
    }
    private AAChartModel getIncomePieUntil(List<PayOrIncomeEventListBean.AllIncomeListDTO> list) {
        Map<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> monthTypeMap = ListGroupUntil.groupRetrofitMonthIncomeTypeList(list);
        double countCostIncome = 0;
        IncomeColor = new String[monthTypeMap.size()];
        IncomePieDate = new Object[monthTypeMap.size()][2];
        int i = 0;
        for (Map.Entry<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> entry : monthTypeMap.entrySet()) {

            IncomeColor[i] = map_color.getOrDefault(entry.getKey(), "#1296db");
            double thisCountCostIncome = 0;
            for (PayOrIncomeEventListBean.AllIncomeListDTO typeList : entry.getValue()) {
                double cost = typeList.getCost();
                thisCountCostIncome = ChangeDouble.addDouble(thisCountCostIncome, cost);
            }
            IncomePieDate[i][0] = entry.getKey();
            IncomePieDate[i][1] = thisCountCostIncome;
            countCostIncome = ChangeDouble.addDouble(countCostIncome, thisCountCostIncome);
            i++;
        }
        IncomeChartModel = new AAChartModel()
                .chartType(AAChartType.Pie)
                .title("收入构成")
                .subtitle("总收入：" + countCostIncome)
                .backgroundColor("#ffffff")
                .dataLabelsEnabled(true)//是否直接显示扇形图数据
                .colorsTheme(IncomeColor)
                .series(new AAPie[]{new AAPie()
                        .name("金额：")
                        .size(110f)
                        .data(IncomePieDate)
                });

        return IncomeChartModel;
    }

    @Override
    public void GetAllStatisticsPieUnSuccessfulCallBack(String s) {
        iView.GetDayPayStatisticsUnSuccessful(s);
    }


    @Override
    public void GetMonthPayColumnsCallBack(@NonNull PayOrIncomeEventListBean body) {
        String Message = null;
        if (body.getPayCount() == 0 && body.getIncomeCount() == 0){
            ColumnModelPay = new AAChartModel()
                    .chartType(AAChartType.Bar)
                    .polar(false)
                    .title("收支对比")
                    .dataLabelsEnabled(false);
            Message = "查询成功但没有收支数据...";
        }else{
            Map<String, List<PayOrIncomeEventListBean.AllPayListDTO>> groupPayData = ListGroupUntil.groupRetrofitMonthPayDateList(body.getAllPayList());
            Map<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> groupIncomeData = ListGroupUntil.groupRetrofitMonthIncomeDateList(body.getAllIncomeList());

            payData = new Object[32];
            incomeData = new Object[32];

            for (Map.Entry<String, List<PayOrIncomeEventListBean.AllPayListDTO>> entry : groupPayData.entrySet()) {
                double countCostPay = 0;
                //double countCostIncome = 0;
                String date1 = entry.getKey();
                String[] date2 = date1.split("-");
                String day2 = date2[2];
                int day = Integer.parseInt(day2);
                for (PayOrIncomeEventListBean.AllPayListDTO costList : entry.getValue()) {
                    double cost = costList.getCost();
                    if (cost < 0) {
                        countCostPay = ChangeDouble.addDouble(countCostPay, cost);
                    } /*else if (cost > 0) {
                        countCostIncome = ChangeDouble.addDouble(countCostIncome, cost);
                    }*/
                }
                payData[day] = -countCostPay;
                //incomeData[day] = countCostIncome;
            }
            for (Map.Entry<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> entry : groupIncomeData.entrySet()) {
                //double countCostPay = 0;
                double countCostIncome = 0;
                String date1 = entry.getKey();
                String[] date2 = date1.split("-");
                String day2 = date2[2];
                int day = Integer.parseInt(day2);
                for (PayOrIncomeEventListBean.AllIncomeListDTO costList : entry.getValue()) {
                    double cost = costList.getCost();
                    if (cost > 0) {
                        countCostIncome = ChangeDouble.addDouble(countCostIncome, cost);
                    }
                }
                //payData[day] = -countCostPay;
                incomeData[day] = countCostIncome;
            }
            ColumnModelPay = new AAChartModel()
                    .chartType(AAChartType.Bar)
                    .polar(false)
                    .title("本月收支")
                    .dataLabelsEnabled(false).series(new Object[]{
                            new AASeriesElement().color("#D50000")
                                    .name("支出")
                                    .data(payData),
                            new AASeriesElement().color("#29B6F6")
                                    .name("收入")
                                    .data(incomeData)});
        }
        iView.GetMonthPayColumnsSuccessful(Message,ColumnModelPay);
    }


    @Override
    public void GetYearPayColumnsCallBack(PayOrIncomeEventListBean body) {
        String Message = null;
        xDay = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        payData = new Object[12];
        incomeData = new Object[12];
        if (body.getPayCount() == 0 && body.getIncomeCount() == 0){
            ColumnModelPay = new AAChartModel()
                    .chartType(AAChartType.Bar)
                    .polar(false)
                    .title("收支对比")
                    .dataLabelsEnabled(false)
                    .categories(xDay);
            Message = "查询成功但今年没有收支数据...";
        }else{
            Map<String, List<PayOrIncomeEventListBean.AllPayListDTO>> groupPayData = ListGroupUntil.groupRetrofitYearPayDateList(body.getAllPayList());
            Map<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> groupIncomeData = ListGroupUntil.groupRetrofitYearIncomeDateList(body.getAllIncomeList());
            for (Map.Entry<String, List<PayOrIncomeEventListBean.AllPayListDTO>> entry : groupPayData.entrySet()) {
                double countCostPay = 0;


                int month = Integer.valueOf(entry.getKey());

                for (PayOrIncomeEventListBean.AllPayListDTO costList : entry.getValue()) {
                    double cost = costList.getCost();
                    if (cost < 0) {
                        countCostPay = ChangeDouble.addDouble(countCostPay, cost);
                    }
                    payData[month - 1] = -countCostPay;

                }
            }
            for (Map.Entry<String, List<PayOrIncomeEventListBean.AllIncomeListDTO>> entry : groupIncomeData.entrySet()) {
                //double countCostPay = 0;
                double countCostIncome = 0;

                int month = Integer.valueOf(entry.getKey());

                for (PayOrIncomeEventListBean.AllIncomeListDTO costList : entry.getValue()) {
                    double cost = costList.getCost();
                    if (cost > 0) {
                        countCostIncome = ChangeDouble.addDouble(countCostIncome, cost);
                    }

                    incomeData[month - 1] = countCostIncome;
                }
            }
            ColumnModelPay = new AAChartModel()
                    .chartType(AAChartType.Bar)
                    .polar(false)
                    .dataLabelsEnabled(false)
                    .categories(xDay)
                    .title("本年收支")
                    .series(new Object[]{
                            new AASeriesElement().color("#D50000")
                                    .name("支出")
                                    .data(payData),
                            new AASeriesElement().color("#29B6F6")
                                    .name("收入")
                                    .data(incomeData)});
        }
        iView.GetYearPayColumnsSuccessful(Message,ColumnModelPay);

    }

}
