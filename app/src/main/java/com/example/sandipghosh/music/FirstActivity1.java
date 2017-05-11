package com.example.sandipghosh.music;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.bumptech.glide.load.engine.Resource;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandipghosh on 20/03/17.
 */

public class FirstActivity1 extends AppCompatActivity {

    private List<Album1> album1;
    private AlbumsAdapter1 albumsAdapter1;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        album1 = new ArrayList<>();
        albumsAdapter1 = new AlbumsAdapter1(this,album1);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dptopx(10), true));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(albumsAdapter1);

        prepareItems();
    }

    private void prepareItems() {
        int[] covers = new int[] {
                R.drawable.english,
                R.drawable.bengali1
        };

        Album1 a = new Album1("English",covers[0]);
        album1.add(a);

        a = new Album1("Bengali",covers[1]);
        album1.add(a);

        albumsAdapter1.notifyDataSetChanged();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private  int spanCount;
        private  int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration (int spanCount, int spacing, boolean includeEdge) {
            this.spacing = spacing;
            this.includeEdge = includeEdge;
            this.spanCount = spanCount;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            int position = parent.getChildAdapterPosition(view);
            int colum = position % spanCount;
        }
    }


    private int dptopx(int dp){
        Resources r = getResources();

        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp, r.getDisplayMetrics()));
    }
}
