package com.ck.hello.nestrefreshlib.View.Adpater;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ck.hello.nestrefreshlib.R;
import com.ck.hello.nestrefreshlib.View.Adpater.Base.StateClickListener;
import com.ck.hello.nestrefreshlib.View.Adpater.Base.StateInterface;
import com.ck.hello.nestrefreshlib.View.SLoading;

/**
 * Created by ck on 2017/9/9.
 */

public class StateHandler implements StateInterface<String> {
    private StateClickListener listener;
    private SLoading sLoading;

    public StateHandler setStateClickListener(StateClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public StateClickListener getStateClickListener() {
        return listener;
    }

    @Override
    public void BindEmptyHolder(RecyclerView.ViewHolder holder, String s) {
        TextView tv = (TextView) holder.itemView.findViewById(R.id.tv);
        if (tv != null)
            tv.setText(s);
        if (tv != null && listener != null)
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.showEmpty(v.getContext());
                }
            });
    }

    @Override
    public void BindErrorHolder(RecyclerView.ViewHolder holder, String s) {
        View reloadbt = holder.itemView.findViewById(R.id.reload);
        if (reloadbt != null && listener != null)
            reloadbt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.netError(v.getContext());
                }
            });
    }

    @Override
    public void BindLoadingHolder(RecyclerView.ViewHolder holder, String s) {

        if (sLoading == null) {
            sLoading = (SLoading) holder.itemView.findViewById(R.id.sloading);
        }
        if (sLoading != null)
            sLoading.startAnimator();
    }

    @Override
    public void BindNomoreHolder(RecyclerView.ViewHolder holder, String s) {
        ((TextView) holder.itemView.findViewById(R.id.tv_nomore)).setText(s);
    }


    public void destory() {
        listener=null;
    }
}
