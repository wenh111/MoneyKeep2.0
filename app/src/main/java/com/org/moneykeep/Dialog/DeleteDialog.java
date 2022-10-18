package com.org.moneykeep.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.org.moneykeep.Activity.OCRView.OCRActivity;
import com.org.moneykeep.R;

public class DeleteDialog extends Dialog {
    public DeleteDialog( @NonNull Context context) {
        super(context);
    }
    public DeleteDialog(@NonNull Activity activity) {
        super(activity);
    }
    public DeleteDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    private IOnCancelListener iOnCancelListener;
    private IOconfirmListener iOconfirmListener;


    public DeleteDialog setiOnCancelListener(IOnCancelListener iOnCancelListener) {
        this.iOnCancelListener = iOnCancelListener;
        return this;
    }

    public DeleteDialog setiOconfirmListener(IOconfirmListener iOconfirmListener) {
        this.iOconfirmListener = iOconfirmListener;
        return this;
    }

    public interface IOnCancelListener {
        void oncancel(DeleteDialog dialog);

    }

    public interface IOconfirmListener {
        void oncofirm(DeleteDialog dialog);
    }

    public void setsText(String sText) {
        this.sText = sText;
    }


    private TextView text;
    private String sText = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置Dialog背景透明
        setContentView(R.layout.layout_delete_dialog);
        text = findViewById(R.id.text);
        Button but_summit = findViewById(R.id.but_summit);
        Button but_cancel = findViewById(R.id.but_cancel);

        if(!sText.equals("")){
            text.setText(sText);
        }
        but_summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iOconfirmListener != null) {
                    iOconfirmListener.oncofirm(DeleteDialog.this);
                }
                dismiss();
            }
        });

        but_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iOnCancelListener != null) {
                    iOnCancelListener.oncancel(DeleteDialog.this);
                }
                dismiss();
            }
        });
    }
}
