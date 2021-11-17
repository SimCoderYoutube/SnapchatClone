package com.simcoder.snapchatclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.simcoder.snapchatclone.MainActivity;
import com.simcoder.snapchatclone.Objects.UserObject;
import com.simcoder.snapchatclone.R;
import com.simcoder.snapchatclone.fragment.main.StoryFragment;

import java.util.List;

/**
 * Adapter for the chat and the story fragment
 */
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolders> {

    private List<UserObject> usersList;
    private Context context;
    private String chatOrStory;
    private StoryFragment storyFragment;

    /**
     * adapter constructor
     * @param usersList - list of the users that have posts to be shown
     * @param chatOrStory - "chat" or "story" depending on what fragment created the adapter
     * @param storyFragment - object of the storyFragment that called the adapter
     * @param context - context of the original activity
     */
    public StoryAdapter(List<UserObject> usersList, String chatOrStory, StoryFragment storyFragment, Context context) {
        this.usersList = usersList;
        this.chatOrStory = chatOrStory;
        this.storyFragment = storyFragment;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_story_item, null);
        return new StoryViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(final StoryViewHolders holder, int position) {
        holder.mName.setText(usersList.get(position).getName());
        if (usersList.get(holder.getLayoutPosition()).getImage() != null)
            Glide.with(context)
                    .load(usersList.get(holder.getLayoutPosition()).getImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.mProfile);

        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (usersList.size() == 0)
                    return;

                String userId = usersList.get(holder.getLayoutPosition()).getId();
                String image = usersList.get(holder.getLayoutPosition()).getImage();
                String name = usersList.get(holder.getLayoutPosition()).getName();

                if (chatOrStory.equals("chat")) {
                    storyFragment.results.remove(holder.getLayoutPosition());
                    notifyDataSetChanged();
                }

                ((MainActivity) context).openDisplaySnapFragment(userId, image, name, chatOrStory);

            }
        });

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
    static class StoryViewHolders extends RecyclerView.ViewHolder {
        TextView mName;
        LinearLayout mLayout;
        ImageView mProfile;

        StoryViewHolders(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mProfile = itemView.findViewById(R.id.profile);
            mLayout = itemView.findViewById(R.id.layout);
        }
    }
}
