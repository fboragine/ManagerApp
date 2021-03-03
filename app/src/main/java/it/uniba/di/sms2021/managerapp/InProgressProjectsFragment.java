package it.uniba.di.sms2021.managerapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InProgressProjectsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InProgressProjectsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private ArrayList<String> projectName;
    private ArrayList<Integer> projectImage;
    private String[] projectDescription;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    public InProgressProjectsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InProgressProjectsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InProgressProjectsFragment newInstance(String param1, String param2) {
        InProgressProjectsFragment fragment = new InProgressProjectsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_in_progress_projects, container, false);

        projectName = new ArrayList<>(Arrays.asList("Project 1", "Project 2", "Project 3", "Project 4", "Project 5", "Project 6", "Project 7", "Project 8", "Project 9", "Project 10"));
        projectImage = new ArrayList<>(Arrays.asList(R.drawable.ic_collections_bookmark, R.drawable.ic_collections_bookmark, R.drawable.ic_collections_bookmark, R.drawable.ic_collections_bookmark, R.drawable.ic_collections_bookmark, R.drawable.ic_collections_bookmark, R.drawable.ic_collections_bookmark, R.drawable.ic_collections_bookmark, R.drawable.ic_collections_bookmark, R.drawable.ic_collections_bookmark));
        projectDescription = new String[]{"Project 1 description", "Project 2 description", "Project 3 description", "Project 4 description", "Project 5 description", "Project 6 description", "Project 7 description", "Project 8 description", "Project 9 description", "Project 10 description"};

        //listView = view.findViewById(R.id.listView);
        recyclerView = view.findViewById(R.id.inProgressRecyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(getActivity().getApplicationContext(), projectName, projectImage);
        recyclerView.setAdapter(adapter);

        /*
        for (int i = 0; i<title.length; i++) {
            Model model = new Model(title[i], description[i]);
            //bind all strings in an array
            arrayList.add(model);
        }

        //pass results to listViewAdapter class
        adapter = new ListViewAdapter(getActivity().getApplicationContext(), arrayList);

        //bind the adapter to the listview
        listView.setAdapter(adapter);

         */

        return view;
    }
}