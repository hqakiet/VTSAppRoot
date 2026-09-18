package com.vts.vtsapproot.UI.Adapters.Bases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.vts.vtsapproot.API.Interfaces.BaseSingleProcessInterface;

import java.util.ArrayList;
import java.util.List;

public abstract class ListAdapter_ViewBinding<T, VB extends ViewBinding>
        extends ListAdapter<T, ListAdapter_ViewBinding.ViewHolder_Base<VB>> {

    protected Context My_Context;

    protected ListAdapter_ViewBinding(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    public static class ViewHolder_Base<VB extends ViewBinding>
            extends RecyclerView.ViewHolder {
        public final VB binding;

        public ViewHolder_Base(VB binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder_Base<VB> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        My_Context = parent.getContext();
        return new ViewHolder_Base<>(createBinding(LayoutInflater.from(My_Context), parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_Base<VB> holder, int position) {
        T item = getItem(position);
        if (item != null) {
            bindData(holder.binding, item, position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder_Base<VB> holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            T item = getItem(position);
            if (item != null) {
                bindPayload(holder.binding, item, position, payloads);
            }
        }
    }

    // Lấy item tại vị trí cụ thể (Hỗ trợ tốt hơn cho việc click)
    public T getItemAt(int position) {
        return getItem(position);
    }

    public void removeItem(int position) {
        List<T> currentList = new ArrayList<>(getCurrentList());
        currentList.remove(position);
        submitList(currentList);
    }

    public void restoreItem(T item, int position) {
        List<T> currentList = new ArrayList<>(getCurrentList());
        currentList.add(position, item);
        submitList(currentList);
    }

    public void submitListAndReset(List<T> data, BaseSingleProcessInterface complete) {
        this.submitList(null);
        this.submitList(data, complete::onCompleted);
    }

    // Các hàm bắt buộc lớp con triển khai
    protected abstract VB createBinding(LayoutInflater inflater, ViewGroup parent);
    protected abstract void bindData(VB binding, T item, int position);

    // Tùy chọn: Xử lý cập nhật từng phần (Partial update)
    protected void bindPayload(VB binding, T item, int position, List<Object> payloads) {}

}