package com.simcoder.snapchatclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.simcoder.snapchatclone.Objects.UserObject;
import com.simcoder.snapchatclone.R;

import java.util.List;


/**
 * Adapter for the user list allowing the user to pick users to send an image to
 */
public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverAdapter.ReceiverViewHolder> {

    private List<UserObject> usersList;
    private Context context;

    /**
     * adapter constructor
     * @param usersList - list of the users to display
     * @param context - context of the original activity
     */
    public ReceiverAdapter(List<UserObject> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReceiverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_receiver_item, null);
        return new ReceiverViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(final ReceiverViewHolder holder, int position) {
        holder.mName.setText(usersList.get(position).getName());
        holder.mReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean receiveState = !usersList.get(holder.getLayoutPosition()).getReceive();
                usersList.get(holder.getLayoutPosition()).setReceive(receiveState);
            }
        });

        if (!usersList.get(holder.getLayoutPosition()).getImage().equals(""))
            Glide.with(context)
                    .load(usersList.get(holder.getLayoutPosition()).getImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.mProfile);
    }

    /**
     * gets the size of the arrayList
     * @return size
     */
    @Override
    public int getItemCount() {
        return this.usersList.size();
    }

    /**
     * View Holder of the layout
     */
    static class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        CheckBox mReceive;
        ImageView mProfile;

        ReceiverViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mReceive = itemView.findViewById(R.id.receive);
            mProfile = itemView.findViewById(R.id.profile);
        }
    }
}
