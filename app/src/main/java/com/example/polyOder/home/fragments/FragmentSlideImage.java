package com.example.polyOder.home.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.polyOder.R;
import com.example.polyOder.base.BaseFragment;
import com.example.polyOder.databinding.FragmentSlideImageBinding;
import com.example.polyOder.home.adapter.SliderImageAdapter;
import com.example.polyOder.model.PhotosSlider;

import java.util.ArrayList;


public class FragmentSlideImage extends BaseFragment {
    private FragmentSlideImageBinding binding;
    ArrayList<PhotosSlider> listPhotos;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int currentItems = binding.sliderImg.getCurrentItem();
            if(currentItems == listPhotos.size() - 1){
                binding.sliderImg.setCurrentItem(0);
            }else {
                binding.sliderImg.setCurrentItem(currentItems + 1);
            }
        }
    };


    public FragmentSlideImage() {
        // Required empty public constructor
    }

    public static FragmentSlideImage newInstance() {
        FragmentSlideImage fragment = new FragmentSlideImage();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSlideImageBinding.inflate(inflater,container,false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();

    }

    @Override
    public void loadData() {
        binding.sliderImg.setOffscreenPageLimit(3);
        binding.sliderImg.setClipToPadding(false);
        binding.sliderImg.setClipChildren(false);
        listPhotos = getListPhotos();
        SliderImageAdapter photoAdapter = new SliderImageAdapter(listPhotos);
        binding.sliderImg.setAdapter(photoAdapter);

        binding.sliderImg.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable,2500);
            }
        });
    }

    @Override
    public void listening() {

    }

    @Override
    public void initObSever() {

    }

    @Override
    public void initView() {

    }
    public ArrayList<PhotosSlider> getListPhotos (){
        ArrayList<PhotosSlider> listPhotos = new ArrayList<>();
        listPhotos.add(new PhotosSlider(R.drawable.img_slider_1));
        listPhotos.add(new PhotosSlider(R.drawable.img_slider_2));
        listPhotos.add(new PhotosSlider(R.drawable.img_slider_3));
        listPhotos.add(new PhotosSlider(R.drawable.img_slider_4));
        return listPhotos;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable,2500);
    }

}