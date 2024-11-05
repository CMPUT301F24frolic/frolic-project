package com.example.frolic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for managing and displaying a list of events in the RecyclerView for the ManageEventsActivity.
 * Each event includes options to view entrants, update the event, view a QR code, and view a map if geolocation is enabled.
 */
public class ManageEventsAdapter extends RecyclerView.Adapter<ManageEventsAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;

    /**
     * Constructs a new ManageEventsAdapter with the given context and list of events.
     *
     * @param context   The activity context for managing intents and resource access.
     * @param eventList A list of Event objects to be displayed in the RecyclerView.
     */
    public ManageEventsAdapter(Context context, List<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    /**
     * Inflates the view for each event item in the RecyclerView.
     *
     * @param parent   The ViewGroup into which the new view will be added after it is bound to an adapter position.
     * @param viewType The view type of the new view.
     * @return A new instance of EventViewHolder containing the inflated view.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.organizers_list_of_events, parent, false);
        return new EventViewHolder(view);
    }

    /**
     * Binds data to each item in the RecyclerView. Displays event details and provides options for viewing entrants,
     * updating the event, viewing the QR code, and viewing the map if geolocation is enabled.
     *
     * @param holder   The ViewHolder that holds the item view.
     * @param position The position of the item in the data set.
     */
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.tvEventName.setText(event.getEventName());

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMM d yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(event.getEventDate());
        holder.tvEventDate.setText(formattedDate);

        // View Entrants
        holder.iconViewEntrants.setOnClickListener(v -> {
            Intent intent = new Intent(context, EntrantDetailsActivity.class);
            intent.putExtra("eventId", event.getEventId());
            context.startActivity(intent);
        });

        // Update Event
        holder.iconUpdateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateEventActivity.class);
            intent.putExtra("eventId", event.getEventId());
            context.startActivity(intent);
        });

        // View QR Code
        holder.iconViewQRCode.setOnClickListener(v -> {
            Intent intent = new Intent(context, DisplayEventQrCodeActivity.class);
            intent.putExtra("eventId", event.getEventId());
            context.startActivity(intent);
        });

        // Only show Map icon if geolocation is required
        if (event.isGeolocationRequired()) {
            holder.iconViewMap.setVisibility(View.VISIBLE);
            holder.iconMapLabel.setVisibility(View.VISIBLE);
            holder.iconViewMap.setOnClickListener(v -> {
                Intent intent = new Intent(context, MapViewActivity.class);
                intent.putExtra("eventId", event.getEventId());
                context.startActivity(intent);
            });
        } else {
            holder.iconViewMap.setVisibility(View.GONE);
            holder.iconMapLabel.setVisibility(View.GONE);
        }
    }

    /**
     * Returns the total number of events in the list.
     *
     * @return The size of the event list.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class to hold references to each item view's elements, such as event name, date,
     * and icons for viewing entrants, updating events, viewing QR codes, and viewing maps.
     */
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventName, tvEventDate;
        ImageView iconViewEntrants, iconUpdateEvent, iconViewQRCode, iconViewMap;
        TextView iconEntrantsLabel, iconUpdateLabel, iconQRCodeLabel, iconMapLabel;

        /**
         * Initializes the view holder for event items, finding each view by ID.
         *
         * @param itemView The individual item view layout for each event.
         */
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvEventDate = itemView.findViewById(R.id.tvEventDate);
            iconViewEntrants = itemView.findViewById(R.id.iconViewEntrants);
            iconUpdateEvent = itemView.findViewById(R.id.iconUpdateEvent);
            iconViewQRCode = itemView.findViewById(R.id.iconViewQRCode);
            iconViewMap = itemView.findViewById(R.id.iconViewMap);

            // Label TextViews for each icon (need to find by id for visibility control on Map)
            iconEntrantsLabel = itemView.findViewById(R.id.iconEntrantsLabel);
            iconUpdateLabel = itemView.findViewById(R.id.iconUpdateLabel);
            iconQRCodeLabel = itemView.findViewById(R.id.iconQRCodeLabel);
            iconMapLabel = itemView.findViewById(R.id.iconMapLabel);
        }
    }
}
