package com.abhimanyu.vocabulate.recycler_swipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.abhimanyu.vocabulate.QuizContract.*;
import com.abhimanyu.vocabulate.QuizContract;
import com.abhimanyu.vocabulate.QuizDbHelper;
import com.abhimanyu.vocabulate.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static com.abhimanyu.vocabulate.like.LikeButtonView.LikedWords;
/**
 * Created by abhimanyu
 */
public class LikeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerViewAdapter mAdapter;
    ArrayList<String> stringArrayList = new ArrayList<>();
    CoordinatorLayout coordinatorLayout;

    private QuizDbHelper dbHelper;
    private SQLiteDatabase db;
    private SQLiteDatabase db2;
    public static Typeface msgFont;
    private TextView noBookmarkView;

//    private String dbWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        //setting notification bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.splashBackground));

        recyclerView = findViewById(R.id.recyclerView);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        dbHelper = new QuizDbHelper(this);
        db = dbHelper.getReadableDatabase();
        db2 = dbHelper.getWritableDatabase();

        populateRecyclerView();
        enableSwipeToDeleteAndUndo();

    }
    private void populateRecyclerView() {
        Cursor c = db.rawQuery("SELECT * FROM " + BookmarkTable.TABLE_NAME + " ORDER BY "+ BookmarkTable.WORD, null);
        while(c.moveToNext())
        {
//            dbWord = c.getString(c.getColumnIndex(BookmarkTable.WORD));
            stringArrayList.add(c.getString(c.getColumnIndex(BookmarkTable.WORD))+": "+c.getString(c.getColumnIndex(BookmarkTable.EXPLANATION)));
        }
        c.close();
//        stringArrayList.add("Item 1");
//        stringArrayList.add("Item 2");
//        stringArrayList.add("Item 3");
//        stringArrayList.add("Item 4");
//        stringArrayList.add("Item 5");
//        stringArrayList.add("Item 6");
//        stringArrayList.add("Item 7");
//        stringArrayList.add("Item 8");
//        stringArrayList.add("Item 9");
//        stringArrayList.add("Item 10");

        mAdapter = new RecyclerViewAdapter(stringArrayList);
        recyclerView.setAdapter(mAdapter);
        msgFont = Typeface.createFromAsset(getAssets(), "fonts/fredoka_one_regular.ttf");
        noBookmarkView = findViewById(R.id.emptyBookmarkMsg);
        noBookmarkView.setTypeface(msgFont);
        //setting heading font
        TextView bookmarkHeadingView= findViewById(R.id.bookmark_heading);
        ImageView bookmarkHeadingLine = findViewById(R.id.bookmark_heading_underline);
        Typeface bmarkHeadFace = Typeface.createFromAsset(getAssets(), "fonts/schoolbell_regular.ttf");
        bookmarkHeadingView.setTypeface(bmarkHeadFace);
        if(stringArrayList.isEmpty())
        {
            noBookmarkView.setVisibility(View.VISIBLE);
            bookmarkHeadingView.setVisibility(View.GONE);
            bookmarkHeadingLine.setVisibility(View.GONE);
        }
        else
            noBookmarkView.setVisibility(View.GONE);



    }
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final String item = mAdapter.getData().get(position);

                mAdapter.removeItem(position);

                String deleteSelection = BookmarkTable.WORD + " LIKE ?";
                // Specify arguments in placeholder order.
                String itemWord = item.split(": ")[0];
                String itemExp = item.split(": ")[1];
                String[] deleteSelectionArgs = {itemWord};
                // Issue SQL statement.
                db.delete(BookmarkTable.TABLE_NAME, deleteSelection, deleteSelectionArgs);

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "\""+itemWord+"\""+" was removed.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //inserting the word and explanation back into the table
                        ContentValues values = new ContentValues();
                        values.put(BookmarkTable.WORD, itemWord);
                        values.put(BookmarkTable.EXPLANATION, itemExp);
                        db.insert(BookmarkTable.TABLE_NAME, null, values);
                        mAdapter.restoreItem(item, position);
                        recyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

}
