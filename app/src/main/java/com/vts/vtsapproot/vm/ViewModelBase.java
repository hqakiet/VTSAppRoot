package com.vts.vtsapproot.vm;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class ViewModelBase<T>
        extends ViewModel {

    public ViewModelBase() {
        _Collator = Collator.getInstance();
    }

    public Collator _Collator;

    public static class SortConfig {
        public String field;
        public boolean isAsc;

        public SortConfig(String field, boolean isAsc) {
            this.field = field;
            this.isAsc = isAsc;
        }
    }

    protected Map<String, SortConfig> getCurrSortConfig() {
        return Collections.emptyMap();
    }

    protected Comparator<T> getComparatorByField(String field) {
        return null;
    }

    protected final MutableLiveData<Date> DateFromTo_TuNgay = new MutableLiveData<>();

    public LiveData<Date> get_DateFromTo_TuNgay() {
        return DateFromTo_TuNgay;
    }

    public void set_DateFromTo_TuNgay(Date value) {
        set_DateFromTo_TuNgay(value, false);
    }

    public void set_DateFromTo_TuNgay(Date value, boolean pCheckValidDateFromTo) {
        DateFromTo_TuNgay.setValue(value);
        if (pCheckValidDateFromTo) {
            CheckValidDateFromTo_TuNgay();
        }
    }

    protected final MutableLiveData<Date> DateFromTo_DenNgay = new MutableLiveData<>();

    public LiveData<Date> get_DateFromTo_DenNgay() {
        return DateFromTo_DenNgay;
    }

    public void set_DateFromTo_DenNgay(Date value) {
        set_DateFromTo_DenNgay(value, false);
    }

    public void set_DateFromTo_DenNgay(Date value, boolean pCheckValidDateFromTo) {
        DateFromTo_DenNgay.setValue(value);
        if (pCheckValidDateFromTo) {
            CheckValidDateFromTo_DenNgay();
        }
    }

    public void CheckValidDateFromTo_TuNgay() {
        if (DateFromTo_TuNgay.getValue() == null) {
            DateFromTo_DenNgay.setValue(null);
        } else {
            if (Objects.requireNonNull(DateFromTo_DenNgay.getValue()).before(DateFromTo_TuNgay.getValue())) {
                DateFromTo_DenNgay.setValue(DateFromTo_TuNgay.getValue());
            }
        }
    }

    public void CheckValidDateFromTo_DenNgay() {
        if (DateFromTo_DenNgay.getValue() == null) {
            DateFromTo_TuNgay.setValue(null);
        } else {
            if (Objects.requireNonNull(DateFromTo_TuNgay.getValue()).after(DateFromTo_DenNgay.getValue())) {
                DateFromTo_TuNgay.setValue(DateFromTo_DenNgay.getValue());
            }
        }
    }

    protected final CompositeDisposable disposables = new CompositeDisposable();
    private io.reactivex.rxjava3.disposables.Disposable filterDisposable;

    public String FilterValue = "";
    public boolean NeedBackToTop = false;

    public List<T> MyFullDatas = new ArrayList<>();

    public List<T> MyRuntimeDatas = new ArrayList<>();

    public final MutableLiveData<ProcessResult> _LiveDatas = new MutableLiveData<>();

    public LiveData<ProcessResult> MyLiveDatas() {
        return _LiveDatas;
    }

    protected abstract void callApi(io.reactivex.rxjava3.core.ObservableEmitter<List<T>> emitter);

    public void LoadData() {
        LoadData(true);
    }

    public void LoadData(boolean pNeedBackToTop) {
        NeedBackToTop = pNeedBackToTop;
        DoLoadData(true, null);
    }

    public void LoadData(io.reactivex.rxjava3.functions.Consumer<ProcessResult> listener) {
        DoLoadData(false, listener);
    }

    protected final void DoLoadData(boolean updateUI, @Nullable io.reactivex.rxjava3.functions.Consumer<ProcessResult> listener) {
        if (updateUI) {
            _LiveDatas.setValue(ProcessResult.loading());
        }

        disposables.add(
                io.reactivex.rxjava3.core.Observable.create(this::callApi)
                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                        .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.computation())
                        .map(pDetails -> {
                            // 1. Lưu dữ liệu gốc vào biến của Base
                            MyFullDatas = Objects.requireNonNullElseGet(pDetails, ArrayList::new);

                            // 2. Gọi hàm Filter mà lớp con đã override
                            // Sử dụng FilterValue hiện tại của Base
                            List<T> filtered = DoFilterData(FilterValue);

                            // 3. Gọi hàm Sort (hàm này Base đã làm tốt, thường không cần override)
                            MyRuntimeDatas = DoSortData(filtered);

                            return MyRuntimeDatas;
                        })
                        .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                        .subscribe(
                                resultList -> {
                                    ProcessResult result = ProcessResult.success();
                                    if (updateUI) _LiveDatas.setValue(result);
                                    if (listener != null) listener.accept(result);
                                },
                                throwable -> {
                                    ProcessResult errorRes = Objects.equals(throwable.getMessage(), "TOKEN_EXPIRED")
                                            ? ProcessResult.tokenexpired()
                                            : ProcessResult.error(throwable.getMessage());
                                    if (updateUI) _LiveDatas.setValue(errorRes);
                                    if (listener != null) listener.accept(errorRes);
                                }
                        )
        );
    }

    protected List<T> DoFilterData() {
        return DoFilterData(FilterValue);
    }

    protected abstract List<T> DoFilterData(String pFilterText);

    protected List<T> DoSortData(List<T> pDatas) {
        if (pDatas == null || pDatas.isEmpty()) return pDatas != null ? pDatas : new ArrayList<>();

        Map<String, SortConfig> CurrSortConfig = getCurrSortConfig();

        if (CurrSortConfig != null) {
            // 2. Xây dựng chuỗi Comparator động
            Comparator<T> multiComparator = null;

            for (SortConfig config : CurrSortConfig.values()) {
                Comparator<T> currentComp = getComparatorByField(config.field);
                if (currentComp == null) continue;

                if (!config.isAsc) {
                    currentComp = currentComp.reversed();
                }

                if (multiComparator == null) {
                    multiComparator = currentComp;
                } else {
                    multiComparator = multiComparator.thenComparing(currentComp);
                }
            }

            // 3. Thực hiện sắp xếp (Sử dụng TimSort mặc định của Java rất hiệu quả cho 10k dòng)
            if (multiComparator != null) {
                pDatas.sort(multiComparator);
            }
        }

        return pDatas;
    }

    public void FilterData(String pFilterText) {
        FilterData(pFilterText, true);
    }

    public void FilterData(String pFilterText, boolean pNeedBackToTop) {
        NeedBackToTop = pNeedBackToTop;
        _LiveDatas.setValue(ProcessResult.loading());

        // Hủy tác vụ filter trước đó nếu nó vẫn đang chạy
        if (filterDisposable != null && !filterDisposable.isDisposed()) {
            filterDisposable.dispose();
        }

        filterDisposable = io.reactivex.rxjava3.core.Single.fromCallable(() -> {
                    // Thực hiện lọc và sắp xếp trên luồng Computation
                    MyRuntimeDatas = DoSortData(DoFilterData(pFilterText));
                    return MyRuntimeDatas;
                })
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.computation())
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(
                        resultList -> _LiveDatas.setValue(ProcessResult.success(ProcessResult.StatusAdv.ISFILTERED)),
                        throwable -> _LiveDatas.setValue(ProcessResult.error("Lỗi tìm kiếm: " + throwable.getMessage()))
                );

        // Thêm vào tập quản lý chung của ViewModel
        disposables.add(filterDisposable);
    }

    public void SortData() {
        SortData(true);
    }

    public void SortData(boolean pNeedBackToTop) {
        NeedBackToTop = pNeedBackToTop;
        _LiveDatas.setValue(ProcessResult.loading());

        disposables.add(
                io.reactivex.rxjava3.core.Single.fromCallable(() -> {
                            // Chạy logic Filter và Sort ở luồng phụ
                            MyRuntimeDatas = DoSortData(DoFilterData());
                            return MyRuntimeDatas;
                        })
                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.computation()) // Luồng tối ưu cho tính toán
                        .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread()) // Trả về Main Thread
                        .subscribe(
                                filteredsortedList -> _LiveDatas.setValue(ProcessResult.success(ProcessResult.StatusAdv.ISSORTED)),
                                throwable -> _LiveDatas.setValue(ProcessResult.error("Lỗi xử lý dữ liệu: " + throwable.getMessage()))
                        )
        );
    }

    public boolean MyFullDatas_IsEmpty() {
        return (MyFullDatas == null || MyFullDatas.isEmpty());
    }

    public boolean MyRuntimeDatas_IsEmpty() {
        return (MyRuntimeDatas == null || MyRuntimeDatas.isEmpty());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear(); // Hủy toàn bộ tiến trình chạy ngầm khi ViewModel đóng
        // Hủy tác vụ filter trước đó nếu nó vẫn đang chạy
        if (filterDisposable != null && !filterDisposable.isDisposed()) {
            filterDisposable.dispose();
        }
    }
}
