package com.example.sandipghosh.music;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by sandipghosh on 20/03/17.
 */

public class AlbumsAdapter1 extends RecyclerView.Adapter<AlbumsAdapter1.MyViewHolder> {

    private Context mContext, context;
    private List<Album1> album1List1;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            title = (TextView) itemView.findViewById(R.id.title);
            imageView = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }

    public AlbumsAdapter1(Context mContext, List<Album1> album1List1) {
        this.mContext = mContext;
        this.album1List1 = album1List1;
    }


    @Override
    public AlbumsAdapter1.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlbumsAdapter1.MyViewHolder holder, int position) {

        Album1 album1 = album1List1.get(position);
        holder.title.setText(album1.getName());

        Glide.with(mContext).load(album1.getThumb()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return album1List1.size();
    }
}
