package com.example.android.movies.Fragments;

import static com.example.android.movies.Data.Constants.ID;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android.movies.Adapters.ReviewAdapter;
import com.example.android.movies.Data.RetrofitInstance;
import com.example.android.movies.Files.Reviews;
import com.example.android.movies.MainActivity;
import com.example.android.movies.R;
import com.example.android.movies.databinding.ListReviewsVideosBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by XXX on 09-Aug-18.
 */

public class ReviewsFragment extends Fragment {

    public ReviewsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ListReviewsVideosBinding binding = ListReviewsVideosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        assert getActivity() != null;
        binding.reviewsList.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));

        Bundle bundle = getArguments();
        assert bundle != null;
        int id = bundle.getInt(ID);

        String url = "https://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" + MainActivity.API_KEY;

        RetrofitInstance.getMovies().getMovieReviews(url).enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(@NonNull Call<Reviews> call, @NonNull Response<Reviews> response) {

                if (response.body() != null && !response.body().getResults().isEmpty()) {

                    binding.emptyTextViewVideo.setVisibility(View.GONE);
                    ReviewAdapter reviewAdapter = new ReviewAdapter(response.body().getResults());
                    binding.reviewsList.setAdapter(reviewAdapter);
                } else {

                    binding.emptyTextViewVideo.setVisibility(View.VISIBLE);
                    binding.emptyTextViewVideo.setText(getString(R.string.no_reviews));
                }

            }

            @Override
            public void onFailure(@NonNull Call<Reviews> call, @NonNull Throwable t) {

                binding.emptyTextViewVideo.setVisibility(View.VISIBLE);
                binding.emptyTextViewVideo.setText(getString(R.string.no_reviews));
            }
        });

        registerForContextMenu(root);

        return root;
    }

}
