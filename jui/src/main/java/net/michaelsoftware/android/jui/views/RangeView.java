package net.michaelsoftware.android.jui.views;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;

/**
 * Created by Michael on 06.06.2016.
 */
public class RangeView extends SeekBar implements SeekBar.OnSeekBarChangeListener {
    private int min = 0;
    private CustomSwipeRefreshLayout swipeRefreshLayout;

    public RangeView(Context context) {
        super(context);

        this.setOnSeekBarChangeListener(this);

        this.setOnTouchListener(new ListView.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow Drawer to intercept touch events.
                        //v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow Drawer to intercept touch events.
                        //v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle seekbar touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public void setProgress(int progress) {
        super.setProgress(progress - this.min);
    }

    @Override
    public void setMax(int max) {
        super.setMax(max - this.min);
    }

    @Override
    public int getProgress() {
        int progress = super.getProgress();
        return progress + this.min;
    }

    public void setSwipeRefreshLayout(CustomSwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(swipeRefreshLayout != null)
            swipeRefreshLayout.disableInterceptTouchEvent(true);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(swipeRefreshLayout != null)
            swipeRefreshLayout.disableInterceptTouchEvent(false);
    }
}
