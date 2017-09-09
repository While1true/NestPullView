package com.ck.hello.nestrefreshlib.View.Adpater;///*

import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ck.hello.nestrefreshlib.R;
import com.ck.hello.nestrefreshlib.View.Adpater.Base.Recorder;
import com.ck.hello.nestrefreshlib.View.Adpater.Base.SimpleViewHolder;
import com.ck.hello.nestrefreshlib.View.Adpater.Base.StateClickListener;
import com.ck.hello.nestrefreshlib.View.Adpater.Base.StateInterface;

import java.util.List;

/* Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */


/**
 * Created by S0005 on 2017/4/17.
 * SHOW_EMPTY:为空时  SHOW_LOADING：加载  SHOW_ERROR：网络错误 SHOW_NOMORE：无更多
 */

/**
 *
 * @param <T> data的泛型
 * @param <E> 传给状态布局的泛型
 *           如果StateHandler写死就不用管E
 */
public class SBaseMutilAdapter<T, E> extends RecyclerView.Adapter {
    /**
     * 存储全局布局id
     */
    private static Recorder recorder;
    public static void init(Recorder recorder1) {
        recorder = recorder1;
    }

    public static final int SHOW_EMPTY = -100, SHOW_LOADING = -200, SHOW_ERROR = -300, SHOW_NOMORE = -400;
    protected int showstate = SHOW_LOADING;
    protected List<T> list;

    private E e = null;
    StateInterface StateHandler;

    public static final int TYPE_ITEM = 30000000;
    private int height = 0;


    /**
     * 设置布局layout
     * @param emptyres
     * @param loadingres
     * @param errorres
     * @param nomore
     * @param
     */
    public SBaseMutilAdapter<T, E> setStateLayout(int emptyres, int loadingres, int errorres,int nomore) {
        Recorder.Builder builder = new Recorder.Builder();
        if(emptyres!=0)
            builder.setEmptyRes(emptyres);
        if(loadingres!=0)
            builder.setLoadingRes(loadingres);
        if(errorres!=0)
            builder.setErrorRes(errorres);
        if(nomore!=0)
            builder.setNomoreRes(nomore);
        recorder=builder.build();
        return this;
    }

    /**
     * 设置数据构造
     * @param list
     */
    public SBaseMutilAdapter(List<T> list) {
        if(recorder==null)
            recorder=new Recorder.Builder().build();
        this.list = list;
        StateHandler = new StateHandler();
    }

    /**
     *
     * @param handler 设置状态布局的处理器
     * @return
     */
    public SBaseMutilAdapter<T, E> setStateHandler(StateInterface handler) {
        if (StateHandler.getStateClickListener() != null)
            handler.setStateClickListener(StateHandler.getStateClickListener());
        this.StateHandler = handler;
        return this;
    }

    /**
     * 设置状态布局的监听
     * @param listener
     * @return
     */
    public SBaseMutilAdapter<T, E> setStateListener(StateClickListener listener) {
        StateHandler.setStateClickListener(listener);
        return this;
    }

    public int getShowstate() {
        return showstate;
    }

    public void setBeanList(List<T> t) {
        this.list = t;
        showstate = TYPE_ITEM;
    }

    public List<T> getBeanlist() {
        return list;
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (height == 0)
                        height = recyclerView.getMeasuredHeight();
//                    notifyDataSetChanged();
                }
            });
    }

    public void showState(int showstate, E e) {
        if (this.showstate != showstate)
            StateHandler.switchState();
        this.showstate = showstate;
        this.e = e;
        notifyDataSetChanged();
    }

    public void showState(int showstate, E e, int height) {
        if (this.showstate != showstate)
            StateHandler.switchState();
        this.height = height;
        this.showstate = showstate;
        this.e = e;
        notifyDataSetChanged();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        StateHandler.destory();
        StateHandler = null;
        Holdersid.clear();
    }

    private RecyclerView.ViewHolder creatHolder(int layout, ViewGroup viewGroup) {

        return new RecyclerView.ViewHolder(InflateView(layout, viewGroup)) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SHOW_EMPTY:
                return creatHolder(recorder.getEmptyres(), parent);
            case SHOW_LOADING:
                return creatHolder(recorder.getLoadingres(), parent);
            case SHOW_ERROR:
                return creatHolder(recorder.getErrorres(), parent);
            case SHOW_NOMORE:
                return creatHolder(recorder.getNomore(), parent);
        }
        return onCreate(parent, viewType);
    }

    public View InflateView(int layout, ViewGroup parent) {

        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (showstate) {
            case SHOW_EMPTY:
                if (height != 0)
                    holder.itemView.getLayoutParams().height = height;
                StateHandler.BindEmptyHolder(holder, e);
                return;
            case SHOW_LOADING:
                if (height != 0)
                    holder.itemView.getLayoutParams().height = height;
                StateHandler.BindLoadingHolder(holder, e);

                return;
            case SHOW_ERROR:
                if (height != 0)
                    holder.itemView.getLayoutParams().height = height;

                StateHandler.BindErrorHolder(holder, e);
                return;
            case SHOW_NOMORE:
                if (position == getItemCount() - 1) {
                    StateHandler.BindNomoreHolder(holder, e);
                    return;
                }
                break;
        }
        for (int i = 0; i < Holdersid.size(); i++) {
            if (Holdersid.get(i).istype(position))
                Holdersid.get(i).onBind((SimpleViewHolder) holder, list.get(position), position);
        }

    }

    @Override
    public int getItemCount() {
        if (showstate == SHOW_EMPTY || showstate == SHOW_LOADING || showstate == SHOW_ERROR) {
            return 1;
        }
        if (showstate == SHOW_NOMORE)
            return getCount() + 1;
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        switch (showstate) {
            case SHOW_EMPTY:
                return SHOW_EMPTY;
            case SHOW_LOADING:
                return SHOW_LOADING;
            case SHOW_ERROR:
                return SHOW_ERROR;
            case SHOW_NOMORE:
                if (position < getItemCount() - 1)
                    return getType(position);
                else
                    return SHOW_NOMORE;

        }
        return getType(position);
    }


    private int getCount() {
        return list == null ? 0 : list.size();
    }

    protected int getType(int position) {
        if (Holdersid.size() == 0)
            return TYPE_ITEM;
        else return getMutilType(position);
    }

    protected RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return SimpleViewHolder.createViewHolder(parent.getContext(), InflateView(Holdersid.get(viewType).getLayout(), parent));
    }

    ArrayMap<Integer, ITEMHOLDER> Holdersid = new ArrayMap<>(2);

    public SBaseMutilAdapter<T, E> addType(int layoutid, ITEMHOLDER<T> itemholder) {
        Holdersid.put(Holdersid.size(), itemholder.setLayout(layoutid));
        return this;
    }

    protected int getMutilType(int position) {
        for (int i = Holdersid.keySet().size() - 1; i >= 0; i--) {
            if (Holdersid.get(i).istype(position))
                return i;
        }
        return TYPE_ITEM;
    }

    public static abstract class ITEMHOLDER<T> {
        private int layout;

        public abstract void onBind(SimpleViewHolder holder, T item, int position);

        public abstract boolean istype(int position);

        public int getLayout() {
            return layout;
        }

        public ITEMHOLDER setLayout(int layout) {
            this.layout = layout;
            return this;
        }
    }
}
