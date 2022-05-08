package com.example.chatapplication;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {
    private List<Post> listdata;
    PostActivities activity;
    private actionOnPosts action_interface;
    Post post;

    public PostRecyclerAdapter(List<Post> listdata1, actionOnPosts interface_action, PostActivities activity) {
        this.listdata = listdata1;
        this.activity = activity;
        this.action_interface = interface_action;
    }

    @NonNull
    @Override
    public PostRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_for_posts, parent, false);
        PostRecyclerAdapter.ViewHolder viewHolder = new PostRecyclerAdapter.ViewHolder(listItem, activity);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecyclerAdapter.ViewHolder holder, int position) {
        post = listdata.get(position);
        Log.e("Post Array", String.valueOf(listdata.size()));
        holder.textView.append(listdata.get(position).getMessage());
        holder.imageView.setImageBitmap(listdata.get(position).getImage());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_interface.showDetail(post);
            }
        });
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                action_interface.lonGPress();
                return true;
            }
        });
        if (!activity.isContexualModelEnabled) {
            holder.checkbox.setVisibility(View.GONE);
        } else {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return this.listdata.size();
    }

    public void removeItems(List<Post> selected_posts) {
        DataBaseHandler db = new DataBaseHandler(activity);
        //Here we will perform deletion operation
        for (int i = 0; i < selected_posts.size(); i++) {
            String id = String.valueOf(selected_posts.get(i).getID());
            db.deletePost(id);
            listdata.remove(selected_posts.get(i));
            Toast.makeText(activity, "Posts are deleted successfully", Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
        }
    }

//    public void sendSelectedPosts(List<Post> selected_posts) {
//        postDetailActivity postDetail = new postDetailActivity();
//        for (int i = 0; i < selected_posts.size(); i++) {
//            postDetail.sendEmail(selected_posts.get(i).getImage(),selected_posts.get(i).getMessage());
//        }
//
//    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        public TextView textView;
        CheckBox checkbox;
        View view;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView, PostActivities activity) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.iv_postImage);
            this.textView = itemView.findViewById(R.id.iv_message);
            this.checkbox = itemView.findViewById(R.id.checkbox);
            this.linearLayout = itemView.findViewById(R.id.linear_layout);
            this.view = itemView;
            checkbox.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            activity.makeSelection(view, getAdapterPosition());
        }
    }
}
