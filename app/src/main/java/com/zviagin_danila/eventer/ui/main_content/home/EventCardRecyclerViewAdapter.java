package com.zviagin_danila.eventer.ui.main_content.home;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zviagin_danila.eventer.Event;
import com.zviagin_danila.eventer.R;
import com.zviagin_danila.eventer.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventCardRecyclerViewAdapter extends RecyclerView.Adapter<EventCardRecyclerViewAdapter.EventViewHolder>{

    Location userLocation;

    private ArrayList<Event> events;
    private OnEventClickListener listener;

    public interface OnEventClickListener {
        void onEventClick(int position);
    }

    public void setOnEventClickListener(OnEventClickListener listener) {
        this.listener = listener;
    }

    public EventCardRecyclerViewAdapter(ArrayList<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card, parent, false);
        EventViewHolder viewHolder = new EventViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event currentEvent = events.get(position);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Double distance = Utils.distance(currentEvent.getLocationLatitude(),
                        currentEvent.getLocationLongitude(),
                        location.getLatitude(),
                        location.getLongitude());
                holder.distanceTextView.setText(distance + "км");
            }
        };

        holder.titleTextView.setText(currentEvent.getTitle());
        holder.timeTextView.setText(Utils.getReadableDateTime(currentEvent.getTimeStart(), currentEvent.getTimeEnd()));
        holder.addFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                CollectionReference currentUserFavorites = firebaseFirestore
                        .collection("users")
                        .document(firebaseAuth.getCurrentUser().getUid())
                        .collection("favorites");

            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        public ImageView photoImageView;
        public TextView distanceTextView;
        public TextView titleTextView;
        public TextView timeTextView;
        public ImageButton addFavorite;

        public EventViewHolder(@NonNull View itemView, OnEventClickListener listener) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            distanceTextView = itemView.findViewById(R.id.distanceTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            addFavorite = itemView.findViewById(R.id.add_favorite);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEventClick(position);
                        }
                    }
                }
            });
        }
    }
}
