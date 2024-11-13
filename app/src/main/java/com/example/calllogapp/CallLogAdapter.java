package com.example.calllogapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {
    private List<CallLogEntry> callLogEntries;

    public CallLogAdapter(List<CallLogEntry> callLogEntries) {
        this.callLogEntries = callLogEntries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_log_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CallLogEntry entry = callLogEntries.get(position);
        holder.tvPhoneNumber.setText(entry.getPhoneNumber());
        holder.tvCallType.setText(entry.getCallType());
        holder.tvCallDate.setText(entry.getCallDate());
        holder.tvCallDuration.setText(entry.getCallDuration());
        holder.tvCallConnected.setText(entry.isConnected() ? "Yes" : "No");
    }

    @Override
    public int getItemCount() {
        return callLogEntries.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvPhoneNumber;
        public TextView tvCallType;
        public TextView tvCallDate;
        public TextView tvCallDuration;
        public TextView tvCallConnected;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvCallType = itemView.findViewById(R.id.tvCallType);
            tvCallDate = itemView.findViewById(R.id.tvCallDate);
            tvCallDuration = itemView.findViewById(R.id.tvCallDuration);
            tvCallConnected = itemView.findViewById(R.id.tvCallConnected);
        }
    }
}
