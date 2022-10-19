package com.org.moneykeep.Dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.org.moneykeep.R;
import com.org.moneykeep.Until.InputFilterMinMax;
import com.org.moneykeep.Until.UpdateList;

import java.util.Calendar;

public class UpdateDialog extends Dialog {

    private UpdateList list;


    public UpdateDialog(@NonNull Context context) {
        super(context);
    }

    public UpdateList getList() {
        return list;
    }

    public void setList(UpdateList list) {
        this.list = list;
    }

    public UpdateDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private IOnCancelListener iOnCancelListener;
    private IOconfirmListener iOconfirmListener;


    public UpdateDialog setOnCancelOnclickListener(IOnCancelListener iOnCancelListener) {
        this.iOnCancelListener = iOnCancelListener;
        return this;
    }

    public UpdateDialog setOnconfirmOnclickListener(IOconfirmListener iOconfirmListener) {
        this.iOconfirmListener = iOconfirmListener;
        return this;
    }

    public void setLinearLayout_locationINVISIBLE() {
        LinearLayout_location = findViewById(R.id.linearLayout_location);
        LinearLayout_location.setVisibility(View.VISIBLE);
    }

    public interface IOnCancelListener {
        void oncancel(UpdateDialog dialog);

    }

    public interface IOconfirmListener {
        void oncofirm(UpdateDialog dialog, UpdateList newUpdateList);
    }

    private Button but_cancel, but_summit, but_date, but_time, but_type;
    private RadioButton radio_pay, radio_income;
    private TextView tv_update, tv_remark, tv_update_location, tv_location;
    private EditText et_cost;
    private String cost, date, time, remark, type;
    private LinearLayout LinearLayout_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置Dialog背景透明
        setContentView(R.layout.layout_update_dialog);


        //设置宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = size.x;//设置dialog的宽度为当前手机屏幕的宽度*0.8
        getWindow().setAttributes(p);

        setCanceledOnTouchOutside(true);//设置为true时，点击空白的地方dialog会消失，设置为false则不会消失
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setWindowAnimations(R.style.BottomDialog_Animation);


        findId();
        setListen();
        setMessage();

    }

    private void setMessage() {
        UpdateList data = getList();
        cost = data.getCost();
        String[] dates = data.getDate().split(" ");
        date = dates[0];
        time = dates[dates.length - 1];
        type = data.getType();
        remark = data.getRemark();

        if (Double.parseDouble(cost) < 0) {
            radio_pay.setChecked(true);
            et_cost.setText(String.valueOf(-Double.parseDouble(cost)));
        } else {
            radio_income.setChecked(true);
            if (cost.contains("+")){
                et_cost.setText(cost.substring(1));
            }else{
                et_cost.setText(cost);
            }

        }

        but_date.setText(date);
        but_time.setText(time);
        but_type.setText(type);
        tv_remark.setText(remark);
        if (data.getLocation() != null) {
            tv_location.setText(data.getLocation());
        }

    }


    private class Onclick implements View.OnClickListener {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.but_summit:
                    UpdateList newUpdateList = new UpdateList();
                    //UpdateList oldUpdateList = getList();
                    double cost = Double.parseDouble(et_cost.getText().toString());

                    if (radio_pay.isChecked()) {
                        newUpdateList.setCost("-" + cost);
                    } else {
                        newUpdateList.setCost("+" + cost);
                    }
                    newUpdateList.setType(but_type.getText().toString());
                    newUpdateList.setRemark(tv_remark.getText().toString());

                    String newDate = but_date.getText().toString();
                    String newTime = but_time.getText().toString();
                    if (LinearLayout_location.getVisibility() == View.VISIBLE) {
                        newUpdateList.setLocation(tv_location.getText().toString());
                    }
                    newUpdateList.setDate(newDate + " " + newTime);
                    newUpdateList.setId(list.getId());

                    if (iOconfirmListener != null) {
                        iOconfirmListener.oncofirm(UpdateDialog.this, newUpdateList);
                    }
                    dismiss();
                    break;
                case R.id.but_cancel:
                    if (iOnCancelListener != null) {
                        iOnCancelListener.oncancel(UpdateDialog.this);
                    }
                    dismiss();
                    break;
                case R.id.but_date:
                    getDate();
                    break;
                case R.id.but_time:
                    getTime();
                    break;
                case R.id.but_type:
                    getType();
                    break;
                case R.id.tv_update:
                    getRemark();
                    break;
                case R.id.tv_update_location:
                    getLocationDialog();
                    break;

            }
        }
    }

    private void getLocationDialog() {
        UpdateRemarkDialog updateRemarkDialog = new UpdateRemarkDialog(getContext());
        updateRemarkDialog.setiOconfirmListener(new UpdateRemarkDialog.IOconfirmListener() {
            @Override
            public void oncofirm(UpdateRemarkDialog dialog, String oldLocation) {
                tv_location.setText(oldLocation);
            }
        });
        updateRemarkDialog.setOldRemark(tv_location.getText().toString());
        Window window = updateRemarkDialog.getWindow();
        window.setWindowAnimations(R.style.NullAnimationDialog);
        updateRemarkDialog.show();
    }

    private void getRemark() {
        UpdateRemarkDialog updateRemarkDialog = new UpdateRemarkDialog(getContext());
        updateRemarkDialog.setiOconfirmListener(new UpdateRemarkDialog.IOconfirmListener() {
            @Override
            public void oncofirm(UpdateRemarkDialog dialog, String remark) {
                tv_remark.setText(remark);
            }
        });
        updateRemarkDialog.setOldRemark(tv_remark.getText().toString());
        Window window = updateRemarkDialog.getWindow();
        window.setWindowAnimations(R.style.NullAnimationDialog);
        updateRemarkDialog.show();
    }

    private void getType() {
        UpdateTypePickerDialog allTypePickerDialog = new UpdateTypePickerDialog(getContext());
        allTypePickerDialog.but_summit((dialog, firstValue, secondValue) -> {
            String[] stringArray;
            if (firstValue == 0) {
                stringArray = getContext().getResources().getStringArray(R.array.pay);
                but_type.setText(stringArray[secondValue]);
            } else if (firstValue == 1) {
                stringArray = getContext().getResources().getStringArray(R.array.income);
                but_type.setText(stringArray[secondValue]);
            } else if (firstValue == 2) {
                stringArray = getContext().getResources().getStringArray(R.array.BankCard);
                but_type.setText(stringArray[secondValue]);
            }
        });
        Window window = allTypePickerDialog.getWindow();
        window.setWindowAnimations(R.style.NullAnimationDialog);
        allTypePickerDialog.show();
    }

    private void getTime() {
        Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                int min = i1;
                String s_min = String.valueOf(i1);
                if (i1 < 10) {
                    s_min = "0" + i1;
                }
                but_time.setText(i + ":" + s_min);
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);

        Window window = timePickerDialog.getWindow();
        window.setWindowAnimations(R.style.NullAnimationDialog);
        timePickerDialog.show();
    }

    private void getDate() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                int month = i1 + 1;
                but_date.setText(i + "-" + month + "-" + i2);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        Window window = datePickerDialog.getWindow();
        window.setWindowAnimations(R.style.NullAnimationDialog);

        datePickerDialog.show();
    }


    private void findId() {
        tv_update = findViewById(R.id.tv_update);
        tv_location = findViewById(R.id.tv_location);
        tv_remark = findViewById(R.id.tv_remark);
        but_summit = findViewById(R.id.but_summit);
        but_cancel = findViewById(R.id.but_cancel);
        but_date = findViewById(R.id.but_date);
        but_time = findViewById(R.id.but_time);
        but_type = findViewById(R.id.but_type);
        radio_pay = findViewById(R.id.radio_pay);
        radio_income = findViewById(R.id.radio_income);
        et_cost = findViewById(R.id.et_cost);
        LinearLayout_location = findViewById(R.id.linearLayout_location);
        tv_update_location = findViewById(R.id.tv_update_location);
    }

    private void setListen() {
        Onclick onclick = new Onclick();
        but_summit.setOnClickListener(onclick);
        but_cancel.setOnClickListener(onclick);
        but_date.setOnClickListener(onclick);
        but_time.setOnClickListener(onclick);
        but_type.setOnClickListener(onclick);
        tv_update.setOnClickListener(onclick);
        tv_update_location.setOnClickListener(onclick);
        et_cost.setFilters(new InputFilter[]{new InputFilterMinMax(0, 1000000)});
    }


}
