package com.example.frolic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AdminImagesAdapter extends RecyclerView.Adapter<AdminImagesAdapter.ImageViewHolder> {
    private ArrayList<ImageData> images;
    private final OnImageActionListener listener;

    public interface OnImageActionListener {
        void onDeleteClicked(ImageData imageData);
    }

    public AdminImagesAdapter(ArrayList<ImageData> images, OnImageActionListener listener) {
        this.images = images;
        this.listener = listener;
    }

    public void updateImages(ArrayList<ImageData> newImages) {
        this.images = newImages;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_view, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageData imageData = images.get(position);

        holder.descriptionTextView.setText(imageData.getType());
        holder.imageView.setImageBitmap(decodeBase64ToBitmap(imageData.getUrl()));

        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClicked(imageData));
    }

    private Bitmap decodeBase64ToBitmap(String base64String) {
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionTextView;
        Button deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}


