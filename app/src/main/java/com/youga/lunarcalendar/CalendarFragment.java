package com.youga.lunarcalendar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by YougaKing on 2016/10/11.
 */

public class CalendarFragment extends Fragment {


    private static final String TAG = "CalendarFragment";
    InnerAdapter mAdapter;
    Callback mCallback;
    List<String> mWeeks = new ArrayList<>();
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    private int mPosition;


    public static CalendarFragment newInstance(int position) {
        CalendarFragment calendarFragment = new CalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.MONTH, position);
        bundle.putSerializable("Calendar", calendar);
        calendarFragment.setArguments(bundle);
        return calendarFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeeks.add("占位");
        mWeeks.add("星期日");
        mWeeks.add("星期一");
        mWeeks.add("星期二");
        mWeeks.add("星期三");
        mWeeks.add("星期四");
        mWeeks.add("星期五");
        mWeeks.add("星期六");

        mPosition = getArguments().getInt("position");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Calendar calendar = (Calendar) getArguments().getSerializable("Calendar");
        init(calendar);
    }

    private void init(Calendar calendar) {
        List<Solar> solars = new ArrayList<>();
        mTvTitle.setText(mPosition + "-->" + new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(calendar.getTime()));
        calendar.set(Calendar.DATE, 1);
        int firstWeek = getWeek(calendar);//计算当月第一天星期几
        int lastWeek = getLastDayOfMonthToWeek(calendar.getTime());//当月最后一天星期几
        int days = getLastDayOfMonth(calendar.getTime());//当月总天数
        int diffWeek = (firstWeek - Calendar.SUNDAY);
        calendar.add(Calendar.DATE, -diffWeek);
        int count = diffWeek + days + (Calendar.SATURDAY - lastWeek);

        for (int i = 0; i < count; i++) {
            if (i != 0)
                calendar.add(Calendar.DATE, 1);
            solars.add(new Solar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1
                    , calendar.get(Calendar.DATE), calendar.getTime()));
        }
        mAdapter = new InnerAdapter(solars, diffWeek, (Calendar.SATURDAY - lastWeek));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), Calendar.DAY_OF_WEEK));
    }

    // 获取当月最后一天是周几
    public int getLastDayOfMonthToWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);     // 设置当前月的1号
        calendar.add(Calendar.MONTH, 1);   // 加一个月，变为下月的1号
        calendar.add(Calendar.DATE, -1);    // 减去一天，变为当前月的最后一天
        return getWeek(calendar);
    }

    // 获取当月最后一天
    public int getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);     // 设置当前月的1号
        calendar.add(Calendar.MONTH, 1);   // 加一个月，变为下月的1号
        calendar.add(Calendar.DATE, -1);    // 减去一天，变为当前月的最后一天
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    // 根据一个日期，返回是星期几的字符串
    public int getWeek(Calendar calendar) {
        String week = new SimpleDateFormat("EEEE", Locale.CHINA).format(calendar.getTime());
        return mWeeks.indexOf(week);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.ViewHolder> {

        private final int mWidth, mHeight;
        private int mStartDiff, mEndDiff;
        List<Solar> mSolarList;

        public InnerAdapter(List<Solar> solars, int startDiff, int endDiff) {
            mWidth = getResources().getDisplayMetrics().widthPixels / 7;
            mHeight = mWidth;
            mSolarList = solars;
            mStartDiff = startDiff;
            mEndDiff = endDiff;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.item_calendar, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindPosition(position);
        }

        @Override
        public int getItemCount() {
            return mSolarList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.btn_lucky)
            Button mBtnLucky;
            @Bind(R.id.tv_solar)
            TextView mTvSolar;
            @Bind(R.id.tv_lunar)
            TextView mTvLunar;
            @Bind(R.id.layout)
            FrameLayout mLayout;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindPosition(final int position) {
                ViewGroup.LayoutParams params = mLayout.getLayoutParams();
                if (params == null) {
                    params = new ViewGroup.LayoutParams(mWidth, mHeight);
                } else {
                    params.width = mWidth;
                    params.height = mHeight;
                }
                mLayout.setLayoutParams(params);

                Solar solar = mSolarList.get(position);
                mBtnLucky.setVisibility(View.GONE);
                String day = String.valueOf(solar.day);
                mTvSolar.setText(day);

                LunarSolarConverter.Lunar lunar = LunarSolarConverter.converterDate(solar.date.getTime());
                mTvLunar.setText(lunar.day == 1 ? lunar.getMonth() + "月" : lunar.getDay());

                if (position < mStartDiff) {
                    mBtnLucky.setVisibility(View.GONE);
                    mTvSolar.setTextColor(Color.parseColor("#DFDFDF"));
                    mTvLunar.setTextColor(Color.parseColor("#DFDFDF"));
                    mLayout.setClickable(false);
                    mLayout.setEnabled(false);
                } else if (position >= (getItemCount() - mEndDiff)) {
                    mTvSolar.setTextColor(Color.parseColor("#DFDFDF"));
                    mTvLunar.setTextColor(Color.parseColor("#DFDFDF"));
                    mBtnLucky.setVisibility(View.GONE);
                    mLayout.setClickable(false);
                    mLayout.setEnabled(false);
                } else {
                    Calendar calendar = Calendar.getInstance();
                    if ((solar.date.getTime() - calendar.getTimeInMillis()) > -24 * 60 * 60 * 1000) {
                        mLayout.setClickable(true);
                        mLayout.setEnabled(true);
                        mTvSolar.setTextColor(Color.parseColor("#000000"));
                        mTvLunar.setTextColor(Color.parseColor("#989898"));

                        if (solar.year == calendar.get(Calendar.YEAR) &&
                                solar.month == calendar.get(Calendar.MONTH) + 1 &&
                                solar.day == calendar.get(Calendar.DATE)) {
                            mTvSolar.setText("今天");
                        }

                    } else {
                        mTvSolar.setTextColor(Color.parseColor("#DFDFDF"));
                        mTvLunar.setTextColor(Color.parseColor("#DFDFDF"));
                        mBtnLucky.setVisibility(View.GONE);
                        mLayout.setClickable(false);
                        mLayout.setEnabled(false);
                    }

                }

                mLayout.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    }

    interface Callback {
        void computeHeight(int position, int height);

        void itemClick(int position, Objects lunar, Calendar calendar);
    }

    public static class Solar {
        public int year, month, day;
        public Date date;

        public Solar(int year, int month, int day, Date date) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.date = date;
        }
    }
}
