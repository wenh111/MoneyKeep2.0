package com.org.moneykeep.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.org.moneykeep.Activity.DetailsView.DetailsActivity;
import com.org.moneykeep.Activity.ui.home.HomeFragmentAPI;
import com.org.moneykeep.Dialog.DeleteDialog;
import com.org.moneykeep.R;
import com.org.moneykeep.RecyclerViewAdapter.RecyclerViewList.DayPayOrIncomeList;
import com.org.moneykeep.RecyclerViewAdapter.RecyclerViewList.MonthPayOrIncomeList;
import com.org.moneykeep.Until.ChangeDouble;
import com.org.moneykeep.Until.RetrofitUntil;
import com.org.moneykeep.retrofitBean.PayEventListBean;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MonthRecyclerViewAdapter extends RecyclerView.Adapter<MonthRecyclerViewAdapter.LinearViewHolder> {

    private Context context;
    private List<MonthPayOrIncomeList> monthData;

    private DayRecyclerViewAdapter dayRecyclerViewAdapter;
    private List<DayPayOrIncomeList> dayPayOrIncomeDate = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private Map<String, List<PayEventListBean.AllPayListDTO>> map;

    public MonthRecyclerViewAdapter(Context context, List<MonthPayOrIncomeList> monthData, Map<String, List<PayEventListBean.AllPayListDTO>> map) {
        this.context = context;
        this.monthData = monthData;
        this.map = map;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public void setMonthData(List<MonthPayOrIncomeList> monthData) {
        this.monthData = monthData;
    }


    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MonthRecyclerViewAdapter.LinearViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_item_month_payorincome, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String Title_Date = monthData.get(position).getDate();
        holder.DayAndMoth.setText(monthData.get(position).getDate());

        List<DayPayOrIncomeList> newDayPayOrIncomeDate = new ArrayList<>();

        for (Map.Entry<String, List<PayEventListBean.AllPayListDTO>> entry : map.entrySet()) {
            if (entry.getKey().equals(Title_Date)) {
                for (PayEventListBean.AllPayListDTO date : entry.getValue()) {
                    String[] getLocations = date.getLocation().split(" ");

                    String getLocation = getLocations[getLocations.length - 1];
                    int objectId = date.getId();

                    double get_cost = date.getCost();

                    String set_cost;
                    if (get_cost > 0) {
                        set_cost = "+" + get_cost;
                    } else if (get_cost < 0) {
                        set_cost = "" + get_cost;
                    } else {
                        set_cost = "" + 0;
                    }
                    String getTime = date.getTime();
                    String[] getTimes = getTime.split(":");
                    String int_time = getTimes[0] + getTimes[1];
                    DayPayOrIncomeList dayPayOrIncomeList = new DayPayOrIncomeList();
                    dayPayOrIncomeList.setDate(date.getDate());
                    dayPayOrIncomeList.setId(objectId);
                    dayPayOrIncomeList.setInt_time(Integer.valueOf(int_time));
                    dayPayOrIncomeList.setCategory(date.getCategory());
                    dayPayOrIncomeList.setPayTime(date.getTime());
                    dayPayOrIncomeList.setLocation(getLocation);
                    dayPayOrIncomeList.setCost(set_cost);
                    dayPayOrIncomeList.setRemark(date.getRemark());
                    newDayPayOrIncomeDate.add(dayPayOrIncomeList);
                }
            }
        }
        double cost = monthData.get(position).getAllPay();
        if (cost == 0) {
            holder.pay_money.setText("0");
        } else {
            holder.pay_money.setText(String.valueOf(cost));
        }
        double income_money = monthData.get(position).getAllIncome();
        holder.income_money.setText(String.valueOf(income_money));
        double TotalAmount = ChangeDouble.subDouble(income_money, cost);
        if (TotalAmount < 0) {
            holder.amount.setTextColor(ContextCompat.getColor(getContext(), R.color.envelopes));
        } else if (TotalAmount > 0) {
            holder.amount.setTextColor(ContextCompat.getColor(getContext(), R.color.income_color));
        }
        holder.amount.setText(String.valueOf(TotalAmount));

        newDayPayOrIncomeDate.sort(new Comparator<DayPayOrIncomeList>() {
            @Override
            public int compare(DayPayOrIncomeList dayPayOrIncomeList, DayPayOrIncomeList t1) {
                return t1.getInt_time() - dayPayOrIncomeList.getInt_time();
            }
        });

        //dayRecyclerViewAdapter = new DayRecyclerViewAdapter(getContext(), dayPayOrIncomeDate);
        dayPayOrIncomeDate = newDayPayOrIncomeDate;
        dayRecyclerViewAdapter.setData(dayPayOrIncomeDate);
        //holder.month_recyclerView.setAdapter(dayRecyclerViewAdapter);
        dayRecyclerViewAdapter.notifyDataSetChanged();


    }

    public SetOnRecyclerItemCostChangeListener setOnRecyclerItemCostChangeListener;

    public void setSetOnRecyclerItemCostChangeListener(SetOnRecyclerItemCostChangeListener setOnRecyclerItemCostChangeListener) {
        this.setOnRecyclerItemCostChangeListener = setOnRecyclerItemCostChangeListener;
    }

    public interface SetOnRecyclerItemCostChangeListener {
        void OnRecyclerItemCostChangeListener(double cost, int position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return monthData == null ? 0 : monthData.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {

        private TextView DayAndMoth, income_money, pay_money, amount;
        private RecyclerView month_recyclerView;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            DayAndMoth = itemView.findViewById(R.id.DayAndMoth);
            income_money = itemView.findViewById(R.id.income_money);
            pay_money = itemView.findViewById(R.id.pay_money);
            month_recyclerView = itemView.findViewById(R.id.month_recyclerView);
            amount = itemView.findViewById(R.id.amount);
            linearLayoutManager = new LinearLayoutManager(getContext());
            month_recyclerView.setLayoutManager(linearLayoutManager);


            dayRecyclerViewAdapter = new DayRecyclerViewAdapter(getContext(), dayPayOrIncomeDate);

            dayRecyclerViewAdapter.setSetOnRecyclerItemLongClickListener((thisAdapter, position1, Data) -> {
                int delete_ObjectId = Data.get(position1).getId();
                double ItemCost = Double.parseDouble(Data.get(position1).getCost());

                DeleteDialog dialog = new DeleteDialog(getContext());
                dialog.setiOconfirmListener(new DeleteDialog.IOconfirmListener() {
                    @Override
                    public void oncofirm(DeleteDialog dialog) {
                        thisAdapter.setOnInnerRecyclerItemCostChangeListener(new DayRecyclerViewAdapter.OnInnerRecyclerItemCostChangeListener() {
                            @Override
                            public void InnerRecyclerItemCostChangeListener(double cost) {
                                double Income = Double.parseDouble(income_money.getText().toString());
                                double Pay = Double.parseDouble(pay_money.getText().toString());
                                if (cost > 0) {
                                    monthData.get(getAdapterPosition()).setAllIncome(ChangeDouble.subDouble(Income, cost));
                                    income_money.setText(String.valueOf(monthData.get(getAdapterPosition()).getAllIncome()));
                                } else if (cost < 0) {
                                    monthData.get(getAdapterPosition()).setAllPay(ChangeDouble.addDouble(Pay, cost));
                                    pay_money.setText(String.valueOf(monthData.get(getAdapterPosition()).getAllPay()));
                                }
                                Income = monthData.get(getAdapterPosition()).getAllIncome();
                                Pay = monthData.get(getAdapterPosition()).getAllPay();
                                double TotalAmount = ChangeDouble.subDouble(Income, Pay);
                                if (TotalAmount < 0) {
                                    amount.setTextColor(ContextCompat.getColor(getContext(), R.color.envelopes));
                                } else if (TotalAmount > 0) {
                                    amount.setTextColor(ContextCompat.getColor(getContext(), R.color.income_color));
                                } else {
                                    amount.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                                }
                                amount.setText(String.valueOf(TotalAmount));
                            }
                        });
                        Retrofit retrofit = RetrofitUntil.getRetrofit();
                        HomeFragmentAPI api = retrofit.create(HomeFragmentAPI.class);
                        Call<Integer> integerCall = api.DeleteDayPayMessage(delete_ObjectId);
                        integerCall.enqueue(new Callback<Integer>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                if (response.code() == HttpURLConnection.HTTP_OK) {
                                    if (setOnRecyclerItemCostChangeListener != null) {
                                        setOnRecyclerItemCostChangeListener.OnRecyclerItemCostChangeListener(ItemCost, getAdapterPosition());
                                    }

                                    map.getOrDefault(Data.get(0).getDate(), null).remove(position1);
                                    Log.i("removeItem",
                                            "key:" + thisAdapter.getData().get(0).getDate() + "\n");
                                    thisAdapter.removeData(position1);

                                    Log.i("removeItem",
                                            "AdapterPosition:" + getAdapterPosition() + "\n" +
                                                    "dataPosition:" + position1 + "\n" +
                                                    "size-1:" + String.valueOf(monthData.size() - 1) + "\n");
                                    Toast.makeText(getContext(), "删除成功...", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getContext(), "删除失败...", Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable t) {
                                Log.i("removeItem",
                                        "AdapterPosition:" + getAdapterPosition() + "\n" +
                                                "dataPosition:" + position1 + "\n");
                                Toast.makeText(getContext(), "删除失败..." + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }).show();
            });

            dayRecyclerViewAdapter.setOnRecyclerItemClickListener(new DayRecyclerViewAdapter.OnRecyclerItemClickListener() {
                @Override
                public void OnRecyclerOnItemClickListener(int id) {
                    ComponentName componentName = new ComponentName(getContext(), DetailsActivity.class);
                    Intent intent = new Intent();
                    intent.setComponent(componentName);
                    Bundle bundle = new Bundle();
                    bundle.putInt("Id", id);
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);

                }
            });

            month_recyclerView.setAdapter(dayRecyclerViewAdapter);
            //dayRecyclerViewAdapter = new DayRecyclerViewAdapter(getContext(), dayPayOrIncomeDate);

        }
    }
}
