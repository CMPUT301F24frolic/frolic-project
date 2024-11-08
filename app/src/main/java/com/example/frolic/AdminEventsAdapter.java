package com.example.frolic;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Adapter for displaying events in the admin management interface.
 * Provides functionality for viewing and deleting events.
 */
public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.ViewHolder> {
    private ArrayList<Event> events;
    private OnEventActionListener listener;

    public interface OnEventActionListener {
        void onEventDeleted(String eventId);
        void onEventSelected(Event event);
    }

    public AdminEventsAdapter(ArrayList<Event> events, OnEventActionListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvEventName.setText(event.getEventName());

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d yyyy", Locale.getDefault());
        holder.tvOrganizerName.setText(dateFormat.format(event.getEventDate()));

        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            Log.d("AdminEventsAdapter", "Event clicked: " + event.getEventId());
            listener.onEventSelected(event);
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEvents(ArrayList<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName;
        TextView tvOrganizerName;

        ViewHolder(View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvOrganizerName = itemView.findViewById(R.id.tvOrganizerName);
        }
    }
}