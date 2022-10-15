package com.org.moneykeep.RecyclerViewAdapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.org.moneykeep.R;
import com.org.moneykeep.RecyclerViewAdapter.RecyclerViewList.SaveMoneyAdapterList;
import com.org.moneykeep.Until.ChangeDouble;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveMoneyAdapter extends RecyclerView.Adapter<SaveMoneyAdapter.LinearViewHolder> {

    private Context context;
    public List<SaveMoneyAdapterList> Data;

    public SaveMoneyAdapter(Context context, List<SaveMoneyAdapterList> data) {
        this.context = context;
        this.Data = data;
    }

    public List<SaveMoneyAdapterList> getData() {
        return Data;
    }

    public void setData(List<SaveMoneyAdapterList> data) {
        this.Data = data;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Map<String, Integer> map;

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        map = new HashMap<>();
        {
            map.put("12月存钱", R.drawable.year_project);
            map.put("30天存钱", R.drawable.month_project);
            map.put("52周存钱", R.drawable.fiftytwo_week);
            map.put("4周存钱", R.drawable.four_week);
            map.put("365天存钱", R.drawable.is_365day);
            map.put("自定义", R.drawable.custom);
        }
        return new LinearViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_item_savemoney, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {

        double targetMoney = Data.get(position).getTargetMoney();
        double savedMoney = Data.get(position).getSavedMoney();
        double overMoney = ChangeDouble.subDouble(targetMoney, savedMoney);
        double saveSpeed = ChangeDouble.divDouble(savedMoney, targetMoney) * 100;
        String type = Data.get(position).getType();
        String text = Data.get(position).getText();
        if(text.equals("已完成")){
            overMoney = 0;
            saveSpeed = 100;
        }
        holder.title.setText(Data.get(position).getTitle());
        holder.target_money.setText(String.valueOf(targetMoney));
        holder.saved_money.setText(String.valueOf(savedMoney));
        holder.over_money.setText(String.valueOf(overMoney));
        holder.imageView.setImageResource(map.getOrDefault(type, R.drawable.all_project));
        holder.speed.setText(saveSpeed + "%");
        holder.progressbar.setProgress((int) saveSpeed);
        holder.status.setText(text);
        if(text.equals("未打卡")){
            holder.status.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.save_item_textview_background_1));
        }else if(text.equals("已打卡")){
            holder.status.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.save_item_textview_background_2));
        }else if(text.equals("已完成")){
            holder.status.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.save_item_textview_background_4));
        }else{
            holder.status.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.save_item_textview_background_3));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return Data == null ? 0 : Data.size();
    }

    class LinearViewHolder extends RecyclerView.ViewHolder {
        private TextView status, title, target_money, speed, saved_money, over_money;
        private ImageView imageView;
        private ProgressBar progressbar;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            over_money = itemView.findViewById(R.id.over_money);
            saved_money = itemView.findViewById(R.id.saved_money);
            speed = itemView.findViewById(R.id.speed);
            target_money = itemView.findViewById(R.id.target_money);
            title = itemView.findViewById(R.id.title);
            status = itemView.findViewById(R.id.status);
            imageView = itemView.findViewById(R.id.imageView);
            progressbar = itemView.findViewById(R.id.progressbar);
            //itemView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.save_item_background));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(view == itemView){
                        if(onRecyclerItemClickListener != null){
                            String id = Data.get(getAdapterPosition()).getObjectId();
                            String date = Data.get(getAdapterPosition()).getDate();
                            String deadlineDate = Data.get(getAdapterPosition()).getDeadlineDate();
                            String nowSpeed = String.valueOf(progressbar.getProgress());
                            double targetMoney = Data.get(getAdapterPosition()).getTargetMoney();
                            double savedMoney = Data.get(getAdapterPosition()).getSavedMoney();
                            String nowState = status.getText().toString();
                            String Title = title.getText().toString();
                            String Type = Data.get(getAdapterPosition()).getType();
                            int everyday_save =  Data.get(getAdapterPosition()).getEveryday_save();
                            int amount = Data.get(getAdapterPosition()).getAmount();
                            /*onRecyclerItemClickListener.OnRecyclerOnItemClickListener(id,date,deadlineDate,targetMoney,
                                    savedMoney,nowSpeed,nowState,Title,Type,everyday_save,amount);*/
                            onRecyclerItemClickListener.OnRecyclerOnItemClickListenerData(Data.get(getAdapterPosition()),nowSpeed,nowState);
                        }
                    }
                }
            });

        }
    }
    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public interface OnRecyclerItemClickListener {
        /*void OnRecyclerOnItemClickListener(String objectId,String date,String deadlineDate,double targetMoney,double savedMoney,
                                           String speed,String nowState, String Title,String Type,int everyday_save,int amount);*/
        void OnRecyclerOnItemClickListenerData(SaveMoneyAdapterList data, String speed, String nowState);
    }
}
