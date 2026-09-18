package com.vts.vtsapproot.API.Interfaces;

import java.util.Date;

public interface DatePickerInterface extends BaseClickInterface<Date> {
    @Override
    default void ItemClicked(Date pItem) {}

    void DatePicked(Date date);
}
