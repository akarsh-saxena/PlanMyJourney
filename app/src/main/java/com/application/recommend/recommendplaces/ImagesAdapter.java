package com.application.recommend.recommendplaces;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Akarsh on 16-01-2018.
 */

class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.Items> {

    List<ImagesModel> imagesModels;
    Context context;

    public ImagesAdapter(List<ImagesModel> imagesModels, Context context) {
        this.imagesModels = imagesModels;
        this.context = context;
    }

    @Override
    public ImagesAdapter.Items onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.images_item, parent, false);
        return new Items(view);
    }

    @Override
    public void onBindViewHolder(final ImagesAdapter.Items holder, int position) {
        final String prefix = imagesModels.get(position).getPrefix();
        final String suffix = imagesModels.get(position).getSuffix();

        String url = prefix+"500x500"+imagesModels.get(position).getSuffix();
        Picasso.with(context).load(url).into(holder.imageView);
        holder.imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String link = prefix+imagesModels.get(holder.getAdapterPosition()).getWidth()+"x"+imagesModels.get(holder.getAdapterPosition()).getHeight()+suffix;
                        Intent intent = new Intent(context, ImageActivity.class);
                        intent.putExtra("imageURL", link);
                        context.startActivity(intent);
                    }
                }
        );

    }

    @Override
    public int getItemCount() {
        return imagesModels.size();
    }

    public class Items extends RecyclerView.ViewHolder {

        ImageView imageView;

        public Items(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
