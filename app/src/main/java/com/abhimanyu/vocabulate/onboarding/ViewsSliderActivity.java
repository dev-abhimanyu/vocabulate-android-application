package com.abhimanyu.vocabulate.onboarding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.abhimanyu.vocabulate.R;
import com.abhimanyu.vocabulate.StartingScreenActivity;
import com.abhimanyu.vocabulate.databinding.ActivityViewsSliderBinding;
/**
 * Created by abhimanyu
 */
public class ViewsSliderActivity extends AppCompatActivity {
    private ViewsSliderAdapter mAdapter;
    private TextView[] dots;
    private int[] layouts;
    private ActivityViewsSliderBinding binding;
    private PrefManager prefManager;
    private int slideCounter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting the status bar color
        // getWindow() is a method of Activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.bg_screen1));
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if(!prefManager.isFirstTimeLaunch()){
            launchHomeScreen();
            finish();
        }


        // Making notification bar transparent
//        if(Build.VERSION.SDK_INT >= 21){
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
        binding = ActivityViewsSliderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

    }

    private void init() {
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.slide_one,
                R.layout.slide_two,
                R.layout.slide_three,
                R.layout.slide_four};

        mAdapter = new ViewsSliderAdapter();
        binding.viewPager.setAdapter(mAdapter);
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback);

        binding.btnSkip.setOnClickListener(v -> launchHomeScreen());

        binding.btnNext.setOnClickListener(v -> {
            // checking for last page
            // if last page home screen will be launched
            int current = getItem(+1);
            if (current < layouts.length) {
                // move to next screen
                binding.viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });
        applyTransformation();
//        binding.iconMore.setOnClickListener(view -> {
//
//            showMenu(view);
//        });

        // adding bottom dots
        addBottomDots(0);
    }

    /*
     * Adds bottom dots indicator
     * */
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        binding.layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            binding.layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return binding.viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        Intent startIntent = new Intent(ViewsSliderActivity.this, StartingScreenActivity.class);
        startActivity(startIntent);
//        Toast.makeText(this, R.string.slides_ended, Toast.LENGTH_LONG).show();
        finish();
    }
    private void applyTransformation()
    {
        binding.viewPager.setPageTransformer(new FadeOutTransformation());
        binding.viewPager.setCurrentItem(0);
        binding.viewPager.getAdapter().notifyDataSetChanged();
    }
//    private void showMenu(View view) {
//        PopupMenu popup = new PopupMenu(this, view);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.pager_transformers, popup.getMenu());
//        popup.setOnMenuItemClickListener(item -> {
//            if (item.getItemId() == R.id.action_orientation) {
//                binding.viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
//            } else {
//                binding.viewPager.setPageTransformer(Utils.getTransformer(item.getItemId()));
//                binding.viewPager.setCurrentItem(0);
//                binding.viewPager.getAdapter().notifyDataSetChanged();
//            }
//            return false;
//        });
//        popup.show();
//    }

    /*
     * ViewPager page change listener
     */
    ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            addBottomDots(position);
            Typeface lowerBtn_font = Typeface.createFromAsset(getAssets(), "fonts/pangolin_regular.ttf");
            binding.btnNext.setTypeface(lowerBtn_font);
            binding.btnSkip.setTypeface(lowerBtn_font);
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                binding.btnNext.setText(getString(R.string.start));
                binding.btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                binding.btnNext.setText(getString(R.string.next));
                binding.btnSkip.setVisibility(View.VISIBLE);
            }
        }
    };

    public class ViewsSliderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ViewsSliderAdapter() {
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(viewType, parent, false);
            //setting fragment fonts
            Typeface head_font = Typeface.createFromAsset(getAssets(), "fonts/bangers_regular.ttf");
            Typeface subhead_font = Typeface.createFromAsset(getAssets(), "fonts/pangolin_regular.ttf");
            TextView slideheadView;
            TextView slidesubheadView;
            slideCounter++;
            if (slideCounter==1) {
                slideheadView = view.findViewById(R.id.slide1head);
                slidesubheadView = view.findViewById(R.id.slide1subhead);
                slidesubheadView.setTypeface(subhead_font);
                slideheadView.setTypeface(head_font);
            }
            else if (slideCounter==2) {
                slideheadView = view.findViewById(R.id.slide2head);
                slidesubheadView = view.findViewById(R.id.slide2subhead);
                slidesubheadView.setTypeface(subhead_font);
                slideheadView.setTypeface(head_font);
            }
            else if (slideCounter==3) {
                slideheadView = view.findViewById(R.id.slide3head);
                slidesubheadView = view.findViewById(R.id.slide3subhead);
                slidesubheadView.setTypeface(subhead_font);
                slideheadView.setTypeface(head_font);
            }
            else if (slideCounter==4) {
                slideheadView = view.findViewById(R.id.slide4head);
                slidesubheadView = view.findViewById(R.id.slide4subhead);
                slidesubheadView.setTypeface(subhead_font);
                slideheadView.setTypeface(head_font);
            }

            return new SliderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemViewType(int position) {
            return layouts[position];
        }

        @Override
        public int getItemCount() {
            return layouts.length;
        }

        public class SliderViewHolder extends RecyclerView.ViewHolder {
            public TextView title, year, genre;

            public SliderViewHolder(View view) {
                super(view);
            }
        }
    }
}

