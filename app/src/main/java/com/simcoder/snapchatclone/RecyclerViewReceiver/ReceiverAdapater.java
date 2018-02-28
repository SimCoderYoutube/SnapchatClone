package com.simcoder.snapchatclone.RecyclerViewReceiver;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.simcoder.snapchatclone.R;
import com.simcoder.snapchatclone.UserInformation;

import java.util.List;

/**
 * Created by simco on 1/24/2018.
 */

public class ReceiverAdapater extends RecyclerView.Adapter<ReceiverViewHolder>{

    private List<ReceiverObject> usersList;
    private Context context;

    public ReceiverAdapater(List<ReceiverObject> usersList, Context context){
        this.usersList = usersList;
        this.context = context;
    }
    @Override
    public ReceiverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_receiver_item, null);
        ReceiverViewHolder rcv = new ReceiverViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final ReceiverViewHolder holder, int position) {
        holder.mEmail.setText(usersList.get(position).getEmail());
        holder.mReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean receiveState = !usersList.get(holder.getLayoutPosition()).getReceive();
                usersList.get(holder.getLayoutPosition()).setReceive(receiveState);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }
}
