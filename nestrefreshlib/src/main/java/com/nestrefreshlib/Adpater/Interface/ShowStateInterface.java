package com.nestrefreshlib.Adpater.Interface;


import com.nestrefreshlib.Adpater.Base.StateEnum;

/**
 * Created by ck on 2017/9/10.
 */

public interface ShowStateInterface<E> {

    void showState(StateEnum showstate, E e);

    void showEmpty();

    void ShowError();

    void showItem();

    void showLoading();

    void showNomore();
}