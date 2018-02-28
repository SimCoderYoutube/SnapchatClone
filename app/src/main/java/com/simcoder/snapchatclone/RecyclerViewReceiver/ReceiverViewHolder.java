package com.simcoder.snapchatclone.RecyclerViewReceiver;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.simcoder.snapchatclone.R;

/**
 * Created by simco on 1/24/2018.
 */

public class ReceiverViewHolder extends RecyclerView.ViewHolder{
    public TextView mEmail;
    public CheckBox mReceive;

    public ReceiverViewHolder(View itemView){
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mReceive = itemView.findViewById(R.id.receive);

    }


}
