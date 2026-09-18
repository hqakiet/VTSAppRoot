package com.vts.vtsapproot.Tools;

import java.util.Objects;

public final class CustomListEvents<T> {
    private final Type type;
    private final T data;

    public enum Type {NEEDREFRESHDATA, NEEDSTARTFILTER, NEEDSTOPFILTER}

    public CustomListEvents(Type type, T data) {
        this.type = type;
        this.data = data;
    }

    public Type type() {
        return type;
    }

    public T data() {
        return data;
    }

    public static <T> CustomListEvents<T> NeedRefreshData() {
        return new CustomListEvents<>(Type.NEEDREFRESHDATA, null);
    }

    public static <T> CustomListEvents<T> NeedStartFilter(T data) {
        return new CustomListEvents<>(Type.NEEDSTARTFILTER, data);
    }

    public static <T> CustomListEvents<T> NeedStopFilter() {
        return new CustomListEvents<>(Type.NEEDSTOPFILTER, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomListEvents<?> that = (CustomListEvents<?>) o;
        return type == that.type && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, data);
    }

    @Override
    public String toString() {
        return "CustomListEvents[" +
                "type=" + type +
                ", data=" + data +
                ']';
    }
}
