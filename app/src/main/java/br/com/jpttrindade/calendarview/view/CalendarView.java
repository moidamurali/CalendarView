package br.com.jpttrindade.calendarview.view;

import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import br.com.jpttrindade.calendarview.R;
import br.com.jpttrindade.calendarview.data.WeekManager;
import br.com.jpttrindade.calendarview.adapters.CalendarAdapter;
import br.com.jpttrindade.calendarview.holders.MonthHolder;

public class CalendarView extends FrameLayout {

    private Context mContext;
    private int mYear;
    private String[] mMonths;

    private RecyclerView rl_calendar;
    private RecyclerView.LayoutManager mLayoutManager;
    private CalendarAdapter mCalendarAdapter;


    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPage = 1;

    public CalendarView(Context context) {
        super(context);
        init(null, 0);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mContext = getContext();

        final TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CalendarView, defStyle, 0);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Service.LAYOUT_INFLATER_SERVICE);

        View content = layoutInflater.inflate(R.layout.calendar_view, null, false);
        addView(content);

        rl_calendar = (RecyclerView) findViewById(R.id.rl_calendar);

        mLayoutManager = new LinearLayoutManager(mContext);


        rl_calendar.setLayoutManager(mLayoutManager);

        setAdapter(a);

        mLayoutManager.scrollToPosition(3);

        rl_calendar.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mCalendarAdapter.getItemCount();
                firstVisibleItem = ((LinearLayoutManager)mLayoutManager).findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    mCalendarAdapter.getNextMonths();

                    loading = true;
                }

                if (!loading && (firstVisibleItem <= 1+visibleThreshold)) {
                    // Start has been reached
                    mCalendarAdapter.getPreviousMonth();
                    loading = true;
                }
            }
        });



        a.recycle();

        invalidate();

    }

    private void setAdapter(TypedArray a) {
        mCalendarAdapter = new CalendarAdapter(mContext);
        final int monthLabelHeight = (int) a.getDimension(R.styleable.CalendarView_monthLabelHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 48, getResources().getDisplayMetrics()));
        final int weekRowHeight = (int) a.getDimension(R.styleable.CalendarView_weekRowHeight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 48, getResources().getDisplayMetrics()));

        mCalendarAdapter.setMonthLabelHeight(monthLabelHeight);

        mCalendarAdapter.setWeekRowHeight(weekRowHeight);

        rl_calendar.setAdapter(mCalendarAdapter);
    }


}
