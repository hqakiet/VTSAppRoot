package com.vts.vtsapproot.API.Interfaces;

import android.view.View;

public interface ItemPickerWithMotionEventInterface<T> extends BaseClickInterface<T> {
    @Override
    default void ItemClicked(T pItem) {}

    void ItemPicked(View viewForTransition, T pItem, android.view.MotionEvent event);
}
