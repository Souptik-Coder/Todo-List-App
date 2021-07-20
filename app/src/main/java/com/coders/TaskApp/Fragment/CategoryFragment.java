package com.coders.TaskApp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coders.TaskApp.R;
import com.coders.TaskApp.databinding.FragmentCategoryBinding;


public class CategoryFragment extends Fragment {

    FragmentCategoryBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding=FragmentCategoryBinding.inflate(inflater);

        return binding.getRoot();
    }
}