package com.vts.vtsapproot.UI.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vts.vtsapproot.UI.Fragments.FraPrivacy;
import com.vts.vtsapproot.UI.Fragments.FraTerms;

public class FragmentState_Policy
        extends FragmentStateAdapter {

    public FragmentState_Policy(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 1 -> new FraPrivacy();
            default -> new FraTerms();
        };
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
