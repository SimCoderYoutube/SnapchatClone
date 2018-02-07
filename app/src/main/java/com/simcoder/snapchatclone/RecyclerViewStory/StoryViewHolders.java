package com.simcoder.snapchatclone.RecyclerViewStory;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.simcoder.snapchatclone.R;

/**
 * Created by simco on 1/24/2018.
 */

public class StoryViewHolders extends RecyclerView.ViewHolder{
    public TextView mEmail;

    public StoryViewHolders(View itemView){
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);

    }


}
