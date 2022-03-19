package edu.neu.madcourse.numad22sp_teamdedj;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

    public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.HistoryRecyclerViewHolder> {

        private final ArrayList<HistoryActivity.HistoryItem> historyItems;
        private ListItemClickListener listener;

        public HistoryRecyclerViewAdapter(ArrayList<HistoryActivity.HistoryItem> historyItems) {
            this.historyItems = historyItems;
        }

        public class HistoryRecyclerViewHolder extends RecyclerView.ViewHolder {
            public ImageView sticker;
            public TextView senderName;
            public TextView timeSent;

            public HistoryRecyclerViewHolder(View itemView, final ListItemClickListener listener) {
                super(itemView);
                sticker = itemView.findViewById(R.id.sticker);
                senderName = itemView.findViewById(R.id.senderName);
                timeSent = itemView.findViewById(R.id.timeSent);

            }
        }

        @NonNull
        @Override
        public HistoryRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
            return new HistoryRecyclerViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(HistoryRecyclerViewHolder holder, int position) {
            holder.sticker.setImageResource(historyItems.get(position).getstickerSource());
            holder.senderName.setText(historyItems.get(position).getstickerSender());
            holder.timeSent.setText(historyItems.get(position).getstickerTime());
        }

        @Override
        public int getItemCount() {
            return historyItems.size();
        }

        public void setOnItemClickListener(ListItemClickListener listener) {
            this.listener = listener;
        }
    }
