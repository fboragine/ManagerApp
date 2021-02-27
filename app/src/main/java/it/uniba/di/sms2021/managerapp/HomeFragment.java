package it.uniba.di.sms2021.managerapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import javax.security.auth.callback.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "SimpleToolbarTest";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private View viewHome;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setContentView(R.layout.activity_student);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);

        /*
        // Create a new Tab named "In Progress"
        TabLayout.Tab inProgressTab = tabLayout.newTab();
        inProgressTab.setText(getString(R.string.in_progress)); // set the Text for the In Progress Tab
        tabLayout.addTab(inProgressTab); // add  the tab at in the TabLayout

        // Create a new Tab named "Closed"
        TabLayout.Tab closedTab = tabLayout.newTab();
        closedTab.setText(getString(R.string.closed)); // set the Text for the In Progress Tab
        tabLayout.addTab(closedTab); // add  the tab at in the TabLayout

         */

        /*
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // get the current selected tab's position and replace the fragment accordingly
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new InProgressProjectsFragment();
                        break;
                    case 1:
                        fragment = new ClosedProjectsFragment();
                        break;
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.home_frame_layout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

         */
    }

    private MyAdapter createAdapter() {
        return new MyAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewHome = inflater.inflate(R.layout.fragment_home, container, false);

        tabLayout = (TabLayout) viewHome.findViewById(R.id.tab_home);
        viewPager = (ViewPager2) viewHome.findViewById(R.id.home_view_pager);
        viewPager.setAdapter(createAdapter());

        if(tabLayout == null) {
            Log.d(TAG, "tabLayout è null");
        }
        if(viewPager == null) {
            Log.d(TAG, "viewpager è null");
        }

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText("Tab " + (position + 1));
            }
        }).attach();

        return viewHome;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {

        // Add Search Menu Item
        int searchId = GuestActivity.SEARCH_ITEM_ID;
        if (menu.findItem(searchId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem search = menu.add(
                    Menu.NONE,
                    searchId,
                    1,
                    getString(R.string.search)
            );

            // Set an icon for the new menu item
            search.setIcon(R.drawable.ic_search);

            // Set the show as action flags for new menu item
            search.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

        }

        // Add Filter Menu Item
        int filterId = GuestActivity.FILTER_ITEM_ID;
        if (menu.findItem(filterId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem filter = menu.add(
                    Menu.NONE,
                    filterId,
                    2,
                    getString(R.string.action_filter)
            );

            // Set an icon for the new menu item
            filter.setIcon(R.drawable.ic_baseline_filter_alt_24);

            // Set the show as action flags for new menu item
            filter.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            filter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

        }

        super.onPrepareOptionsMenu(menu);
    }
}