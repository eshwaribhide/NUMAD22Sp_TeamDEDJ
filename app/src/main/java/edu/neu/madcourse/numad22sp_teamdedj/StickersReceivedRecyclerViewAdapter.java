package edu.neu.madcourse.numad22sp_teamdedj;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

    public class StickersReceivedRecyclerViewAdapter extends RecyclerView.Adapter<StickersReceivedRecyclerViewAdapter.StickersReceivedRecyclerViewHolder> {

        private final ArrayList<StickersReceivedActivity.StickersReceived> StickersReceivedItems;
        private ListItemClickListener listener;

        public StickersReceivedRecyclerViewAdapter(ArrayList<StickersReceivedActivity.StickersReceived> stickersReceivedItems) {
            this.StickersReceivedItems = stickersReceivedItems;
        }

        public class StickersReceivedRecyclerViewHolder extends RecyclerView.ViewHolder {
            public ImageView sticker;
            public TextView senderName;
            public TextView timeSent;

            public StickersReceivedRecyclerViewHolder(View itemView, final ListItemClickListener listener) {
                super(itemView);
                sticker = itemView.findViewById(R.id.sticker);
                senderName = itemView.findViewById(R.id.senderName);
                timeSent = itemView.findViewById(R.id.timeSent);

            }
        }

        @NonNull
        @Override
        public StickersReceivedRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stickers_received_item, parent, false);
            return new StickersReceivedRecyclerViewHolder(view, listener);
        }

        @Override
        public void onBindViewHolder(StickersReceivedRecyclerViewHolder holder, int position) {
            holder.sticker.setImageResource(StickersReceivedItems.get(position).getstickerSource());
            holder.senderName.setText(StickersReceivedItems.get(position).getstickerSender());
            holder.timeSent.setText(StickersReceivedItems.get(position).getstickerTime());
        }

        @Override
        public int getItemCount() {
            return StickersReceivedItems.size();
        }

        public void setOnItemClickListener(ListItemClickListener listener) {
            this.listener = listener;
        }
    }
