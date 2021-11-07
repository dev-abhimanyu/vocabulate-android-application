package com.abhimanyu.vocabulate.like;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.abhimanyu.vocabulate.QuizActivity;
import com.abhimanyu.vocabulate.QuizContract;
import com.abhimanyu.vocabulate.QuizContract.*;
import com.abhimanyu.vocabulate.QuizDbHelper;
import com.abhimanyu.vocabulate.R;
import com.abhimanyu.vocabulate.db.VocabulateData;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * Created by abhimanyu
 */
public class LikeButtonView extends FrameLayout implements View.OnClickListener {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private QuizDbHelper dbHelper = new QuizDbHelper(getContext());
    private SQLiteDatabase db = dbHelper.getWritableDatabase();
    public static String LikedWords = "";
    public static int rowId = 0;
    public String LikedWordsExplanation = "";

    @BindView(R.id.ivStar)
    ImageView ivStar;
    @BindView(R.id.vDotsView)
    DotsView vDotsView;
    @BindView(R.id.vCircle)
    CircleView vCircle;

    public boolean isChecked;
    public AnimatorSet animatorSet;

    public LikeButtonView(Context context) {
        super(context);
        init();
    }

    public LikeButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LikeButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LikeButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_like_button, this, true);
        ButterKnife.bind(this);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        isChecked = !isChecked;
        ivStar.setImageResource(isChecked ? R.drawable.ic_star_rate_on : R.drawable.ic_star_rate_off);

        if (animatorSet != null) {
            animatorSet.cancel();
        }

        if (isChecked) {
            ivStar.animate().cancel();
            ivStar.setScaleX(0);
            ivStar.setScaleY(0);
            vCircle.setInnerCircleRadiusProgress(0);
            vCircle.setOuterCircleRadiusProgress(0);
            vDotsView.setCurrentProgress(0);

            animatorSet = new AnimatorSet();

            ObjectAnimator outerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
            outerCircleAnimator.setDuration(250);
            outerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator innerCircleAnimator = ObjectAnimator.ofFloat(vCircle, CircleView.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
            innerCircleAnimator.setDuration(200);
            innerCircleAnimator.setStartDelay(200);
            innerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_Y, 0.2f, 1f);
            starScaleYAnimator.setDuration(350);
            starScaleYAnimator.setStartDelay(250);
            starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(ivStar, ImageView.SCALE_X, 0.2f, 1f);
            starScaleXAnimator.setDuration(350);
            starScaleXAnimator.setStartDelay(250);
            starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(vDotsView, DotsView.DOTS_PROGRESS, 0, 1f);
            dotsAnimator.setDuration(900);
            dotsAnimator.setStartDelay(50);
            dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);

            animatorSet.playTogether(
                    outerCircleAnimator,
                    innerCircleAnimator,
                    starScaleYAnimator,
                    starScaleXAnimator,
                    dotsAnimator
            );

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    vCircle.setInnerCircleRadiusProgress(0);
                    vCircle.setOuterCircleRadiusProgress(0);
                    vDotsView.setCurrentProgress(0);
                    ivStar.setScaleX(1);
                    ivStar.setScaleY(1);
                }
            });

            animatorSet.start();
            int wordExists = 0;
            String[] colsToSelect = {BookmarkTable.WORD};
            String whereClause = BookmarkTable.WORD + " = ?";
            String[] whereSelectionArgs = { LikedWords };
            Cursor cursor1 = db.query(
                    BookmarkTable.TABLE_NAME,
                    colsToSelect,
                    whereClause,
                    whereSelectionArgs,
                    null,
                    null,
                    null
            );
            while(cursor1.moveToNext())
            {
                if (cursor1.getString(cursor1.getColumnIndex(BookmarkTable.WORD)).equals(LikedWords))
                {
                    wordExists = 1;
                    break;
                }
            }
            cursor1.close();
            if(wordExists == 0) {
                ContentValues values = new ContentValues();
                rowId++;
                values.put(BookmarkTable._ID, rowId);
                values.put(BookmarkTable.WORD, LikedWords);
                VocabulateData vdataObj = new VocabulateData();
                LikedWordsExplanation = vdataObj.VocabData().get(LikedWords);
                values.put(BookmarkTable.EXPLANATION, LikedWordsExplanation);
                db.insert(BookmarkTable.TABLE_NAME, null, values);
            }
        }
        else
        {
            // if isChecked is false then, delete the word from Liked Words list
            // Define 'where' part of query.
            String deleteSelection = BookmarkTable.WORD + " LIKE ?";
            // Specify arguments in placeholder order.
            String[] deleteSelectionArgs = { LikedWords };
            // Issue SQL statement.
            db.delete(BookmarkTable.TABLE_NAME, deleteSelection, deleteSelectionArgs);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ivStar.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).setInterpolator(DECCELERATE_INTERPOLATOR);
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
                if (isPressed() != isInside) {
                    setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_UP:
                ivStar.animate().scaleX(1).scaleY(1).setInterpolator(DECCELERATE_INTERPOLATOR);
                if (isPressed()) {
                    performClick();
                    setPressed(false);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                ivStar.animate().scaleX(1).scaleY(1).setInterpolator(DECCELERATE_INTERPOLATOR);
                setPressed(false);
                break;

        }
        return true;
    }

    public boolean isChecked() {
        return isChecked;
    }
}


