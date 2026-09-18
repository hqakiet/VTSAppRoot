package com.vts.vtsapproot.API.Request;

import com.vts.vtsapproot.Tools.gvSystem;

import java.util.Date;

public class ReqMod_DateFromTo {
    String DateFrom;
    String DateTo;

    public ReqMod_DateFromTo(String dateFrom, String dateTo) {
        DateFrom = dateFrom;
        DateTo = dateTo;
    }

    public ReqMod_DateFromTo(Date dateFrom, Date dateTo) {
        DateFrom = gvSystem.getStrDate_WithFormat(dateFrom, "yyyy/MM/dd");
        DateTo = gvSystem.getStrDate_WithFormat(dateTo, "yyyy/MM/dd");
    }
}
