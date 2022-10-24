package com.org.moneykeep.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.org.moneykeep.R;


public class AllTypePickerDialog extends Dialog implements View.OnClickListener {

    public AllTypePickerDialog(@NonNull Context context) {
        super(context);

    }

    public AllTypePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private NumberPicker PayOrIncomePicker, typePicker;
    private Button but_cancel, but_summit;
    private IOconfirmListener confirmListener;
    private IOnCancelListener cancelListener;

    public AllTypePickerDialog but_cancel(IOnCancelListener listener) {
        this.cancelListener = listener;
        return this;
    }

    public AllTypePickerDialog but_summit(IOconfirmListener listener) {
        this.confirmListener = listener;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置Dialog背景透明
        setContentView(R.layout.layout_alltypepicker);
        but_cancel = findViewById(R.id.but_cancel);
        but_summit = findViewById(R.id.but_summit);
        PayOrIncomePicker = findViewById(R.id.PayOrIncomePicker);
        typePicker = findViewById(R.id.typePicker);
        but_cancel.setOnClickListener(this);
        but_summit.setOnClickListener(this);

        String[] getPayOrIncome = {"支出", "收入", "全部", "银行卡"};
        PayOrIncomePicker.setMaxValue(getPayOrIncome.length - 1);
        PayOrIncomePicker.setMinValue(0);
        PayOrIncomePicker.setDisplayedValues(getPayOrIncome);
        PayOrIncomePicker.setValue(0);

        typePicker.setMaxValue(getContext().getResources().getStringArray(R.array.pay).length - 1);
        typePicker.setMinValue(0);
        typePicker.setDisplayedValues(getContext().getResources().getStringArray(R.array.pay));
        typePicker.setValue(0);

        PayOrIncomePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        typePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        PayOrIncomePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                typePicker.setMaxValue(0);
                if (PayOrIncomePicker.getValue() == 0) {
                    typePicker.setDisplayedValues(getContext().getResources().getStringArray(R.array.pay));
                    typePicker.setMaxValue(getContext().getResources().getStringArray(R.array.pay).length - 1);
                    typePicker.setMinValue(0);
                    typePicker.setValue(0);
                } else if (PayOrIncomePicker.getValue() == 1) {
                    typePicker.setDisplayedValues(getContext().getResources().getStringArray(R.array.income));
                    typePicker.setMaxValue(getContext().getResources().getStringArray(R.array.income).length - 1);
                    typePicker.setMinValue(0);
                    typePicker.setValue(0);
                } else if (PayOrIncomePicker.getValue() == 2) {
                    String[] all = {"全部类型", "微信","支付宝"};
                    typePicker.setDisplayedValues(all);
                    typePicker.setMaxValue(all.length - 1);
                    typePicker.setMinValue(0);
                    typePicker.setValue(0);
                } else if (PayOrIncomePicker.getValue() == 3) {
                    typePicker.setDisplayedValues(getContext().getResources().getStringArray(R.array.BankCard));
                    typePicker.setMaxValue(getContext().getResources().getStringArray(R.array.BankCard).length - 1);
                    typePicker.setMinValue(0);
                    typePicker.setValue(0);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.but_cancel:
                if (cancelListener != null) {
                    cancelListener.oncancel(this);
                }
                dismiss();
                break;
            case R.id.but_summit:
                int firstValue = PayOrIncomePicker.getValue();
                int secondValue = typePicker.getValue();
                if (confirmListener != null) {
                    confirmListener.oncofirm(this, firstValue, secondValue);
                }
                dismiss();
                break;
        }
    }

    public interface IOnCancelListener {
        void oncancel(AllTypePickerDialog dialog);

    }

    public interface IOconfirmListener {
        void oncofirm(AllTypePickerDialog dialog, int firstValue, int secondValue);
    }
}
