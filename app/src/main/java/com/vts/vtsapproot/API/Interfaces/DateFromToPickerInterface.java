package com.vts.vtsapproot.API.Interfaces;

import java.util.Date;

public interface DateFromToPickerInterface extends BaseClickInterface<Date> {
    @Override
    default void ItemClicked(Date pItem) { }

    void DateFromToPicked(Date dateFrom, Date dateTo);
}
