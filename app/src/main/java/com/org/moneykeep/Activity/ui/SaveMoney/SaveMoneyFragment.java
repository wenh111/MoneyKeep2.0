package com.org.moneykeep.Activity.ui.SaveMoney;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.org.moneykeep.Activity.AddSaveMoneyPlanView.AddSaveMoneyPlanActivity;
import com.org.moneykeep.Activity.PunchCardView.PunchCardActivity;
import com.org.moneykeep.R;
import com.org.moneykeep.RecyclerViewAdapter.RecyclerViewList.SaveMoneyAdapterList;
import com.org.moneykeep.RecyclerViewAdapter.SaveMoneyAdapter;
import com.org.moneykeep.databinding.FragmentSavemoneyBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SaveMoneyFragment extends Fragment implements SaveMoneyFragmentInterface.IView{
    private FragmentSavemoneyBinding binding;
    private RadioGroup radioGroup;
    private TextView add_event, count;
    private RadioButton radio_all, radio_12month, radio_30day, radio_4week, radio_52week, radio_365day, radio, radio_custom;
    private String user_account;
    private RecyclerView recyclerView;
    private List<SaveMoneyAdapterList> saveData = new ArrayList<>();
    private SaveMoneyAdapter saveMoneyAdapter;
    private SaveMoneyFragmentInterface.IPresenter iPresenter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSavemoneyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        iPresenter = new SaveMoneyFragmentPresenterImplements(getContext(),this);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences userdata = null;
        if (userdata == null) {
            userdata = getActivity().getSharedPreferences("user", MODE_PRIVATE);
        }
        user_account = userdata.getString("user_email", "");
        FindId();
        SetListen();
        saveMoneyAdapter = new SaveMoneyAdapter(getContext(),saveData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(saveMoneyAdapter);
        if (radio_all.isChecked()) {
            getAllProject();
        }
    }

    private boolean isdelete;

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences getBoolean = null;
        if (getBoolean == null) {
            getBoolean = getActivity().getSharedPreferences("DeleteOrUpdate", MODE_PRIVATE);
        }
        isdelete = getBoolean.getBoolean("start", false);
        if (isdelete == true) {
            radio = getView().findViewById(radioGroup.getCheckedRadioButtonId());
            if (radio_all == radio) {
                getAllProject();
            } else {
                String type = radio.getText().toString();
                getAppointProject(type);
            }
        }
        SharedPreferences.Editor editor = getBoolean.edit();
        editor.putBoolean("start", false);
        editor.commit();

    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void showAllSaveMoneyPlanEventSuccessful(String successfulMessage, int doingThing, List<SaveMoneyAdapterList> newSaveData) {
        if (newSaveData != null) {
            count.setText(doingThing + "项");
            //saveMoneyAdapter = new SaveMoneyAdapter(getContext(), saveData);
            newSaveData.sort(Comparator.comparing(SaveMoneyAdapterList::getWeight).
                    thenComparing(SaveMoneyAdapterList::getLongDDL));

            saveData = newSaveData;
            saveMoneyAdapter.setData(saveData);
            saveMoneyAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(saveMoneyAdapter);
            saveMoneyAdapter.setOnRecyclerItemClickListener((data, speed, nowState) -> {
                ComponentName componentName = new ComponentName(getActivity(), PunchCardActivity.class);
                Intent intent = new Intent();
                intent.setComponent(componentName);
                Bundle bundle = new Bundle();
                bundle.putString("objectId", data.getObjectId());
                bundle.putString("deadlineDate", data.getDeadlineDate());
                bundle.putDouble("targetMoney", data.getTargetMoney());
                bundle.putDouble("savedMoney", data.getSavedMoney());
                bundle.putString("speed", speed);
                bundle.putString("nowState", nowState);
                bundle.putString("title", data.getTitle());
                bundle.putString("type", data.getType());
                bundle.putString("date", data.getDate());
                bundle.putInt("amount", data.getAmount());
                bundle.putInt("remainingTimes",data.getRemainingTimes());
                bundle.putInt("everydaySave", data.getEveryday_save());
                intent.putExtras(bundle);
                startActivity(intent);
            });
            Log.i("LoadTest", "数据处理完成...");
        }else{
            if (saveMoneyAdapter != null) {
                saveData.clear();
                saveMoneyAdapter.setData(saveData);
                saveMoneyAdapter.notifyDataSetChanged();
            }
            count.setText(0 + "项");
            Toast.makeText(getContext(), successfulMessage, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void showAllSaveMoneyPlanEventUnSuccessful(String unsuccessfulMessage) {
        Toast.makeText(getContext(), unsuccessfulMessage, Toast.LENGTH_SHORT).show();
    }


    private class Onclick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String type;
            //String sql = "select * from SaveMoney where account = ? and type = ?";
            if(view.getId() == R.id.radio_all){
                getAllProject();
            }else if(view.getId() == R.id.add_event){
                ComponentName componentName = new ComponentName(getActivity(), AddSaveMoneyPlanActivity.class);
                Intent intent = new Intent();
                intent.setComponent(componentName);
                Bundle bundle = new Bundle();
                if (radioGroup.getCheckedRadioButtonId() != R.id.radio_all) {
                    bundle.putInt("RadioButtonId", radioGroup.getCheckedRadioButtonId());
                }
                intent.putExtras(bundle);
                startActivity(intent);
            }else{
                RadioButton radioAppoint = getView().findViewById(view.getId());
                type = radioAppoint.getText().toString();
                getAppointProject(type);
            }
            /*switch (view.getId()) {
                case R.id.radio_all:
                    getAllProject();
                    break;
                case R.id.radio_12month:
                    type = radio_12month.getText().toString();
                    getAppointProject(type);
                    break;
                case R.id.radio_30day:
                    type = radio_30day.getText().toString();
                    getAppointProject(type);
                    break;
                case R.id.radio_4week:
                    type = radio_4week.getText().toString();
                    getAppointProject(type);
                    break;
                case R.id.radio_52week:
                    type = radio_52week.getText().toString();
                    getAppointProject(type);
                    break;
                case R.id.radio_365day:
                    type = radio_365day.getText().toString();
                    getAppointProject(type);
                    break;
                case R.id.radio_custom:
                    type = radio_custom.getText().toString();
                    getAppointProject(type);
                    break;
                case R.id.add_event:

                    break;
            }*/
        }
    }


    private void getAppointProject(String type) {
        Log.i("LoadTest", "开始获取数据...");
        iPresenter.showAllSaveMoneyPlanEvent(user_account,type);
       /* String sql = "select * from SaveMoney where account = ? and type = ?";
        new BmobQuery<SaveMoney>().doSQLQuery(sql, new SQLQueryListener<SaveMoney>() {
            @Override
            public void done(BmobQueryResult<SaveMoney> bmobQueryResult, BmobException e) {
                if (e == null) {
                    int doingThing = 0;
                    List<SaveMoney> results = bmobQueryResult.getResults();
                    List<SaveMoneyAdapterList> newSaveData = new ArrayList<>();
                    if (results.size() > 0) {
                        for (SaveMoney list : results) {
                            SaveMoneyAdapterList saveMoneyAdapterList = new SaveMoneyAdapterList();
                            double savedMoney = list.getSavedMoney();
                            double targetMoney = list.getTargetMoney();
                            String type = list.getType();
                            Calendar c = Calendar.getInstance();
                            String date = list.getDate();
                            String deadlineDate = list.getDeadlineDate();
                            String[] dates = date.split("-");
                            String[] deadlineDates = deadlineDate.split("-");
                            String DDL = "";
                            for (String ddl : deadlineDates) {
                                DDL = DDL + ddl;
                            }
                            long longDDL = Long.parseLong(DDL);
                            String text = "";
                            String thismonth = "";
                            int everyday_save = (int) targetMoney / list.getAmount();
                            Log.i("everydaySave", String.valueOf(everyday_save));
                            if (c.get(Calendar.MONTH) + 1 < 10) {
                                thismonth = "0" + String.valueOf(c.get(Calendar.MONTH) + 1);
                            } else {
                                thismonth = String.valueOf(c.get(Calendar.MONTH) + 1);
                            }
                            String thisDate = "";
                            if (c.get(Calendar.DATE) < 10) {
                                thisDate = "0" + c.get(Calendar.DATE);
                            } else {
                                thisDate = String.valueOf(c.get(Calendar.DATE));
                            }
                            String custom_type = "无";
                            if (list.getCustomType() != null) {
                                custom_type = list.getCustomType();
                            }
                            if (type.contains("月") || (type.equals("自定义") && custom_type.contains("月"))) {
                                if (!(c.get(Calendar.YEAR) + thismonth).equals(dates[0] + dates[1]) || savedMoney == 0) {
                                    text = "未打卡";
                                } else {
                                    text = "已打卡";
                                }
                            } else if (type.contains("周") || (type.equals("自定义") && custom_type.contains("周"))) {
                                if (!(c.get(Calendar.YEAR) + String.valueOf(c.get(Calendar.WEEK_OF_YEAR))).equals(dates[0] + dates[3]) || savedMoney == 0) {
                                    text = "未打卡";
                                } else {
                                    text = "已打卡";
                                }
                            } else if (type.contains("天") || (type.equals("自定义") && custom_type.contains("日"))) {
                                //Log.i("everydaySave", ((c.get(Calendar.YEAR) + thismonth + thisDate).equals(dates[0] + dates[1] + dates[2])));
                                Log.i("everydaySave", c.get(Calendar.YEAR) + thismonth + thisDate + " : " + dates[0] + dates[1] + dates[2]);
                                if (!((c.get(Calendar.YEAR) + thismonth + thisDate)
                                        .equals(dates[0] + dates[1] + dates[2])) || savedMoney == 0) {

                                    text = "未打卡";
                                } else {
                                    text = "已打卡";
                                }
                            } else if (type.equals("自定义") && custom_type.contains("年")) {
                                if (c.get(Calendar.YEAR) != Integer.parseInt(dates[0]) || savedMoney == 0) {
                                    text = "未打卡";
                                } else {
                                    text = "已打卡";
                                }
                            }

                            if (c.get(Calendar.YEAR) < Integer.parseInt(dates[0])) {
                                text = "未开始";
                            } else if (c.get(Calendar.YEAR) == Integer.parseInt(dates[0]) && c.get(Calendar.MONTH) + 1 < Integer.parseInt(dates[1])) {
                                text = "未开始";
                            } else if (c.get(Calendar.YEAR) == Integer.parseInt(dates[0]) && c.get(Calendar.MONTH) + 1 == Integer.parseInt(dates[1])
                                    && c.get(Calendar.DAY_OF_MONTH) < Integer.parseInt(dates[2])) {
                                text = "未开始";
                            } else if (c.get(Calendar.YEAR) > Integer.parseInt(deadlineDates[0])) {
                                text = "未完成";
                            } else if (c.get(Calendar.YEAR) == Integer.parseInt(deadlineDates[0]) && c.get(Calendar.MONTH) + 1 > Integer.parseInt(deadlineDates[1])) {
                                text = "未完成";
                            } else if (c.get(Calendar.YEAR) == Integer.parseInt(deadlineDates[0]) && c.get(Calendar.MONTH) + 1 == Integer.parseInt(deadlineDates[1])
                                    && c.get(Calendar.DAY_OF_MONTH) > Integer.parseInt(deadlineDates[2])) {
                                text = "未完成";
                            }

                            if (savedMoney >= targetMoney) {
                                text = "已完成";
                            }
                            if (text.equals("未打卡") || text.equals("已打卡")) {
                                doingThing = doingThing + 1;
                            }
                            if (text.equals("未打卡")) {
                                saveMoneyAdapterList.setWeight(0);
                            } else if (text.equals("已打卡")) {
                                saveMoneyAdapterList.setWeight(1);
                            } else if (text.equals("已完成")) {
                                saveMoneyAdapterList.setWeight(2);
                            } else if (text.equals("未开始")) {
                                saveMoneyAdapterList.setWeight(3);
                            } else if (text.equals("未完成")) {
                                saveMoneyAdapterList.setWeight(4);
                            }

                            saveMoneyAdapterList.setAmount(list.getAmount());
                            saveMoneyAdapterList.setLongDDL(longDDL);
                            saveMoneyAdapterList.setText(text);
                            saveMoneyAdapterList.setTitle(list.getTitle());
                            saveMoneyAdapterList.setType(list.getType());
                            saveMoneyAdapterList.setDeadlineDate(list.getDeadlineDate());
                            saveMoneyAdapterList.setDate(list.getDate());
                            saveMoneyAdapterList.setTargetMoney(list.getTargetMoney());
                            saveMoneyAdapterList.setSavedMoney(list.getSavedMoney());
                            saveMoneyAdapterList.setObjectId(list.getObjectId());
                            saveMoneyAdapterList.setEveryday_save(everyday_save);
                            newSaveData.add(saveMoneyAdapterList);
                        }

                        count.setText(doingThing + "项");
                        saveMoneyAdapter = new SaveMoneyAdapter(getContext(), saveData);
                        newSaveData.sort(Comparator.comparing(SaveMoneyAdapterList::getWeight).
                                thenComparing(SaveMoneyAdapterList::getLongDDL));
                        saveData = newSaveData;
                        saveMoneyAdapter.setData(saveData);
                        saveMoneyAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(saveMoneyAdapter);
                        saveMoneyAdapter.setOnRecyclerItemClickListener(new SaveMoneyAdapter.OnRecyclerItemClickListener() {
                            @Override
                            public void OnRecyclerOnItemClickListener(String objectId, String date, String deadlineDate, double targetMoney,
                                                                      double savedMoney, String speed, String nowState,
                                                                      String Title, String Type, int everyday_save, int amount) {
                                *//*Toast.makeText(getContext(), "objectId:" + objectId, Toast.LENGTH_SHORT).show();
                                Log.i("position", "objectId:" + objectId);*//*
                                ComponentName componentName = new ComponentName(getActivity(), PunchCardActivity.class);
                                Intent intent = new Intent();
                                intent.setComponent(componentName);
                                Bundle bundle = new Bundle();
                                bundle.putString("objectId", objectId);
                                bundle.putString("deadlineDate", deadlineDate);
                                bundle.putDouble("targetMoney", targetMoney);
                                bundle.putDouble("savedMoney", savedMoney);
                                bundle.putString("speed", speed);
                                bundle.putString("nowState", nowState);
                                bundle.putString("title", Title);
                                bundle.putString("type", Type);
                                bundle.putString("date", date);
                                bundle.putInt("amount", amount);
                                bundle.putInt("everydaySave", everyday_save);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                    } else {
                        if (saveMoneyAdapter != null) {
                            saveData.clear();
                            saveMoneyAdapter.setData(saveData);
                            saveMoneyAdapter.notifyDataSetChanged();
                        }
                        Toast.makeText(getContext(), "没有计划...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "查询失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, user_account, type);*/
    }

    private void getAllProject() {
        //String sql = "select * from SaveMoney where account = ?";
        Log.i("LoadTest", "开始获取数据...");
        iPresenter.showAllSaveMoneyPlanEvent(user_account,"");
        /*new BmobQuery<SaveMoney>().doSQLQuery(sql, new SQLQueryListener<SaveMoney>() {
            @Override
            public void done(BmobQueryResult<SaveMoney> bmobQueryResult, BmobException e) {
                if (e == null) {
                    int doingThing = 0;
                    List<SaveMoney> results = bmobQueryResult.getResults();
                    Log.i("LoadTest", "数据获取完成...");
                    List<SaveMoneyAdapterList> newSaveData = new ArrayList<>();
                    if (results.size() != 0) {
                        for (SaveMoney list : results) {
                            SaveMoneyAdapterList saveMoneyAdapterList = new SaveMoneyAdapterList();
                            double savedMoney = list.getSavedMoney();
                            double targetMoney = list.getTargetMoney();
                            String type = list.getType();
                            Calendar c = Calendar.getInstance();
                            String date = list.getDate();
                            String deadlineDate = list.getDeadlineDate();
                            String[] dates = date.split("-");
                            String[] deadlineDates = deadlineDate.split("-");
                            String DDL = "";
                            for (String ddl : deadlineDates) {
                                DDL = DDL + ddl;
                            }
                            long longDDL = Long.parseLong(DDL);
                            int everyday_save = (int) targetMoney / list.getAmount();
                            Log.i("everydaySave", String.valueOf(everyday_save));
                            String text = "";
                            String thismonth = "";
                            String custom_type = "无";
                            if (list.getCustomType() != null) {
                                custom_type = list.getCustomType();
                            }
                            if (c.get(Calendar.MONTH) + 1 < 10) {
                                thismonth = "0" + String.valueOf(c.get(Calendar.MONTH) + 1);
                            } else {
                                thismonth = String.valueOf(c.get(Calendar.MONTH) + 1);
                            }
                            String thisDate = "";
                            if (c.get(Calendar.DATE) < 10) {
                                thisDate = "0" + c.get(Calendar.DATE);
                            } else {
                                thisDate = String.valueOf(c.get(Calendar.DATE));
                            }
                            if (type.contains("月") || (type.equals("自定义") && custom_type.contains("月"))) {
                                if (!(c.get(Calendar.YEAR) + thismonth).equals(dates[0] + dates[1]) || savedMoney == 0) {
                                    text = "未打卡";
                                } else {
                                    text = "已打卡";
                                }
                            } else if (type.contains("周") || (type.equals("自定义") && custom_type.contains("周"))) {
                                if (!(c.get(Calendar.YEAR) + String.valueOf(c.get(Calendar.WEEK_OF_YEAR))).equals(dates[0] + dates[3]) || savedMoney == 0) {
                                    text = "未打卡";
                                } else {
                                    text = "已打卡";
                                }
                            } else if (type.contains("天") || (type.equals("自定义") && custom_type.contains("日"))) {
                                if (!((c.get(Calendar.YEAR) + thismonth + thisDate)
                                        .equals(dates[0] + dates[1] + dates[2])) || savedMoney == 0) {
                                    text = "未打卡";
                                } else {
                                    text = "已打卡";
                                }
                            } else if (type.equals("自定义") && custom_type.contains("年")) {
                                if (c.get(Calendar.YEAR) != Integer.parseInt(dates[0]) || savedMoney == 0) {
                                    text = "未打卡";
                                } else {
                                    text = "已打卡";
                                }
                            }

                            if (c.get(Calendar.YEAR) < Integer.parseInt(dates[0])) {
                                text = "未开始";
                            } else if (c.get(Calendar.YEAR) == Integer.parseInt(dates[0]) && c.get(Calendar.MONTH) + 1 < Integer.parseInt(dates[1])) {
                                text = "未开始";
                            } else if (c.get(Calendar.YEAR) == Integer.parseInt(dates[0]) && c.get(Calendar.MONTH) + 1 == Integer.parseInt(dates[1])
                                    && c.get(Calendar.DAY_OF_MONTH) < Integer.parseInt(dates[2])) {
                                text = "未开始";
                            } else if (c.get(Calendar.YEAR) > Integer.parseInt(deadlineDates[0])) {
                                text = "未完成";
                            } else if (c.get(Calendar.YEAR) == Integer.parseInt(deadlineDates[0]) && c.get(Calendar.MONTH) + 1 > Integer.parseInt(deadlineDates[1])) {
                                text = "未完成";
                            } else if (c.get(Calendar.YEAR) == Integer.parseInt(deadlineDates[0]) && c.get(Calendar.MONTH) + 1 == Integer.parseInt(deadlineDates[1])
                                    && c.get(Calendar.DAY_OF_MONTH) > Integer.parseInt(deadlineDates[2])) {
                                text = "未完成";
                            }

                            if (savedMoney >= targetMoney) {
                                text = "已完成";
                            }

                            if (text.equals("未打卡") || text.equals("已打卡")) {
                                doingThing = doingThing + 1;
                            }
                            if (text.equals("未打卡")) {
                                saveMoneyAdapterList.setWeight(0);
                            } else if (text.equals("已打卡")) {
                                saveMoneyAdapterList.setWeight(1);
                            } else if (text.equals("已完成")) {
                                saveMoneyAdapterList.setWeight(2);
                            } else if (text.equals("未开始")) {
                                saveMoneyAdapterList.setWeight(3);
                            } else if (text.equals("未完成")) {
                                saveMoneyAdapterList.setWeight(4);
                            }

                            saveMoneyAdapterList.setAmount(list.getAmount());
                            saveMoneyAdapterList.setLongDDL(longDDL);
                            saveMoneyAdapterList.setText(text);
                            saveMoneyAdapterList.setTitle(list.getTitle());
                            saveMoneyAdapterList.setType(list.getType());
                            saveMoneyAdapterList.setDeadlineDate(list.getDeadlineDate());
                            saveMoneyAdapterList.setDate(list.getDate());
                            saveMoneyAdapterList.setTargetMoney(list.getTargetMoney());
                            saveMoneyAdapterList.setSavedMoney(list.getSavedMoney());
                            saveMoneyAdapterList.setObjectId(list.getObjectId());
                            saveMoneyAdapterList.setEveryday_save(everyday_save);
                            newSaveData.add(saveMoneyAdapterList);
                        }

                        count.setText(doingThing + "项");
                        saveMoneyAdapter = new SaveMoneyAdapter(getContext(), saveData);

                        newSaveData.sort(Comparator.comparing(SaveMoneyAdapterList::getWeight).
                                thenComparing(SaveMoneyAdapterList::getLongDDL));
                        *//*newSaveData.sort(new Comparator<SaveMoneyAdapterList>() {
                            @Override
                            public int compare(SaveMoneyAdapterList saveMoneyAdapterList, SaveMoneyAdapterList t1) {
                                return saveMoneyAdapterList.getWeight() - t1.getWeight();
                            }
                        });*//*
                        saveData = newSaveData;
                        saveMoneyAdapter.setData(saveData);
                        saveMoneyAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(saveMoneyAdapter);
                        Log.i("LoadTest", "数据处理完成...");
                        saveMoneyAdapter.setOnRecyclerItemClickListener(new SaveMoneyAdapter.OnRecyclerItemClickListener() {
                            @Override
                            public void OnRecyclerOnItemClickListener(String objectId, String date, String deadlineDate, double targetMoney,
                                                                      double savedMoney, String speed, String nowState,
                                                                      String Title, String Type, int everyday_save, int amount) {

                                ComponentName componentName = new ComponentName(getActivity(), PunchCardActivity.class);
                                Intent intent = new Intent();
                                intent.setComponent(componentName);
                                Bundle bundle = new Bundle();
                                bundle.putString("objectId", objectId);
                                bundle.putString("deadlineDate", deadlineDate);
                                bundle.putDouble("targetMoney", targetMoney);
                                bundle.putDouble("savedMoney", savedMoney);
                                bundle.putString("speed", speed);
                                bundle.putString("nowState", nowState);
                                bundle.putString("title", Title);
                                bundle.putString("type", Type);
                                bundle.putString("date", date);
                                bundle.putInt("amount", amount);
                                bundle.putInt("everydaySave", everyday_save);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                    } else {
                        if (saveMoneyAdapter != null) {
                            saveData.clear();
                            saveMoneyAdapter.setData(saveData);
                            saveMoneyAdapter.notifyDataSetChanged();
                        }
                        Toast.makeText(getContext(), "没有计划...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "查询失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, user_account);*/

    }

    private void SetListen() {
        Onclick onclick = new Onclick();
        radio_all.setOnClickListener(onclick);
        radio_12month.setOnClickListener(onclick);
        radio_30day.setOnClickListener(onclick);
        radio_4week.setOnClickListener(onclick);
        radio_52week.setOnClickListener(onclick);
        radio_365day.setOnClickListener(onclick);
        radio_custom.setOnClickListener(onclick);
        add_event.setOnClickListener(onclick);
    }

    private void FindId() {
        radio_custom = getView().findViewById(R.id.radio_custom);
        radioGroup = getView().findViewById(R.id.radioGroup);
        radio_all = getView().findViewById(R.id.radio_all);
        radio_12month = getView().findViewById(R.id.radio_12month);
        radio_30day = getView().findViewById(R.id.radio_30day);
        radio_4week = getView().findViewById(R.id.radio_4week);
        radio_52week = getView().findViewById(R.id.radio_52week);
        radio_365day = getView().findViewById(R.id.radio_365day);
        add_event = getView().findViewById(R.id.add_event);
        count = getView().findViewById(R.id.count);
        recyclerView = getView().findViewById(R.id.recyclerView);
    }
}
