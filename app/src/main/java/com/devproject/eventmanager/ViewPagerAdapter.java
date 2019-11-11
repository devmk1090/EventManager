package com.devproject.eventmanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                InputFragment inputFragment = new InputFragment();
                return inputFragment;
            case 1:
                OutputFragment outputFragment = new OutputFragment();
                return outputFragment;
            case 2:
                CompareFragment compareFragment = new CompareFragment();
                return compareFragment;
            case 3:
                SettingFragment settingFragment = new SettingFragment();
                return settingFragment;

                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    //제목 세팅을 한번더 해준다
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Input";
            case 1:
                return "Output";
            case 2:
                return "Compare";
            case 3:
                return "Setting";

            default:
                return null;
        }
    }
}
