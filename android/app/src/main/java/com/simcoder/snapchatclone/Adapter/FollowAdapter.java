package com.simcoder.snapchatclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.simcoder.snapchatclone.MainActivity;
import com.simcoder.snapchatclone.Objects.UserObject;
import com.simcoder.snapchatclone.R;

import java.util.List;

/**
 * Adapter for the user list allowing the user to follow/unfollow other users
 */
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.FollowViewHolders> {

    private List<UserObject> usersList;
    private Context context;

    /**
     * adapter constructor
     * @param usersList - list of the users to display
     * @param context - context of the original activity
     */
    public FollowAdapter(List<UserObject> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recylerview_followers_item, null);
        return new FollowViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(final FollowViewHolders holder, int position) {
        holder.mName.setText(usersList.get(position).getEmail());

        if (((MainActivity) context).listFollowing.contains(usersList.get(holder.getLayoutPosition()).getId())) {
            buttonSelected(holder);
        } else {
            buttonIdle(holder);
        }

        if (usersList.get(holder.getLayoutPosition()).getImage() != null)
            Glide.with(context)
                    .load(usersList.get(holder.getLayoutPosition()).getImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.mProfile);

        holder.mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (!((MainActivity) context).listFollowing.contains(usersList.get(holder.getLayoutPosition()).getId())) {
                    buttonSelected(holder);
                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getId()).setValue(true);
                } else {
                    buttonIdle(holder);
                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("following").child(usersList.get(holder.getLayoutPosition()).getId()).removeValue();
                }
            }
        });
    }

    /**
     * Set the button to "follow" changing the color and text accordingly
     * @param holder - holder of the layout of the element in a position
     */
    private void buttonIdle(FollowViewHolders holder) {
        holder.mFollow.setText(R.string.follow);
        holder.mFollow.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        holder.mFollow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_idle));
    }

    /**
     * Set the button to "following" changing the color and text accordingly
     * @param holder - holder of the layout of the element in a position
     */
    private void buttonSelected(FollowViewHolders holder) {
        holder.mFollow.setText(R.string.following);
        holder.mFollow.setTextColor(context.getResources().getColor(R.color.white));
        holder.mFollow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.button_round));
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
    static class FollowViewHolders extends RecyclerView.ViewHolder {
        TextView mName;
        Button mFollow;
        ImageView mProfile;

        FollowViewHolders(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mFollow = itemView.findViewById(R.id.follow);
            mProfile = itemView.findViewById(R.id.profile);
        }
    }
}
