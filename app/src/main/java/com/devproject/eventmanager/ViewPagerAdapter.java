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
                OutFragment outFragment = new OutFragment();
                return outFragment;
            case 1:
                InFragment inFragment = new InFragment();
                return inFragment;
            case 2:
                StatisticsFragment statisticsFragment = new StatisticsFragment();
                return statisticsFragment;
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    //제목 세팅을 한번더 해준다
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "나간 돈";
            case 1:
                return "받은 돈";
            case 2:
                return "통계";
            default:
                return null;
        }
    }
}
