package it.uniba.di.sms2021.managerapp.segreteria;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {
    private Context context;
    private int totalTabs;

    public TabAdapter(Context context, @NonNull FragmentManager fm, int totalTabs) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TeachersListFragment();
            case 1:
                return new StudentsListFragment();
            case 2:
                return new ExamsListFragment();
            case 3:
                return new CoursesListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
