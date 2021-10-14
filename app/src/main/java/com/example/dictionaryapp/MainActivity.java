package com.example.dictionaryapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.dictionaryapp.Adapters.MeaningAdapter;
import com.example.dictionaryapp.Adapters.PhoneticsAdapter;
import com.example.dictionaryapp.Models.APIResponse;
import com.example.dictionaryapp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    ProgressDialog dialog;
    PhoneticsAdapter phoneticsAdapter;
    MeaningAdapter meaningAdapter;


    private final OnFetchDataListener listener = new OnFetchDataListener() {
        @Override
        public void onFetchData(APIResponse apiResponse, String message) {
            dialog.dismiss();
            if (apiResponse == null) {
                Toast.makeText(MainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                return;
            }
            showData(apiResponse);
        }

        @Override
        public void onError(String message) {
            dialog.dismiss();
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new ProgressDialog(this);

        dialog.setTitle("Loading...");
        dialog.show();
        RequestManager manager = new RequestManager(MainActivity.this);
        manager.getWordMeaning(listener, "hello");

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dialog.setTitle("Fetching response for " + query);
                dialog.show();
                RequestManager manager = new RequestManager(MainActivity.this);
                manager.getWordMeaning(listener, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    private void showData(APIResponse apiResponse) {

        binding.textViewWord.setText("Word: " + apiResponse.getWord());
        binding.recyclerPhonetics.setHasFixedSize(true);
        binding.recyclerPhonetics.setLayoutManager(new GridLayoutManager(this, 1));
        phoneticsAdapter = new PhoneticsAdapter(this, apiResponse.getPhonetics());
        binding.recyclerPhonetics.setAdapter(phoneticsAdapter);

        binding.recyclerMeanings.setHasFixedSize(true);
        binding.recyclerMeanings.setLayoutManager(new GridLayoutManager(this, 1));
        meaningAdapter = new MeaningAdapter(this, apiResponse.getMeanings());
        binding.recyclerMeanings.setAdapter(meaningAdapter);
    }
}