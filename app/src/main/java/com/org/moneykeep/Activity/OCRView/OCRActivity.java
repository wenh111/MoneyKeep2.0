package com.org.moneykeep.Activity.OCRView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.dx.dxloadingbutton.lib.AnimationType;
import com.org.moneykeep.R;
import com.org.moneykeep.RecyclerViewAdapter.DayRecyclerViewAdapter;
import com.org.moneykeep.RecyclerViewAdapter.RecyclerViewList.DayPayOrIncomeList;
import com.org.moneykeep.Until.OCRFileUtil;
import com.org.moneykeep.Until.RetrofitUntil;
import com.org.moneykeep.databinding.ActivityOcractivityBinding;
import com.org.moneykeep.retrofitBean.PayEventBean;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class OCRActivity extends AppCompatActivity {
    private ActivityOcractivityBinding binding;
    private final ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    RecognizeService.recAccurateBasic(getApplicationContext(), OCRFileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                            new RecognizeService.ServiceListener() {
                                @Override
                                public void onResult(OCRBean OCRJason) {
                                    ShowMessages(OCRJason);
                                }

                                @Override
                                public void onResult(String error) {

                                }
                            });
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOcractivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags;

        binding.butFinish.getBackground().setAlpha(0);
        binding.buttonPick.getBackground().setAlpha(0);

        requestPermission();
        initAccessToken();
        setListen();


    }

    private void setListen() {
        Onclick onclick = new Onclick();
        binding.buttonPick.setOnClickListener(onclick);
        binding.butSummit.setOnClickListener(onclick);
        binding.butFinish.setOnClickListener(onclick);
    }

    private class Onclick implements View.OnClickListener {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_pick:
                    if (!checkTokenStatus()) {
                        return;
                    }
                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                            OCRFileUtil.getSaveFile(getApplicationContext()).getAbsolutePath());
                    intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                            CameraActivity.CONTENT_TYPE_GENERAL);
                    intentActivityResultLauncher.launch(intent);
                    break;
                case R.id.but_finish:
                    finish();
                    break;
                case R.id.but_summit:
                    binding.butSummit.setEnabled(false);
                    binding.butSummit.startLoading();
                    summitData(data);
                    break;
            }
        }
    }

    private void summitData(List<DayPayOrIncomeList> data) {
        if (data == null || data.size() == 0) {
            binding.butSummit.setEnabled(true);
            binding.butSummit.loadingFailed();
            ToastMakeText("不能上传空数据...");
            return;
        }
        binding.butSummit.setAnimationEndAction(new Function1<AnimationType, Unit>() {
            @Override
            public Unit invoke(AnimationType animationType) {
                finish();
                return null;
            }
        });
        SharedPreferences userdata = getSharedPreferences("user", MODE_PRIVATE);
        String user_account = userdata.getString("user_email", "");
        List<PayEventBean> payEventBeanList = new ArrayList<>();
        String[] datas;
        String month;
        String day;
        String int_data;
        for (DayPayOrIncomeList dayPayOrIncomeList : data) {
            datas = dayPayOrIncomeList.getDate().split("-");
            month = datas[1];
            day = datas[2];
            if (Integer.parseInt(datas[1]) < 10) {
                month = "0" + month;
            }
            if (Integer.parseInt(datas[2]) < 10) {
                day = "0" + day;
            }
            int_data = datas[0] + month + day;
            PayEventBean payEventBean = new PayEventBean();
            payEventBean.setAccount(user_account);
            payEventBean.setPayTime(dayPayOrIncomeList.getDate() + " " + dayPayOrIncomeList.getTime());
            payEventBean.setLocation(dayPayOrIncomeList.getLocation());
            payEventBean.setCategory(dayPayOrIncomeList.getCategory());
            payEventBean.setCost(Double.parseDouble(dayPayOrIncomeList.getCost()));
            payEventBean.setRemark(dayPayOrIncomeList.getRemark());
            payEventBean.setDate(dayPayOrIncomeList.getDate());
            payEventBean.setTime(dayPayOrIncomeList.getTime());
            payEventBean.setYear(datas[0]);
            payEventBean.setMonth(datas[1]);
            payEventBean.setInt_date(Integer.parseInt(int_data));
            payEventBeanList.add(payEventBean);
        }
        Retrofit retrofit = RetrofitUntil.getRetrofit();
        OCRInertPayEventAPI api = retrofit.create(OCRInertPayEventAPI.class);
        Call<Integer> integerCall = api.OCRInsertPayEvent(payEventBeanList);
        integerCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call, @NonNull Response<Integer> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    binding.butSummit.loadingSuccessful();
                    ToastMakeText("数据上传成功...");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Integer> call, @NonNull Throwable t) {
                ToastMakeText("数据上传失败:" + t.getMessage());
                binding.butSummit.setEnabled(true);
                binding.butSummit.loadingFailed();
            }
        });
    }

    private boolean isPermissionRequested = false;

    private void requestPermission() {
        if (!isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            @SuppressLint("InlinedApi") String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (permissionsList.isEmpty()) {
                return;
            } else {
                requestPermissions(permissionsList.toArray(new String[0]), 0);
            }
        }
    }

    private List<DayPayOrIncomeList> data;

    @SuppressLint("NotifyDataSetChanged")
    public void ShowMessages(OCRBean OCRJason) {

        StringBuilder sb = new StringBuilder();
        for (OCRBean.WordsResultDTO words : OCRJason.getWords_result()) {
            sb.append(words.getWords());
            sb.append("\n");
        }
        String dateWord;
        String YearAndMonth = null;
        String year = "";
        String month = "";
        String regex = "(^+\\d{4})([年])(1[0-2]|0?\\d)([月])";
        Pattern pattern = Pattern.compile(regex);    // 编译正则表达式
        int index = 0;
        List<OCRBean.WordsResultDTO> wordsResultList = OCRJason.getWords_result();
        for (OCRBean.WordsResultDTO word : OCRJason.getWords_result()) {
            dateWord = word.getWords();
            Matcher matcher = pattern.matcher(dateWord);
            if (matcher.find()) {
                YearAndMonth = matcher.group();
                year = YearAndMonth.substring(0, YearAndMonth.indexOf("年"));
                month = YearAndMonth.substring(YearAndMonth.indexOf("年") + 1, YearAndMonth.indexOf("月"));
                Log.v("regex", "index = " + index + "年月：" + OCRJason.getWords_result().get(index).getWords());
                if (OCRJason.getWords_result().get(index + 1).getWords().contains("收入")) {
                    index = index + 2;
                } else {
                    index = index + 3;
                }
                break;
            }
            index++;
        }
        if (YearAndMonth == null) {
            ToastMakeText("找不到年份索引...");
            return;
        }
        Log.v("regex", year + "年" + month + "月");
        Log.v("regex", sb.toString());
        List<DayPayOrIncomeList> data = OrganizeData(wordsResultList, index, year);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        DayRecyclerViewAdapter dayRecyclerViewAdapter = new DayRecyclerViewAdapter(getApplicationContext(), new ArrayList<>());
        binding.recyclerView.setAdapter(dayRecyclerViewAdapter);
        dayRecyclerViewAdapter.setData(data);
        dayRecyclerViewAdapter.notifyDataSetChanged();


        //binding.test.setText(sb.toString());
    }

    private List<DayPayOrIncomeList> OrganizeData(List<OCRBean.WordsResultDTO> wordsResultList, int index, String year) {
        String MonthAndDayRegex = "(1[0-2]|0?\\d)([月])([0-2]\\d|\\d|30|31)([日])(\\s?)([01]\\d|2[0-3])([：])([0-5]\\d)";
        String PayOrIncomeRegex = "^([+]?|[-])(\\d+)(\\.\\d{1,2})?$";
        Pattern MonthAndDayRegexCompile = Pattern.compile(MonthAndDayRegex);
        Pattern PayOrIncomeRegexCompile = Pattern.compile(PayOrIncomeRegex);
        String ResultDate = null;
        String ResultCost;
        String ResultRemark;
        String month;
        String day;
        String min = null;
        String sec = null;
        String date = null;
        String time = null;
        data = new ArrayList<>();
        for (int indexFor = index; indexFor < wordsResultList.size(); indexFor++) {
            Matcher matcher = PayOrIncomeRegexCompile.matcher(wordsResultList.get(indexFor).getWords());
            if (matcher.find()) {

                ResultCost = matcher.group();
                ResultRemark = wordsResultList.get(indexFor - 1).getWords();

                for (int dataSelect = indexFor; dataSelect < wordsResultList.size(); dataSelect++) {
                    Matcher matcher1 = MonthAndDayRegexCompile.matcher(wordsResultList.get(dataSelect).getWords());
                    if (matcher1.find()) {
                        ResultDate = matcher1.group();
                        month = ResultDate.substring(0, ResultDate.indexOf("月"));
                        day = ResultDate.substring(ResultDate.indexOf("月") + 1, ResultDate.indexOf("日"));
                        date = String.format("%s-%s-%s", year, month, day);
                        min = ResultDate.substring(ResultDate.indexOf("日") + 1, ResultDate.indexOf("："));
                        sec = ResultDate.substring(ResultDate.indexOf("：") + 1);
                        time = min + ":" + sec;
                        //payTime = date + " " + time;

                        break;
                    }
                }
                if(ResultRemark == null || ResultDate == null){
                    ToastMakeText("账单数据不完整...");
                    continue;
                }
                DayPayOrIncomeList dayPayOrIncomeList = new DayPayOrIncomeList();
                dayPayOrIncomeList.setCost(ResultCost);
                dayPayOrIncomeList.setRemark(ResultRemark);
                dayPayOrIncomeList.setDate(date);
                dayPayOrIncomeList.setTime(time);
                dayPayOrIncomeList.setLocation("来源：微信账单");
                dayPayOrIncomeList.setInt_time(Integer.parseInt(min + sec));
                dayPayOrIncomeList.setCategory("微信");
                Log.v("regex", "备注：" + dayPayOrIncomeList.getRemark() + "\n" +
                        "金额" + dayPayOrIncomeList.getCost() + "\n" +
                        "时间" + dayPayOrIncomeList.getDate() + "\n" +
                        "支付时间" + dayPayOrIncomeList.getTime() + "\n");
                data.add(dayPayOrIncomeList);

            }
        }
        return data;
    }

    private boolean hasGotToken = false;

    private void initAccessToken() {
        OCR.getInstance(getApplicationContext()).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
                Log.v("OCR", "licence方式获取token成功");
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.v("OCR", "licence方式获取token失败" + error.getMessage());
            }
        }, getApplicationContext());

    }
    private void ToastMakeText(String Message){
        Toast.makeText(getApplicationContext(), Message, Toast.LENGTH_LONG).show();
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            ToastMakeText("token还未成功获取。。。");
        }
        return hasGotToken;
    }
}