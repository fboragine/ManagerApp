package it.uniba.di.sms2021.managerapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.Arrays;

import javax.security.auth.callback.Callback;

public class HomeFragment extends Fragment {

    private static final String TAG = "SimpleToolbarTest";

    private ArrayList<String> inProgressProjectName;
    private ArrayList<String> inProgressProjectExam;
    private ArrayList<String> closedProjectName;
    private ArrayList<String> closedProjectExam;
    private RecyclerView inProgressRecyclerView;
    private RecyclerView closedRecyclerView;
    private RecyclerView.LayoutManager inProgressLayoutManager;
    private RecyclerView.LayoutManager closedLayoutManager;
    private RecyclerView.Adapter inProgressAdapter;
    private RecyclerView.Adapter closedAdapter;

    private View viewHome;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewHome = inflater.inflate(R.layout.fragment_home, container, false);

        inProgressProjectName = new ArrayList<>(Arrays.asList("Project 1", "Project 2", "Project 3", "Project 4", "Project 5", "Project 6", "Project 7", "Project 8", "Project 9", "Project 10"));
        inProgressProjectExam = new ArrayList<>(Arrays.asList("Exam 1", "Exam 2", "Exam 3", "Exam 4", "Exam 5", "Exam 6", "Exam 7", "Exam 8", "Exam 9", "Exam 10"));

        closedProjectName = new ArrayList<>(Arrays.asList("Closed Project 1", "Closed Project 2", "Closed Project 3", "Closed Project 4", "Closed Project 5", "Closed Project 6", "Closed Project 7", "Closed Project 8", "Closed Project 9", "Closed Project 10"));
        closedProjectExam = new ArrayList<>(Arrays.asList("Exam 1", "Exam 2", "Exam 3", "Exam 4", "Exam 5", "Exam 6", "Exam 7", "Exam 8", "Exam 9", "Exam 10"));

        inProgressRecyclerView = viewHome.findViewById(R.id.inProgressRecyclerView);
        inProgressRecyclerView.setHasFixedSize(true);
        closedRecyclerView = viewHome.findViewById(R.id.closedRecyclerView);
        closedRecyclerView.setHasFixedSize(true);

        inProgressLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        inProgressRecyclerView.setLayoutManager(inProgressLayoutManager);
        closedLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        closedRecyclerView.setLayoutManager(closedLayoutManager);
        inProgressAdapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), inProgressProjectName, inProgressProjectExam);
        inProgressRecyclerView.setAdapter(inProgressAdapter);
        closedAdapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), closedProjectName, closedProjectExam);
        closedRecyclerView.setAdapter(closedAdapter);

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