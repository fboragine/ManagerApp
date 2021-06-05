package it.uniba.di.sms2021.managerapp.segreteria.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.segreteria.addItem.AddActivity;
import it.uniba.di.sms2021.managerapp.segreteria.service.TabAdapter;

public class HomeAdminFragment extends Fragment {

    private ViewPager viewPager;

    public HomeAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewHome = inflater.inflate(R.layout.fragment_home_admin, container, false);

        TabLayout tabLayout = viewHome.findViewById(R.id.tabLayout);
        viewPager = viewHome.findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.teachers));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.students));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.exams));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.courses));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final TabAdapter adapter = new TabAdapter(requireContext(), getParentFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        FloatingActionButton btnAddItem = viewHome.findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(v -> {
            if (v.getId() == R.id.btnAddItem) {
                Intent intent = new Intent(requireActivity().getApplicationContext(), AddActivity.class);
                startActivity(intent);
            }
        });

        return viewHome;
    }
}