package com.zviagin_danila.eventer.ui.main_content.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zviagin_danila.eventer.Event;
import com.zviagin_danila.eventer.R;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeFragment extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference eventsDatabaseReference;
    private ChildEventListener eventsChildEventListener;

    private HashMap<String, Boolean> eventsMap;
    private ArrayList<Event> eventArrayList;
    private RecyclerView eventRecyclerView;
    private EventCardRecyclerViewAdapter eventAdapter;
    private RecyclerView.LayoutManager eventLayoutManager;
    FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        eventsMap = new HashMap<>();
        eventArrayList = new ArrayList<>();

        // this method gets all events from firestore and build recycler view
        getEvents(view);

        setUpToolbar(view);

        // Set up the RecyclerView
        buildRecyclerView(view);

        return view;
    }

    private void getEvents(View view) {
        firebaseFirestore.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Event event = new Event();
                                event.setTitle((String) document.get("title"));
                                event.setAuthor((String) document.get("author"));
                                event.setTimeStart((Long) document.get("startDateTime"));
                                event.setTimeEnd((Long) document.get("endDateTime"));
                                event.setDescription((String) document.get("description"));
                                event.setPrice((String) document.get("price"));
                                event.setLocationLatitude((Double) document.get("location_latitude"));
                                event.setLocationLongitude((Double) document.get("location_longitude"));
                                event.setId((Long) document.get("eventId"));

                                eventArrayList.add(event);
                            }

                            // build recycler view
                            buildRecyclerView(view);
                        } else {
                            Toast.makeText(view.getContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void setUpToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.app_bar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
    }



    private void buildRecyclerView(View view) {

        eventRecyclerView = view.findViewById(R.id.event_recycler_view);
        eventRecyclerView.setHasFixedSize(true);
        eventLayoutManager = new LinearLayoutManager(getActivity());
        eventAdapter = new EventCardRecyclerViewAdapter(eventArrayList);

        eventRecyclerView.setLayoutManager(eventLayoutManager);
        eventRecyclerView.setAdapter(eventAdapter);

        eventAdapter.setOnEventClickListener(new EventCardRecyclerViewAdapter.OnEventClickListener() {
            @Override
            public void onEventClick(int position) {
                goToEvent(position);
            }
        });
    }

    private void goToEvent(int position) {
        Intent intent = new Intent(this.getActivity(), EventActivity.class);
        intent.putExtra("title", eventArrayList.get(position).getTitle());
        intent.putExtra("timeStart", eventArrayList.get(position).getTimeStart());
        intent.putExtra("timeEnd", eventArrayList.get(position).getTimeEnd());
        intent.putExtra("author", eventArrayList.get(position).getAuthor());
        intent.putExtra("description", eventArrayList.get(position).getDescription());
        intent.putExtra("location_latitude", eventArrayList.get(position).getLocationLatitude());
        intent.putExtra("location_longitude", eventArrayList.get(position).getLocationLongitude());
        intent.putExtra("price", eventArrayList.get(position).getPrice());
        startActivity(intent);
    }
}