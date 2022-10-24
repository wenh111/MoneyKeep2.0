package com.org.moneykeep.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.org.moneykeep.R;
import com.org.moneykeep.RecyclerViewAdapter.RecyclerViewList.DayPayOrIncomeList;

import java.util.HashMap;
import java.util.List;

public class DayRecyclerViewAdapter extends RecyclerSwipeAdapter<DayRecyclerViewAdapter.LinearViewHolder> {

    private Context context;
    public List<DayPayOrIncomeList> Data;
    //public HashMap<String, Integer> IntegerColor;

    public DayRecyclerViewAdapter(Context context, List<DayPayOrIncomeList> data) {
        this.context = context;
        this.Data = data;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<DayPayOrIncomeList> getData() {
        return Data;
    }

    public void setData(List<DayPayOrIncomeList> data) {
        Data = data;
    }

    public void removeData(int position) {
        double cost = Double.parseDouble(Data.get(position).getCost());

        getData().remove(position);
        notifyItemRemoved(position);
        if (onInnerRecyclerItemCostChangeListener != null) {
            onInnerRecyclerItemCostChangeListener.InnerRecyclerItemCostChangeListener(cost);
        }


    }

    public void updateData(int position, DayPayOrIncomeList dayPayOrIncomeList) {
        getData().set(position, dayPayOrIncomeList);
        notifyItemChanged(position);

    }

    @NonNull
    @Override
    public LinearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new LinearViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_item_day_payorincome, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull LinearViewHolder holder, int position) {
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);//形状
        gradientDrawable.setCornerRadius(6f);//设置圆角Radius
        gradientDrawable.setColor(holder.IntegerColor.getOrDefault(Data.get(position).getCategory(),
                ContextCompat.getColor(getContext(), R.color.travel)));//颜色

        GradientDrawable gradientDrawable_parent = new GradientDrawable();
        gradientDrawable_parent.setShape(GradientDrawable.RECTANGLE);//形状
        gradientDrawable_parent.setCornerRadius(6f);//设置圆角Radius
        gradientDrawable_parent.setColor(ContextCompat.getColor(getContext(), R.color.newbackgound));


        if (Double.parseDouble(Data.get(position).getCost()) > 0) {
            if (!Data.get(position).getCost().contains("+")) {
                Data.get(position).setCost("+" + Data.get(position).getCost());
            }
            holder.tx_money.setTextColor(ContextCompat.getColor(getContext(), R.color.income_color));
        } else {
            holder.tx_money.setTextColor(ContextCompat.getColor(getContext(), R.color.envelopes));
        }

        holder.parent_ly.setBackground(gradientDrawable_parent);
        holder.left_color.setBackground(gradientDrawable);
        holder.tx_type.setText(Data.get(position).getCategory());
        holder.tx_location.setText(Data.get(position).getLocation());
        holder.tx_time.setText(Data.get(position).getTime());
        holder.tx_money.setText(Data.get(position).getCost());
        holder.tx_remark.setText(Data.get(position).getRemark());
        mItemManger.bindView(holder.itemView, position);//实现只展现一条列表项的侧滑区域
    }

    @Override
    public int getItemCount() {
        return Data == null ? 0 : Data.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout;
    }


    class LinearViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout left_color, parent_ly;
        public TextView tx_type, tx_time, tx_location, tx_money, tx_remark;
        public ImageButton image_delete, image_detail;
        public SwipeLayout swipeLayout;
        public HashMap<String, Integer> IntegerColor;

        @SuppressLint("ClickableViewAccessibility")
        public LinearViewHolder(View itemView) {
            super(itemView);
            IntegerColor = new HashMap<>();
            {
                IntegerColor.put("餐饮", ContextCompat.getColor(getContext(), R.color.restaurant));/*getContext().getResources().getColor()*/
                IntegerColor.put("交通", ContextCompat.getColor(getContext(), R.color.traffic));
                IntegerColor.put("服饰", ContextCompat.getColor(getContext(), R.color.clothes));
                IntegerColor.put("购物", ContextCompat.getColor(getContext(), R.color.shopping));
                IntegerColor.put("服务", ContextCompat.getColor(getContext(), R.color.service));
                IntegerColor.put("教育", ContextCompat.getColor(getContext(), R.color.teach));
                IntegerColor.put("娱乐", ContextCompat.getColor(getContext(), R.color.entertainment));
                IntegerColor.put("运动", ContextCompat.getColor(getContext(), R.color.motion));
                IntegerColor.put("生活缴费", ContextCompat.getColor(getContext(), R.color.living_payment));
                IntegerColor.put("旅行", ContextCompat.getColor(getContext(), R.color.travel));
                IntegerColor.put("宠物", ContextCompat.getColor(getContext(), R.color.pets));
                IntegerColor.put("医疗", ContextCompat.getColor(getContext(), R.color.medical));
                IntegerColor.put("保险", ContextCompat.getColor(getContext(), R.color.insurance));
                IntegerColor.put("公益", ContextCompat.getColor(getContext(), R.color.welfare));
                IntegerColor.put("发红包", ContextCompat.getColor(getContext(), R.color.envelopes));
                IntegerColor.put("转账", ContextCompat.getColor(getContext(), R.color.collection_and_transfer));
                IntegerColor.put("亲属卡", ContextCompat.getColor(getContext(), R.color.kinship_card));
                IntegerColor.put("做人情", ContextCompat.getColor(getContext(), R.color.human));
                IntegerColor.put("其它支出", ContextCompat.getColor(getContext(), R.color.other));
                IntegerColor.put("生意", ContextCompat.getColor(getContext(), R.color.business));
                IntegerColor.put("工资", ContextCompat.getColor(getContext(), R.color.wages));
                IntegerColor.put("奖金", ContextCompat.getColor(getContext(), R.color.bonus));
                IntegerColor.put("收红包", ContextCompat.getColor(getContext(), R.color.envelopes));
                IntegerColor.put("收转账", ContextCompat.getColor(getContext(), R.color.collection_and_transfer));
                IntegerColor.put("其它收入", ContextCompat.getColor(getContext(), R.color.other_income));
                IntegerColor.put("建设银行", ContextCompat.getColor(getContext(), R.color.Construction_Bank));
                IntegerColor.put("农业银行", ContextCompat.getColor(getContext(), R.color.Agricultural_Bank));
                IntegerColor.put("微信", ContextCompat.getColor(getContext(), R.color.wechat));
                IntegerColor.put("支付宝", ContextCompat.getColor(getContext(), R.color.alipay));

            }
            parent_ly = itemView.findViewById(R.id.parent_ly);
            left_color = itemView.findViewById(R.id.left_color);
            tx_type = itemView.findViewById(R.id.tx_type);
            tx_time = itemView.findViewById(R.id.tx_time);
            tx_location = itemView.findViewById(R.id.tx_location);
            tx_money = itemView.findViewById(R.id.tx_money);
            tx_remark = itemView.findViewById(R.id.tx_remark);
            image_delete = itemView.findViewById(R.id.image_delete);
            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            image_detail = itemView.findViewById(R.id.image_detail);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                float startY, curY, curX, startX;
                long timeDown, timeUp;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        timeDown = System.currentTimeMillis();
                        startX = motionEvent.getX();
                        startY = motionEvent.getY();
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        curX = motionEvent.getX();
                        curY = motionEvent.getY();
                        timeUp = System.currentTimeMillis();
                        long durationMs = timeUp - timeDown;
                        float dx = curX - startX;
                        if (Math.abs(dx) < 6) {
                            Log.i("onInterceptTouchEvent", "durationMs == " + durationMs);
                            if (durationMs < 500) {
                                if ((!isOpen(getAdapterPosition()))) {
                                    if (onRecyclerItemClickListener != null) {
                                        closeAllItems();
                                        onRecyclerItemClickListener.OnRecyclerOnItemClickListener(getData().get(getAdapterPosition()).getId(), getData().get(getAdapterPosition()), getAdapterPosition());
                                    }
                                }
                            }
                        }

                    }
                    return false;
                }

            });

            itemView.setOnLongClickListener(view -> {
                if (view == itemView) {
                    if (setOnRecyclerItemLongClickListener != null) {
                        setOnRecyclerItemLongClickListener.OnRecyclerOnItemClickListener(DayRecyclerViewAdapter.this, getAdapterPosition(), getData());
                    }
                }
                return true;
            });
            image_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (setOnRecyclerItemLongClickListener != null) {
                        setOnRecyclerItemLongClickListener.OnRecyclerOnItemClickListener(DayRecyclerViewAdapter.this, getAdapterPosition(), getData());
                    }
                }
            });
            image_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecyclerItemClickListener != null) {
                        onRecyclerItemClickListener.OnRecyclerOnItemClickListener(getData().get(getAdapterPosition()).getId(), getData().get(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });
            /*itemView.setOnClickListener(view -> {
                if (view == itemView) {
                    if (onRecyclerItemClickListener != null) {
                        onRecyclerItemClickListener.OnRecyclerOnItemClickListener(getData().get(getAdapterPosition()).getId());
                    }
                }
            });*/


        }
    }

    private SetOnRecyclerItemLongClickListener setOnRecyclerItemLongClickListener;

    public void setSetOnRecyclerItemLongClickListener(SetOnRecyclerItemLongClickListener setOnRecyclerItemLongClickListener) {
        this.setOnRecyclerItemLongClickListener = setOnRecyclerItemLongClickListener;
    }

    public interface SetOnRecyclerItemLongClickListener {
        void OnRecyclerOnItemClickListener(DayRecyclerViewAdapter thisAdapter, int position, List<DayPayOrIncomeList> Data);
    }

    private OnRecyclerItemClickListener onRecyclerItemClickListener;

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public interface OnRecyclerItemClickListener {
        void OnRecyclerOnItemClickListener(int objectId, DayPayOrIncomeList dayPayOrIncomeList, int adapterPosition);
    }


    public OnInnerRecyclerItemCostChangeListener onInnerRecyclerItemCostChangeListener;

    public void setOnInnerRecyclerItemCostChangeListener(OnInnerRecyclerItemCostChangeListener onInnerRecyclerItemCostChangeListener) {
        this.onInnerRecyclerItemCostChangeListener = onInnerRecyclerItemCostChangeListener;
    }

    public interface OnInnerRecyclerItemCostChangeListener {
        void InnerRecyclerItemCostChangeListener(double cost);
    }
}
