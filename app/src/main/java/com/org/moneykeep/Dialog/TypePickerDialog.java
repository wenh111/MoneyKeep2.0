package com.org.moneykeep.Dialog;

import android.annotation.SuppressLint;
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


public class TypePickerDialog extends Dialog implements View.OnClickListener {
    private String PayOrIncome;
    private String[] getPayOrIncome;

    public TypePickerDialog(@NonNull Context context, String payOrIncome) {
        super(context);
        PayOrIncome = payOrIncome;
    }

    public TypePickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private NumberPicker numberPicker;
    private Button but_cancel, but_summit;
    private IOconfirmListener confirmListener;
    private IOnCancelListener cancelListener;

    public TypePickerDialog but_cancel(IOnCancelListener listener) {
        this.cancelListener = listener;
        return this;
    }

    public TypePickerDialog but_summit(IOconfirmListener listener) {
        this.confirmListener = listener;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置Dialog背景透明
        setContentView(R.layout.layout_typepicker);
        but_cancel = findViewById(R.id.but_cancel);
        but_summit = findViewById(R.id.but_summit);
        numberPicker = findViewById(R.id.numberPicker);
        but_cancel.setOnClickListener(this);
        but_summit.setOnClickListener(this);


        if (PayOrIncome.equals("支出")) {
            getPayOrIncome = getContext().getResources().getStringArray(R.array.pay);
        } else {
            getPayOrIncome = getContext().getResources().getStringArray(R.array.income);
        }

        numberPicker.setMaxValue(getPayOrIncome.length - 1);
        numberPicker.setMinValue(0);
        numberPicker.setDisplayedValues(getPayOrIncome);

        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

    }

    @SuppressLint("NonConstantResourceId")
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
                int value = numberPicker.getValue();
                if (confirmListener != null) {
                    confirmListener.oncofirm(this, value);
                }
                dismiss();
                break;
        }
    }

    public interface IOnCancelListener {
        void oncancel(TypePickerDialog dialog);

    }

    public interface IOconfirmListener {
        void oncofirm(TypePickerDialog dialog, int value);
    }
}
