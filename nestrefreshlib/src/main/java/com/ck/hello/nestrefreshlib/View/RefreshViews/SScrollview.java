/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.ck.hello.nestrefreshlib.View.RefreshViews;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

import com.ck.hello.nestrefreshlib.View.RefreshViews.HeadWrap.DefaultRefreshWrap;
import com.ck.hello.nestrefreshlib.View.RefreshViews.HeadWrap.EmptyRefreshWrap;
import com.ck.hello.nestrefreshlib.View.RefreshViews.HeadWrap.RefreshWrapBase;
import com.ck.hello.nestrefreshlib.View.RefreshViews.HeadWrap.WrapInterface;


/**
 * Created by s0005 on 2017/4/8.
 * <p>
 * 功能包括 下拉刷新  上啦加载 只预估数值实现自定义添加在布局  overscroll itemtouchhelper
 * 出列快速滑动时的处理不是很完美，但能用
 */

public class SScrollview extends LinearLayout implements NestedScrollingParent, WrapInterface {
    public static final String TAG = "SRecyclerView";
    private NestedScrollingParentHelper scrollingParentHelper;
    private LinearLayout headLayout;
    private LinearLayout footLayout;
    private MyScrollView myScrollView;
    private int speedRate = 5;
    static final Interpolator sQuinticInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    ScrollerCompat scrollerCompat = ScrollerCompat.create(getContext(), sQuinticInterpolator);
    private OnRefreshListener listener;


    private int pullRate = 3;

    private boolean actruallyHead = true, actruallyFoot = true;

    private int scrolls;

    private long beginRefreshing;

    private boolean isLoading = false;

    private int maxTime = 200;

    private int maxFastOverScroll = dp2px(80);

    private boolean canheader = true, canfooter = false;
    private ValueAnimator animator;
    private ValueAnimator animator1;
    private boolean canOverLoadingHeader = true, canOverLoadingfooter = true, canLoadingHeader = true, canLoadingFooter = false;
    private boolean canOverscrollheader = false, canOverscrollfooter = false;

    //头布局
    private RefreshWrapBase headerRefreshWrap=new EmptyRefreshWrap(this,true);
    private RefreshWrapBase footerRefreshWrap=new EmptyRefreshWrap(this,false);


    String[] pulldown = {"下拉刷新", "释放刷新", "正在刷新", "刷新完成"};
    String[] pullup = {"上拉加载", "释放加载", "正在加载", "加载完成"};

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        if (animator1 != null) {
            animator1.cancel();
            animator1 = null;
        }
//        headerRefreshWrap.OnDetachFromWindow();
//        footerRefreshWrap.OnDetachFromWindow();
    }

    public SScrollview(Context context) {
        this(context, null);
    }

    public SScrollview(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SScrollview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        scrollingParentHelper = new NestedScrollingParentHelper(this);

        headLayout = new LinearLayout(getContext());
        headLayout.setOrientation(VERTICAL);
        myScrollView = new MyScrollView(getContext());
        ViewCompat.setNestedScrollingEnabled(myScrollView, true);
        footLayout = new LinearLayout(getContext());
        footLayout.setOrientation(VERTICAL);
        addView(headLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(myScrollView, new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(footLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

    }

    public SScrollview setVelocityRaty(int rate) {
        this.speedRate = rate;
        return this;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 4) {
            throw new IllegalArgumentException("只能有一个直接子布局");
        }
        View child = getChildAt(3);
        removeView(child);
        myScrollView.addView(child);
    }

    public SScrollview setFastMaxOverScroll(int max) {
        maxFastOverScroll = max;
        return this;
    }

    public SScrollview setRefreshMode(boolean head, boolean foot, boolean canLoadingHeader, boolean canloadingFooter) {
        this.canheader = head;
        this.canfooter = foot;
        this.canLoadingHeader = canLoadingHeader;
        this.canLoadingFooter = canloadingFooter;
        return this;
    }

    public SScrollview setBackgdColor(int color) {
        myScrollView.setBackgroundColor(color);
        return this;
    }


    public SScrollview setView(int res) {
        myScrollView.removeAllViews();
        View view = LayoutInflater.from(getContext()).inflate(res, myScrollView, false);
        myScrollView.addView(view);
        return this;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    public SScrollview setRefreshing() {
        headLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrolls = headerRefreshWrap.getHeight();
                smoothScroll(0, -headerRefreshWrap.getHeight(), SCROLLTYPE.PULLDOWN);
                isLoading = true;
                if (listener != null) {
                    headerRefreshWrap.onRefresh();
                    listener.Refreshing();
                }
            }
        }, 300);
        return this;
    }

    public SScrollview addHeader(RefreshWrapBase wrapBase) {
        this.headerRefreshWrap = wrapBase;
        return this;
    }

    public SScrollview addFooter(RefreshWrapBase wrapBase) {
        this.footerRefreshWrap = wrapBase;
        return this;
    }

    public SScrollview addDefaultHeaderFooter() {

        headerRefreshWrap = new DefaultRefreshWrap(this, true);

        footerRefreshWrap = new DefaultRefreshWrap(this, false);
        return this;
    }


    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    public SScrollview setRefreshingListener(OnRefreshListener listener) {
        this.listener = listener;
        return this;
    }

    public SScrollview setPullRate(int rate) {
        if (rate >= 1) {
            pullRate = rate;
        }
        return this;
    }

    public SScrollview setScrollChangeListener(NestedScrollView.OnScrollChangeListener listener) {
        myScrollView.setOnScrollChangeListener(listener);
        return this;
    }

    @Override
    public LinearLayout getHeaderLayout() {
        return headLayout;
    }

    @Override
    public LinearLayout getFootLayout() {
        return footLayout;
    }


    public abstract static class OnRefreshListener {
        public void pullDown(int height) {
        }

        public void pullUp(int height) {
        }

        public abstract void Refreshing();

        public void Loading() {
        }

        public void PreLoading(int pre) {
        }

        public void flingdown(int height) {
        }

        public void flingup(int height) {
        }
    }

    /**
     * @param //         快速滑动加载？
     * @param  //超出范围？
     * @return
     */
    public SScrollview setOverScrollEnable(boolean overscrollhead, boolean overscrollfoot, boolean overloadingheader, boolean overloadingfooter) {
        canOverLoadingHeader = overloadingheader;
        canOverLoadingfooter = overloadingfooter;
        canOverscrollheader = overscrollhead;
        canOverscrollfooter = overscrollfoot;
        return this;
    }

    /**
     * 是否真实滚动头部
     *
     * @return
     */
    public SScrollview setActrullyScrollMode(boolean actruallyHead, boolean actrullyFoot) {
        this.actruallyHead = actruallyHead;
        this.actruallyFoot = actrullyFoot;
        return this;
    }


    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        //TODO回拉时的处理
        if (scrolls == 0 || isLoading)
            return;
        if (canheader) {
            //下拉回拉时
            if (dy > 0 && scrolls < 0) {
                scrolls += dy;
                if (scrolls > 0)
                    scrolls = 0;
                if (actruallyHead)
                    scrollTo(0, scrolls / pullRate);
                headerRefreshWrap.onPull(scrolls / pullRate);
                consumed[1] = dy;
                if (listener != null)
                    listener.pullDown(scrolls / pullRate);
                return;
            }

        }


        if (canfooter) {
            if (dy < 0 && scrolls > 0) {
                scrolls += dy;
                if (scrolls < 0)
                    scrolls = 0;
                if (actruallyFoot)
                    scrollTo(0, scrolls / pullRate);
                consumed[1] = dy;

                footerRefreshWrap.onPull(scrolls / pullRate);

                if (listener != null)
                    listener.pullUp(scrolls / pullRate);
            }
        }
    }

    public class DecelerateAccelerateInterpolator implements TimeInterpolator {
        @Override
        public float getInterpolation(float input) {
            float result;
            if (input <= 0.5) {
                result = (float) (Math.sin(Math.PI * input)) / 2;
            } else {
                result = (float) (2 - Math.sin(Math.PI * input)) / 2;
            }
            return result;
        }

    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed == 0 || isLoading)
            return;
        //TODO拉动的处理
        if ((dyUnconsumed < 0 && canheader) || (dyUnconsumed > 0 && canfooter)) {
            scrolls += dyUnconsumed;
            int pull = scrolls / pullRate;
            if (scrolls < 0 && actruallyHead)
                scrollTo(0, pull);
            else if (scrolls > 0 && actruallyFoot)
                scrollTo(0, pull);

            if (listener != null) {
                if (scrolls > 0) {
                    listener.pullUp(pull);
                    footerRefreshWrap.onPull(pull);
                } else {
                    listener.pullDown(pull);
                    headerRefreshWrap.onPull(pull);
                }
            }
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (myScrollView.canPull(1) && myScrollView.canPull(-1))
            return false;
        boolean canOverscroll = false;
        if (scrolls == 0) {
            if ((canOverscrollheader && velocityY < 1000) || (canOverscrollfooter && velocityY > 1000)) {
                canOverscroll = true;
                if (animator1 != null)
                    animator1.cancel();
//                scrollerCompat.fling(0, getScrollY(), 0, (int) velocityY, 0, 0, 0, maxFastOverScroll, 0, myScrollView.computeVerticalScrollRange());
                scrollerCompat.fling(0, myScrollView.getScrollY(), 0, (int) velocityY, 0, 0, -2 * maxFastOverScroll, myScrollView.computeVerticalScrollRange() + 2 * maxFastOverScroll);
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
        return canOverscroll;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public int getNestedScrollAxes() {
        return scrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void computeScroll() {

        if (scrollerCompat.computeScrollOffset()) {
            if ((myScrollView.canPull(-1) && scrollerCompat.getCurrY() < 0)) {
                int i = scrollerCompat.getFinalY() - scrollerCompat.getCurrY();

                if (i < -headerRefreshWrap.getHeight() && canOverLoadingHeader) {
                    smoothScroll(0, -headerRefreshWrap.getHeight(), SCROLLTYPE.PULLDOWN);
                    scrolls = -headerRefreshWrap.getHeight();
                    if (listener != null) {
                        listener.Refreshing();
                        beginRefreshing = System.currentTimeMillis();
                    }
                } else {
                    if (i > 0)
                        return;
                    smoothScrollRepeat(i < -maxFastOverScroll ? -maxFastOverScroll : i, SCROLLTYPE.FLINGDOWN);
                    scrolls = 0;
                }
                scrollerCompat.abortAnimation();
            } else if ((myScrollView.canPull(1) && scrollerCompat.getCurrY() > 0)) {
                int i2 = scrollerCompat.getFinalY() - scrollerCompat.getCurrY();
                if (i2 > headerRefreshWrap.getHeight() && canOverLoadingfooter) {
                    smoothScroll(0, footerRefreshWrap.getHeight(), SCROLLTYPE.PULLUP);
                    if (listener != null)
                        listener.Loading();
                    scrolls = footerRefreshWrap.getHeight();
                } else {
                    if (i2 < 0)
                        return;
                    smoothScrollRepeat(i2 > maxFastOverScroll ? maxFastOverScroll : i2, SCROLLTYPE.FLINGUP);
                    scrolls = 0;
                }
                scrollerCompat.abortAnimation();
            }
        } else {
//            Log.i(TAG, "computeScroll: finish");
        }

    }

    public enum SCROLLTYPE {
        PULLUP, PULLDOWN, FLINGDOWN, FLINGUP
    }

    private void smoothScrollRepeat(final int max, final SCROLLTYPE type) {
        if (animator1 != null) {
            animator1.cancel();
            animator1.setIntValues(0, max, 0);
        } else {
            animator1 = ValueAnimator.ofInt(0, max, 0);
            animator1.setInterpolator(new DecelerateAccelerateInterpolator());
        }
        animator1.setDuration(getAnimatorDuring(0, max));
        animator1.removeAllUpdateListeners();
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();

                if (listener == null)
                    scrollTo(0, value);
                else {
                    if (type == SCROLLTYPE.FLINGDOWN) {
                        if (actruallyHead)
                            scrollTo(0, value);
                        listener.flingdown(value);
                    } else {
                        if (actruallyFoot)
                            scrollTo(0, value);
                        listener.flingup(value);
                    }
                }
            }
        });
        animator1.start();
    }

    public void notifyRefreshComplete() {
        long current = System.currentTimeMillis() - beginRefreshing;
        footerRefreshWrap.onComplete();
        headerRefreshWrap.onComplete();
        footLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                isLoading = false;
                smoothScroll(scrolls / pullRate, 0, scrolls >= 0 ? SCROLLTYPE.PULLUP : SCROLLTYPE.PULLDOWN);
            }
        }, current > 500 ? 100 : 500);
    }

    @Override
    public void onStopNestedScroll(View child) {
        scrollingParentHelper.onStopNestedScroll(child);
        if (isLoading || !scrollerCompat.isFinished()) {
            return;
        }
        if (scrolls / pullRate <= -headerRefreshWrap.getHeight() && canLoadingHeader) {
            if (listener != null && !isLoading) {
                headerRefreshWrap.onRefresh();
                listener.Refreshing();
                beginRefreshing = System.currentTimeMillis();
            }

            isLoading = true;
            smoothScroll(scrolls / pullRate, -headerRefreshWrap.getHeight(), SCROLLTYPE.PULLDOWN);
        } else if (scrolls / pullRate >= footerRefreshWrap.getHeight() && canLoadingFooter) {

            if (listener != null && !isLoading) {
                listener.Loading();
                footerRefreshWrap.onRefresh();
            }
            isLoading = true;
            smoothScroll(scrolls / pullRate, footerRefreshWrap.getHeight(), SCROLLTYPE.PULLUP);
        } else {
            Log.i(TAG, "onStopNestedScroll: 3333");
            smoothScroll(scrolls / pullRate, 0, scrolls >= 0 ? SCROLLTYPE.PULLUP : SCROLLTYPE.PULLDOWN);
        }
    }

    private void smoothScroll(final int from, final int to, final SCROLLTYPE type) {
        if (from == to)
            return;
        Log.i(TAG, "smoothScroll: " + from + "-" + to);
        if (animator != null) {
            animator.cancel();
            animator.setIntValues(from, to);
            animator.setDuration(getAnimatorDuring(from, to));
        } else {
            animator = ValueAnimator.ofInt(from, to);
            animator.setDuration(getAnimatorDuring(from, to));
            animator.setInterpolator(new DecelerateInterpolator());
        }
        animator.removeAllUpdateListeners();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();

                scrolls = pullRate * value;

                if (listener == null)
                    scrollTo(0, value);
                else {
                    if (type == SCROLLTYPE.PULLDOWN) {
                        if (actruallyHead)
                            scrollTo(0, value);
                        listener.pullDown(value);
                    } else {
                        if (actruallyFoot)
                            scrollTo(0, value);
                        listener.pullDown(value);
                    }

                }
            }
        });
        animator.start();
    }

    private long getAnimatorDuring(int from, int to) {
        long l = (long) (maxTime * ((float) Math.abs(from - to) / (float) headerRefreshWrap.getHeight()));
        if (l < maxTime)
            l = maxTime;
        return l > 550 ? 550 : l;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
//        if(animator!=null)
//            animator.cancel();
//        if(animator1!=null)
//            animator1.cancel();
        scrollerCompat.abortAnimation();
        scrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    class MyScrollView extends NestedScrollView implements NestedScrollingChild, NestedScrollingParent {

        public MyScrollView(Context context) {
            this(context, null);
        }

        @Override
        public void setNestedScrollingEnabled(boolean enable) {
            super.setNestedScrollingEnabled(enable);
        }

        public MyScrollView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setFillViewport(true);
            setOverScrollMode(OVER_SCROLL_NEVER);
            setNestedScrollingEnabled(true);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
        }

        /**
         * @param type -1下拉  1上拉
         * @return
         */
        public boolean canPull(int type) {
            if (type == -1)
                return !canScrollVertically(-1);
            else
                return !canScrollVertically(1);
        }
    }
}
