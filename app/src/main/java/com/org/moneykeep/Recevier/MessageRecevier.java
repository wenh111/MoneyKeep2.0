package com.org.moneykeep.Recevier;

import static android.content.Context.MODE_PRIVATE;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.org.moneykeep.Activity.AddPayEventView.AddPayEventAPI;
import com.org.moneykeep.Until.RetrofitUntil;
import com.org.moneykeep.Until.SmsHelper;
import com.org.moneykeep.retrofitBean.PayEventBean;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageRecevier extends BroadcastReceiver {
    public static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private boolean isFirstLoc = true;
    public String city, district, easy_Location, AmountOfMoney, type, IncomeOrPay, dString, address, user_account, remark;
    public LocationClient mLocClient;

    public MessageRecevier() {
        super();
        Log.v("successfulTest", "SmsRecevier create");
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences userdata = context.getSharedPreferences("user", MODE_PRIVATE);
        boolean user_islogin = userdata.getBoolean("user_isused", false);
        if (intent.getAction().equals(ACTION) && user_islogin) {
            dString = SmsHelper.getSmsBody(intent);
            address = SmsHelper.getSmsAddress(intent);
            if (address.equals("95533") || address.equals("95599")) {

                user_account = userdata.getString("user_email", "");

                if (address.equals("95533")) {
                    //支出，收入，存入
                    Log.v("dimos", "income");
                    IncomeOrPay = null;
                    String[] number = dString.split(",");
                    String tmp = number[0];
                    if (number[0].contains("（") && number[0].contains("）")) {
                        remark = number[0].substring(number[0].indexOf("（") + 1, number[0].indexOf("）"));
                        number[0] = number[0].substring(number[0].indexOf("）") + 1);
                        Log.v("dimos", "remark:" + remark);
                    }
                    if (number[0].contains("存入") || number[0].contains("收入")) {
                        Log.i("dimos", address + "," + number[0]);
                        if (number[0].contains("存入")) {
                            if (!(tmp.contains("（") && tmp.contains("）"))) {
                                remark = number[0].substring(0, number[0].lastIndexOf("月") - 1);
                            }
                            IncomeOrPay = number[0].substring(number[0].indexOf("存入"));
                            Log.i("dimos", number[0].substring(number[0].indexOf("存入")));
                        } else if (number[0].contains("收入")) {
                            if (!(tmp.contains("（") && tmp.contains("）"))) {
                                remark = number[0].substring(number[0].indexOf("分") + 1, number[0].indexOf("收入"));
                            }
                            IncomeOrPay = number[0].substring(number[0].indexOf("收入"));
                            Log.i("dimos", number[0].substring(number[0].indexOf("收入")));
                        }
                    } else if (number[0].contains("支出")) {
                        if (!(tmp.contains("（") && tmp.contains("）"))) {
                            remark = number[0].substring(number[0].indexOf("分") + 1, number[0].indexOf("支出"));
                        }
                        IncomeOrPay = "-" + number[0].substring(number[0].indexOf("支出"));
                        Log.i("dimos", IncomeOrPay);
                    }
                    StringBuilder money = new StringBuilder();
                    for (int i = 0; i < IncomeOrPay.length(); i++) {
                        if (IncomeOrPay.charAt(i) >= 45 && IncomeOrPay.charAt(i) <= 57) {
                            money.append(IncomeOrPay.charAt(i));
                        }
                    }

                    AmountOfMoney = money.toString();
                    Log.i("dimos", AmountOfMoney);
                    type = dString.substring(dString.indexOf("[") + 1, dString.indexOf("]"));

                }

                if (address.equals("95599") && dString.contains("交易人民币")) {
                    type = "农业银行";
                    String[] number = dString.split("，");
                    String body = number[0];
                    String remark_ok = body.substring(body.indexOf("完成"));
                    if (remark_ok.equals("（")) {
                        remark = body.substring(body.indexOf("完成") + 2, body.indexOf("("));
                    } else {
                        remark = body.substring(body.indexOf("完成") + 2, body.indexOf("人民币"));
                    }
                    IncomeOrPay = body.substring(body.indexOf("交易人民币"));
                    StringBuilder money = new StringBuilder();
                    for (int i = 0; i < IncomeOrPay.length(); i++) {
                        if (IncomeOrPay.charAt(i) >= 45 && IncomeOrPay.charAt(i) <= 57) {
                            money.append(IncomeOrPay.charAt(i));
                        }
                    }
                    AmountOfMoney = money.toString();
                    Log.i("dimos", "body:" + body);
                    Log.i("dimos", "remark:" + remark);
                    Log.i("dimos", "IncomeOrPay:" + IncomeOrPay);
                    Log.i("dimos", "AmountOfMoney:" + AmountOfMoney);
                }

                initLocation(context);

            } else {
                abortBroadcast();
            }

        } else if (intent.getAction().equals("ACTION")) {
            Log.i("successfulTest", "InMessageRecevier");
        } else {
            abortBroadcast();
        }
        //Log.i("dimos", "电话:" + address);
        //阻止广播继续传递，如果该receiver比系统的级别高，
        //abortBroadcast();
    }

    public void initLocation(Context context) {

        LocationClient.setAgreePrivacy(true);
        // 定位初始化时捕获异常
        try {
            mLocClient = new LocationClient(context);
            MyLocationListenner myListener = new MyLocationListenner(context);
            mLocClient.registerLocationListener(myListener);

            LocationClientOption option = new LocationClientOption();
            // 打开gps
            option.setOpenGps(true);
            // 设置坐标类型
            option.setIsNeedLocationDescribe(true);
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll");
            option.setIsNeedAddress(true);
            //option.setFirstLocType(LocationClientOption.FirstLocType.ACCURACY_IN_FIRST_LOC);
            mLocClient.setLocOption(option);
            mLocClient.start();
        } catch (Exception e) {
            Log.i("dimos", "定位出错" + e.getMessage());
            Toast.makeText(context, "定位出错" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    public class MyLocationListenner extends BDAbstractLocationListener {
        private final Context context;
        public MyLocationListenner(Context context) {
            this.context = context;
        }

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (isFirstLoc) {
                isFirstLoc = false;
                //获取详细地址信息
                city = bdLocation.getCity();
                district = bdLocation.getDistrict();
                easy_Location = bdLocation.getLocationDescribe();
                String location = city + " " + district + " " + easy_Location;
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int monthOfYear = calendar.get(Calendar.MONTH) + 1;
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);

                String s_min;
                if (min < 10) {
                    s_min = "0" + min;
                } else {
                    s_min = "" + min;
                }

                String time = hour + ":" + s_min;
                String date = year + "-" + monthOfYear + "-" + dayOfMonth;
                String s_year = String.valueOf(year);
                String s_month = String.valueOf(monthOfYear);
                String s_day = String.valueOf(dayOfMonth);

                String payTime = date + " " + time;

                PayEventBean allPay = new PayEventBean();
                allPay.setDate(date);
                allPay.setAccount(user_account);
                allPay.setPayTime(payTime);
                allPay.setLocation(location);
                allPay.setCost(Double.valueOf(AmountOfMoney));
                allPay.setCategory(type);
                allPay.setTime(time);
                allPay.setMonth(s_month);
                allPay.setYear(s_year);
                if (monthOfYear < 10 || dayOfMonth < 10) {
                    if (monthOfYear < 10) {
                        s_month = "0" + monthOfYear;
                    }
                    if (dayOfMonth < 10) {
                        s_day = "0" + dayOfMonth;
                    }
                }
                String s_date = s_year + s_month + s_day;
                int int_date = Integer.parseInt(s_date);
                allPay.setInt_date(int_date);
                if (remark != null) {
                    allPay.setRemark(remark);
                    Log.i("dimos", "\n" + "电话：" + address + "\n" + "类型：" + type + "\n" + "金额：" + AmountOfMoney +
                            "\n" + "地址：" + easy_Location + "\n" + "备注：" + remark);
                } else {
                    Log.i("dimos", "\n" + "电话：" + address + "\n" + "类型：" + type + "\n" + "金额：" + AmountOfMoney +
                            "\n" + "地址：" + easy_Location + "\n" + "备注：" + "无");
                }
                Log.v("dimos", "上传数据...");
                AddPayEventAPI api = RetrofitUntil.getRetrofit().create(AddPayEventAPI.class);
                Call<Integer> integerCall = api.InsertPayEvent(allPay);
                integerCall.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(@NonNull Call<Integer> call,@NonNull Response<Integer> response) {
                        SharedPreferences keep = context.getSharedPreferences("DeleteOrUpdate", Context.MODE_PRIVATE);
                        SharedPreferences.Editor user_editor = keep.edit();
                        user_editor.putBoolean("isdelete", true);
                        user_editor.apply();
                        Toast.makeText(context, "收支数据上传成功", Toast.LENGTH_LONG).show();
                        Log.i("dimos", "创建成功...");
                    }

                    @Override
                    public void onFailure(@NonNull Call<Integer> call,@NonNull Throwable t) {
                        Toast.makeText(context, "收支数据上传失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i("dimos", "创建失败...");
                    }
                });
                //阻止广播继续传递，如果该receiver比系统的级别高，
                abortBroadcast();

                // 退出时销毁定位
                if (mLocClient != null) {
                    isFirstLoc = true;
                    mLocClient.stop();
                }

            }

        }
    }
}