package com.haoye.customviewgroup;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by Haoye on 2016/5/28.
 * Copyright © 2016 Haoye All Rights Reserved
 */

public class CustomViewGroup extends ViewGroup {
    private int mScrWidth;
    private int mScrHeight;
    private int mOldY;
    private int mStartY;
    private int mEndY;
    private Scroller mScroller;

    public CustomViewGroup(Context context) {
        this(context, null);
    }

    public CustomViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mScrWidth = metrics.widthPixels;
        mScrHeight = metrics.heightPixels;
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int childCnt = getChildCount();
        for (int i = 0; i < childCnt; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCnt = getChildCount();
        MarginLayoutParams mlp = (MarginLayoutParams) getLayoutParams();
        mlp.width = mScrWidth;
        mlp.height = mScrHeight;
        setLayoutParams(mlp);
        int displayNum = 0;
        for (int i = 0; i < childCnt; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                child.layout(l, displayNum*mScrHeight, r, (displayNum+1)*mScrHeight);
                displayNum++;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int newY = (int) event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mOldY = newY;
            mStartY = getScrollY();
            break;
        case MotionEvent.ACTION_MOVE:
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
            int dy = mOldY - newY;
            scrollBy(0, dy);
            mOldY = newY;
            break;
        case MotionEvent.ACTION_UP:
            mEndY = getScrollY();
            int dScrollY = mEndY - mStartY;
            if (dScrollY > 0) {
                if (dScrollY < mScrHeight/3) {
                    mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                }
                else {
                    mScroller.startScroll(0, getScrollY(), 0, mScrHeight - dScrollY);
                }
            }
            else {
                if (-dScrollY < mScrHeight/3) {
                    mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                }
                else {
                    mScroller.startScroll(0, getScrollY(), 0, -mScrHeight - dScrollY);
                }
            }
            break;
        }
        postInvalidate();
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }
}
