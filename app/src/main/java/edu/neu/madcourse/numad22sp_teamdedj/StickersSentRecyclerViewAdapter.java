package edu.neu.madcourse.numad22sp_teamdedj;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StickersSentRecyclerViewAdapter extends RecyclerView.Adapter<StickersSentRecyclerViewAdapter.StickersSentRecyclerViewHolder> {

    private final ArrayList<StickersSentActivity.StickersSent> StickersSentItems;
    private ListItemClickListener listener;

    public StickersSentRecyclerViewAdapter(ArrayList<StickersSentActivity.StickersSent> StickersSentItems) {
        this.StickersSentItems = StickersSentItems;
    }

    public class StickersSentRecyclerViewHolder extends RecyclerView.ViewHolder {
        public ImageView sticker;
        public TextView stickerCount;

        public StickersSentRecyclerViewHolder(View itemView, final ListItemClickListener listener) {
            super(itemView);
            sticker = itemView.findViewById(R.id.stickersSentLabel);
            stickerCount = itemView.findViewById(R.id.stickerCount);
        }
    }

    @NonNull
    @Override
    public StickersSentRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_sent_item, parent, false);
        return new StickersSentRecyclerViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(StickersSentRecyclerViewHolder holder, int position) {
        holder.sticker.setImageResource(StickersSentItems.get(position).getstickerSource());
        holder.stickerCount.setText(StickersSentItems.get(position).getstickerCount());
    }

    @Override
    public int getItemCount() {
        return StickersSentItems.size();
    }

    public void setOnItemClickListener(ListItemClickListener listener) {
        this.listener = listener;
    }
}