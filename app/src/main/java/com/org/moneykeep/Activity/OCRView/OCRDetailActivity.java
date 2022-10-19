package com.org.moneykeep.Activity.OCRView;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.org.moneykeep.Activity.DetailsView.DetailsActivity;
import com.org.moneykeep.Dialog.UpdateDialog;
import com.org.moneykeep.R;
import com.org.moneykeep.Until.UpdateList;
import com.org.moneykeep.databinding.ActivityOcrdetailBinding;

import java.util.HashMap;
import java.util.Map;


public class OCRDetailActivity extends AppCompatActivity {
    private ActivityOcrdetailBinding binding;
    private String cost;
    private String date;
    private String location;
    private String remark;
    private int position;
    private final Map<String, Integer> img = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOcrdetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags;
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
            img.put("微信", R.mipmap.wechat_128);
            img.put("建设银行", R.mipmap.construction_bank_128);
            img.put("农业银行", R.mipmap.agricultural_bank_128);
            img.put("全部类型", R.mipmap.all_128);
        }
        getMessage();

        setListen();

    }

    private UpdateList updateList;

    private void setListen() {
        binding.detailDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                ComponentName componentName = new ComponentName(OCRDetailActivity.this, OCRActivity.class);
                intent.setComponent(componentName);
                Bundle bundle = new Bundle();
                bundle.putBoolean("delete", true);
                bundle.putInt("position", position);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        binding.detailUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateList = new UpdateList();
                updateList.setType("微信");
                updateList.setCost(cost);
                updateList.setDate(date);
                updateList.setRemark(remark);
                updateList.setLocation(location);
                UpdateDialog dialog = new UpdateDialog(OCRDetailActivity.this);
                dialog.setList(updateList);
                dialog.setOnconfirmOnclickListener(new UpdateDialog.IOconfirmListener() {
                    @Override
                    public void oncofirm(UpdateDialog dialog, UpdateList newUpdateList) {
                        Glide.with(getApplicationContext()).load(img.getOrDefault(newUpdateList.getType(), R.mipmap.others_128)).into(binding.detailImageView);
                        binding.detailCost.setText(newUpdateList.getCost());
                        binding.detailTime.setText(newUpdateList.getDate());
                        binding.detailLocation.setText(newUpdateList.getLocation());
                        binding.detailType.setText((newUpdateList.getType()));
                        updateList = newUpdateList;
                        String[] dates = newUpdateList.getDate().split(" ");
                        Intent intent = new Intent();
                        ComponentName componentName = new ComponentName(OCRDetailActivity.this, OCRActivity.class);
                        intent.setComponent(componentName);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("update", true);
                        bundle.putInt("position", position);
                        bundle.putString("cost", newUpdateList.getCost());
                        bundle.putString("date",dates[0]);
                        bundle.putString("remark", newUpdateList.getRemark());
                        bundle.putString("time",dates[1]);
                        bundle.putString("location",newUpdateList.getLocation());
                        bundle.putString("category",newUpdateList.getType());
                        intent.putExtras(bundle);

                        /*dayPayOrIncomeList.setCost(ResultCost);
                        dayPayOrIncomeList.setRemark(ResultRemark);
                        dayPayOrIncomeList.setDate(date);
                        dayPayOrIncomeList.setTime(time);
                        dayPayOrIncomeList.setLocation("来源：微信账单");
                        dayPayOrIncomeList.setInt_time(Integer.parseInt(min + sec));
                        dayPayOrIncomeList.setCategory("微信");*/

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).show();
                dialog.setLinearLayout_locationINVISIBLE();


            }
        });
    }

    private void getMessage() {
        Bundle bundle = getIntent().getExtras();
        cost = bundle.getString("cost");
        date = bundle.getString("date");
        location = bundle.getString("location");
        remark = bundle.getString("remark");
        position = bundle.getInt("position");
        binding.detailCost.setText(cost);
        binding.detailTime.setText(date);
        binding.detailLocation.setText(location);
        binding.detailRemark.setText(remark);
    }
}