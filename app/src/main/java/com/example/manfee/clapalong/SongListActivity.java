package com.example.manfee.clapalong;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SongListActivity extends AppCompatActivity {

    ContentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_song_list);
        adapter = new ContentAdapter(SongListActivity.this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SongListActivity.this));

    }


    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView;


        public CustomViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.custom_row, parent, false));
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            Toast.makeText(SongListActivity.this, "Hello", Toast.LENGTH_SHORT).show();

            Intent playSongIntent = new Intent(SongListActivity.this, PlaySongActivity.class);
            SongListActivity.this.startActivity(playSongIntent);
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<CustomViewHolder> {

        private final String[] mSongLists;
        private final Drawable[] mSongPictures;


        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            mSongLists = resources.getStringArray(R.array.song_lists);
            TypedArray a = resources.obtainTypedArray(R.array.song_pictures);
            mSongPictures = new Drawable[a.length()];
            for (int i = 0; i < mSongPictures.length; i++) {
                mSongPictures[i] = a.getDrawable(i);
            }
            a.recycle();

        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CustomViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            holder.textView.setText(mSongLists[position % mSongLists.length]);
            holder.imageView.setImageDrawable(mSongPictures[position % mSongPictures.length]);
//            Glide.with(getApplicationContext())
//                    .load(mSongPictures[position % mSongPictures.length].getImageResource())
//                     .placeholder(holder)
//                    .into(imageView);


        }

        @Override
        public int getItemCount() {
            return mSongLists.length;
        }
    }


}


