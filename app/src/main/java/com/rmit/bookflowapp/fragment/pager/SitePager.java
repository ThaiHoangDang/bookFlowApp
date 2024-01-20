package com.rmit.bookflowapp.fragment.pager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class SitePager extends FragmentStateAdapter {
    private static final String TAG = "SitePager";

    private final List<Fragment> fragments = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();
    private final List<Integer> icons = new ArrayList<>();

    public SitePager(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment, String title, int drawable) {
        fragments.add(fragment);
        titles.add(title);
        icons.add(drawable);
    }

    public String getPageTitle(int position) {
        return titles.get(position);
    }

    public Integer getPageIcon(int position) {
        return icons.get(position);
    }
}