package com.example.chatapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder>{
    private List<Post> listdata;
    private actionOnPosts action_interface;
    public PostRecyclerAdapter (List<Post> listdata1, actionOnPosts interface_action) {
        this.listdata = listdata1;
        this.action_interface = interface_action;
    }
    @NonNull
    @Override
    public PostRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item_for_posts, parent, false);
        PostRecyclerAdapter.ViewHolder viewHolder = new PostRecyclerAdapter.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostRecyclerAdapter.ViewHolder holder, int position) {
        Post post = listdata.get(position);
        Log.e("Post Array", String.valueOf(listdata.size()));
        holder.textView.append(listdata.get(position).getMessage());
        holder.imageView.setImageBitmap(listdata.get(position).getImage());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            action_interface.showDetail(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public LinearLayout linearLayout;
        public ViewHolder (View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.iv_postImage);
            this.textView = itemView.findViewById(R.id.iv_message);
            this.linearLayout = itemView.findViewById(R.id.linear_layout);
        }
    }
}
