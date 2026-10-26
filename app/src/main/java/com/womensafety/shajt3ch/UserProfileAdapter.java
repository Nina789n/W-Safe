package com.womensafety.shajt3ch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.ViewHolder> {
    private List<UserProfile> users;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(UserProfile user);
    }

    public UserProfileAdapter(List<UserProfile> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserProfile user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvInitials, tvUserName, tvUserPhone, tvLastActive, tvStatus;
        private MaterialCardView statusBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
            tvLastActive = itemView.findViewById(R.id.tvLastActive);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            statusBadge = itemView.findViewById(R.id.statusBadge);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(users.get(getAdapterPosition()));
                }
            });
        }

        void bind(UserProfile user) {
            tvInitials.setText(user.getInitials());
            tvUserName.setText(user.getName());
            tvUserPhone.setText(user.getPhone());
            tvLastActive.setText("Last active: " + user.getLastActive());
            tvStatus.setText(user.getStatus());

            // Set status badge color
            int statusColor;
            switch (user.getStatus()) {
                case "SOS":
                    statusColor = 0xFFF44336; // Red
                    break;
                case "Alert":
                    statusColor = 0xFFFF9800; // Orange
                    break;
                case "Safe":
                default:
                    statusColor = 0xFF4CAF50; // Green
                    break;
            }
            statusBadge.setCardBackgroundColor(statusColor);
        }
    }
}