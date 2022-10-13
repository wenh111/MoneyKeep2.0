package com.org.moneykeep.Activity.AddPayEventView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.org.moneykeep.Dialog.GetBankMessageDialog;
import com.org.moneykeep.Dialog.ThisTimePickerDialog;
import com.org.moneykeep.Dialog.TypePickerDialog;
import com.org.moneykeep.Excention.ErrorAddPayEvent;
import com.org.moneykeep.R;
import com.org.moneykeep.Until.InputFilterMinMax;

import java.util.Calendar;

public class AddPayEventActivity extends AppCompatActivity implements AddPayEventInterface.IView {
    private LocationClient mLocClient;
    private String addr, easy_Location, city, province, street, district;
    private String time, location, type, remark, summit_data, summit_time, summit_month, summit_year, summit_day;
    private double money;
    private ImageButton but_finish, but_summit;
    private boolean isFirstLoc = true;
    private CheckBox RadBut_pay, RadBut_income;
    private EditText ed_time, ed_location, ed_type, ed_money, ed_remark;
    private Button but_add;
    private AddPayEventInterface.IPresenter iPresenter;

    private ActivityResultLauncher<Intent> intentActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onActivityResult);

    public AddPayEventActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pay_event);

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags;

        iPresenter = new AddPayEventPresenterImplements(this, this);
        FindId();

        initLocation();

        SetListen();
        but_finish.getBackground().setAlpha(0);
        but_summit.getBackground().setAlpha(0);
        ed_time.setInputType(InputType.TYPE_NULL);
        ed_location.setInputType(InputType.TYPE_NULL);
        ed_type.setInputType(InputType.TYPE_NULL);
    }

    @SuppressLint("SetTextI18n")
    private void onActivityResult(ActivityResult result) {
        if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
            Intent intent = result.getData();
            Bundle bundle = intent.getExtras();
            String Location = bundle.getString("location_return", "");
            String my_location = bundle.getString("my_city_return", "");
            if (!Location.equals("")) {
                ed_location.setText(Location);
            } else if (!my_location.equals("")) {
                ed_location.setText(my_location + " " + district + " " + easy_Location);
            } else {
                ed_location.setText("");
            }

        } else {
            Toast.makeText(AddPayEventActivity.this, "请输入地址", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void AddPayEventSuccessful(String s) {

        Toast.makeText(AddPayEventActivity.this, "创建成功...", Toast.LENGTH_LONG).show();
        but_summit.setEnabled(true);
        finish();
    }

    @Override
    public void AddPayEventUnSuccessful(String s) {
        Toast.makeText(AddPayEventActivity.this, "创建失败...", Toast.LENGTH_LONG).show();
        but_summit.setEnabled(true);
    }

    public class MyLocationListenner extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (isFirstLoc) {
                isFirstLoc = false;
                //获取详细地址信息
                easy_Location = bdLocation.getLocationDescribe();
                addr = bdLocation.getAddrStr();
                city = bdLocation.getCity();
                province = bdLocation.getProvince();
                street = bdLocation.getStreet();
                district = bdLocation.getDistrict();
            }

        }
    }

    public void initLocation() {
        /**
         * 定位SDK是否同意隐私政策接口
         * false不同意：不支持定位，SDK抛出异常
         * true同意：支持定位功能
         */

        LocationClient.setAgreePrivacy(true);
        // 定位初始化时捕获异常
        try {
            mLocClient = new LocationClient(this);
            MyLocationListenner myListener = new MyLocationListenner();

            mLocClient.registerLocationListener(myListener);

            LocationClientOption option = new LocationClientOption();
            // 打开gps
            option.setOpenGps(true);
            // 设置坐标类型
            option.setIsNeedLocationDescribe(true);
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll");
            option.setScanSpan(1000);
            //option.setFirstLocType(LocationClientOption.FirstLocType.ACCURACY_IN_FIRST_LOC);
            option.setIsNeedAddress(true);
            option.setNeedNewVersionRgc(true);

            mLocClient.setLocOption(option);
            mLocClient.start();
        } catch (Exception e) {
            Toast.makeText(this, "定位出错", Toast.LENGTH_SHORT).show();
        }

    }

    private void SetListen() {
        Onclick onclick = new Onclick();
        OnFocusChange onFocusChange = new OnFocusChange();
        CheckBoxChangeListener checkBoxChangeListener = new CheckBoxChangeListener();

        //单选框
        RadBut_pay.setOnCheckedChangeListener(checkBoxChangeListener);
        RadBut_income.setOnCheckedChangeListener(checkBoxChangeListener);

        //点击事件
        ed_time.setOnClickListener(onclick);
        ed_location.setOnClickListener(onclick);
        ed_type.setOnClickListener(onclick);
        but_finish.setOnClickListener(onclick);
        but_summit.setOnClickListener(onclick);

        //输入框监听
        ed_time.setOnFocusChangeListener(onFocusChange);
        ed_location.setOnFocusChangeListener(onFocusChange);
        ed_type.setOnFocusChangeListener(onFocusChange);
        ed_money.setFilters(new InputFilter[]{new InputFilterMinMax(0, 1000000)});


        but_add.setOnClickListener(onclick);

    }

    //点击事件监听
    private class Onclick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.but_finish:
                    finish();
                    break;
                case R.id.ed_time:
                    getTime();
                    break;
                case R.id.ed_location:
                    getLocation();
                    break;
                case R.id.ed_type:
                    getType();
                    break;
                case R.id.but_summit:
                    summitData();
                    break;
                case R.id.but_add:
                    getMessage();
                    break;
            }
        }
    }

    private void getMessage() {
        GetBankMessageDialog dialog = new GetBankMessageDialog(this);
        dialog.setiOconfirmListener(new GetBankMessageDialog.IOconfirmListener() {
            @Override
            public void oncofirm(GetBankMessageDialog dialog, String message) {
                String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
                String message_remark = null;
                String IncomeOrPay = null;
                String AmountOfMoney = null;
                String message_type = null;
                if (message.contains("[建设银行]")) {
                    String[] number = message.split(",");
                    String tmp = number[0];

                    if (number[0].contains("（") && number[0].contains("）")) {
                        message_remark = number[0].substring(number[0].indexOf("（") + 1, number[0].indexOf("）"));
                        number[0] = number[0].substring(number[0].indexOf("）") + 1);
                        Log.v("dimos", "remark:" + message_remark);
                    }
                    if (number[0].contains("存入") || number[0].contains("收入")) {
                        if (number[0].contains("存入")) {
                            if (!(tmp.contains("（") && tmp.contains("）"))) {
                                message_remark = number[0].substring(0, number[0].lastIndexOf("月") - 1);
                            }
                            IncomeOrPay = number[0].substring(number[0].indexOf("存入"));
                            Log.i("dimos", number[0].substring(number[0].indexOf("存入")));
                        } else if (number[0].contains("收入")) {
                            if (!(tmp.contains("（") && tmp.contains("）"))) {
                                message_remark = number[0].substring(number[0].indexOf("分") + 1, number[0].indexOf("收入"));
                            }
                            IncomeOrPay = number[0].substring(number[0].indexOf("收入"));
                            Log.i("dimos", number[0].substring(number[0].indexOf("收入")));
                        }
                    } else if (number[0].contains("支出")) {
                        if (!(tmp.contains("（") && tmp.contains("）"))) {
                            message_remark = number[0].substring(number[0].indexOf("分") + 1, number[0].indexOf("支出"));
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
                    if (Double.parseDouble(AmountOfMoney) < 0) {
                        AmountOfMoney = String.valueOf(-Double.parseDouble(AmountOfMoney));
                        RadBut_pay.setChecked(true);
                    } else {
                        RadBut_income.setChecked(true);
                    }
                    Log.i("dimos", AmountOfMoney);
                    message_type = "建设银行";

                }
                ed_location.setText(city + " " + district + " " + easy_Location);
                ed_type.setText(message_type);
                ed_money.setText(AmountOfMoney);
                ed_remark.setText(message_remark);

            }
        }).show();
    }

    private void summitData() {
        but_summit.setEnabled(false);
        //String[] text  = {ed_time.getText().toString(),ed_location.getText().toString(),ed_type.getText().toString(),ed_money.getText().toString()};
        try {
            EditText[] editTexts = {ed_time, ed_location, ed_type, ed_money};
            if (ed_time.getText().toString().equals("")
                    || ed_location.getText().toString().equals("")
                    || ed_type.getText().toString().equals("")
                    || ed_money.getText().toString().equals("")) {
                for (EditText editText : editTexts) {
                    if (editText.getText().toString().equals("")) {
                        editText.requestFocus();
                        throw new ErrorAddPayEvent("信息不完善");
                    }
                }
            } else {
                but_summit.setEnabled(false);
                time = ed_time.getText().toString();
                location = ed_location.getText().toString();
                type = ed_type.getText().toString();
                remark = ed_remark.getText().toString();
                money = Double.valueOf(ed_money.getText().toString());
                if (RadBut_pay.isChecked()) {
                    money = -money;
                }
                iPresenter.AddPayEvent(time, location, type, remark, money);

                /*allPay.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e == null){
                            SharedPreferences keep = null;
                            if (keep == null) {
                                keep = getSharedPreferences("DeleteOrUpdate", Context.MODE_PRIVATE);
                            }
                            SharedPreferences.Editor user_editor = keep.edit();
                            user_editor.putBoolean("isdelete", true);
                            user_editor.commit();
                            Toast.makeText(AddPayEventActivity.this, "创建成功", Toast.LENGTH_LONG).show();
                            but_summit.setEnabled(true);
                            finish();
                        }else{
                            but_summit.setEnabled(true);
                            Toast.makeText(AddPayEventActivity.this, "创建失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });*/


            }
        } catch (ErrorAddPayEvent errorAddPayEvent) {
            Toast.makeText(AddPayEventActivity.this, "请完善信息...", Toast.LENGTH_LONG).show();
        }


    }

    private void getType() {
        //RadBut_pay, RadBut_income;
        if (RadBut_pay.isChecked()) {
            String pay = RadBut_pay.getText().toString();
            TypePickerDialog typePickerDialog = new TypePickerDialog(AddPayEventActivity.this, pay);
            typePickerDialog.but_summit(new TypePickerDialog.IOconfirmListener() {
                @Override
                public void oncofirm(TypePickerDialog dialog, int value) {
                    String[] payTypes = getResources().getStringArray(R.array.pay);
                    ed_type.setText(payTypes[value]);
                }
            }).show();
        } else if (RadBut_income.isChecked()) {
            String income = RadBut_income.getText().toString();
            TypePickerDialog typePickerDialog = new TypePickerDialog(AddPayEventActivity.this, income);
            typePickerDialog.but_summit(new TypePickerDialog.IOconfirmListener() {
                @Override
                public void oncofirm(TypePickerDialog dialog, int value) {
                    String[] incomeTypes = getResources().getStringArray(R.array.income);
                    ed_type.setText(incomeTypes[value]);
                }
            }).show();
            ;
        }
    }

    private void getLocation() {
        ComponentName componentName = new ComponentName(AddPayEventActivity.this, GetLocationActivity.class);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        Bundle bundle = new Bundle();
        bundle.putString("city", city);
        bundle.putString("street", street);
        bundle.putString("province", province);
        bundle.putString("district", district);
        intent.putExtras(bundle);
        intentActivityResultLauncher.launch(intent);
    }

    private void getTime() {
        ThisTimePickerDialog thisTimePickerDialog = new ThisTimePickerDialog(AddPayEventActivity.this);
        thisTimePickerDialog.but_summit(new ThisTimePickerDialog.IOconfirmListener() {
            @Override
            public void oncofirm(ThisTimePickerDialog dialog, String data, String time) {
                summit_data = data;
                ed_time.setText(data + "   " + time);
            }
        }).show();
    }

    //输入框聚焦监听
    private class OnFocusChange implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean b) {
            switch (view.getId()) {
                case R.id.ed_time:
                    if (b) {
                        getTime();
                    }
                    break;
                case R.id.ed_location:
                    if (b) {
                        getLocation();
                    }
                    break;
                case R.id.ed_type:
                    if (b) {
                        getType();
                    }
                    break;
            }
        }
    }

    //复选框响应代码
    public class CheckBoxChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            switch (compoundButton.getId()) {
                case R.id.RadBut_pay:
                    if (RadBut_pay.isChecked()) {
                        RadBut_pay.setChecked(true);
                        RadBut_income.setChecked(false);
                    } else {
                        RadBut_pay.setChecked(false);
                    }
                    break;

                case R.id.RadBut_income:
                    if (RadBut_income.isChecked()) {
                        RadBut_pay.setChecked(false);
                        RadBut_income.setChecked(true);
                    } else {
                        RadBut_income.setChecked(false);
                    }
                    break;

            }
        }
    }

    private void FindId() {
        but_finish = findViewById(R.id.but_finish);
        but_summit = findViewById(R.id.but_summit);
        RadBut_pay = findViewById(R.id.RadBut_pay);
        RadBut_income = findViewById(R.id.RadBut_income);
        ed_time = findViewById(R.id.ed_time);
        ed_location = findViewById(R.id.ed_location);
        ed_money = findViewById(R.id.ed_money);
        ed_type = findViewById(R.id.ed_type);
        ed_remark = findViewById(R.id.ed_remarks);
        but_add = findViewById(R.id.but_add);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        // 退出时销毁定位
        if (mLocClient != null) {
            mLocClient.stop();
        }

    }


}