package com.example.mathprojectavishaigozland.tramp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private final List<PendingRequest> list;
    private final OnRequestActionListener listener;

    public RequestAdapter(List<PendingRequest> list, OnRequestActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PendingRequest req = list.get(position);
        holder.tvInfo.setText(req.getUserName() + " (" + req.getRequestedSeats() + " מקומות)");

        if ("מאושר".equals(req.getStatus())) {
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
            holder.tvStatusLabel.setVisibility(View.VISIBLE);
            holder.tvStatusLabel.setText("מאושר בנסיעה");
            holder.tvStatusLabel.setTextColor(Color.GREEN);
        } else {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            holder.tvStatusLabel.setVisibility(View.GONE);
            holder.btnApprove.setOnClickListener(v -> listener.onApprove(req));
            holder.btnReject.setOnClickListener(v -> listener.onReject(req));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnRequestActionListener {
        void onApprove(PendingRequest request);

        void onReject(PendingRequest request);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInfo, tvStatusLabel;
        Button btnApprove, btnReject;

        public ViewHolder(View v) {
            super(v);
            tvInfo = v.findViewById(R.id.tvRequesterName); // וודא שה-ID תואם ל-XML שלך
            tvStatusLabel = v.findViewById(R.id.tvRequestedSeats); // אפשר להשתמש בזה כסטטוס
            btnApprove = v.findViewById(R.id.btnApprove);
            btnReject = v.findViewById(R.id.btnReject);
        }
    }
}