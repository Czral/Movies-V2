package com.example.android.movies.Fragments;

import static com.example.android.movies.Data.Constants.ID;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.android.movies.Data.RetrofitInstance;
import com.example.android.movies.Files.MovieDetails;
import com.example.android.movies.MainActivity;
import com.example.android.movies.R;
import com.example.android.movies.databinding.ActivityDetailsFragmentBinding;
import com.hsalf.smilerating.BaseRating;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by XXX on 09-Aug-18.
 */

public class DetailsFragment extends Fragment {

    private ActivityDetailsFragmentBinding binding;

    public DetailsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailsFragmentBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        assert bundle != null;
        int id = bundle.getInt(ID);

        String url = "movie/" + id + "?api_key=" + MainActivity.API_KEY;

        RetrofitInstance.getMovies().getMovieDetails(url).enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetails> call, @NonNull Response<MovieDetails> response) {

                if (response.body() != null) {

                    MovieDetails details = response.body();

                    setSmileyRating(response.body().getVoteAverage());
                    assert binding.ratingText != null;
                    binding.ratingText.setText(String.valueOf(details.getVoteAverage()));
                    assert binding.movieOverview != null;
                    binding.movieOverview.setText(details.getOverview());
                    binding.movieOverview.setMovementMethod(new ScrollingMovementMethod());
                    assert binding.releaseDate != null;
                    binding.releaseDate.setText(getString(R.string.release_date, parseDate(details.getReleaseDate())));
                    assert binding.movieTitle != null;
                    binding.movieTitle.setVisibility(View.GONE);

                    Picasso.get().load("https://image.tmdb.org/t/p/w500/" + details.getPosterPath()).
                            error(R.drawable.no_poster).
                            into(binding.moviePoster);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, @NonNull Throwable t) {

            }
        });

        registerForContextMenu(binding.getRoot());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    private String parseDate(String dateString) {

        String s = "N/A";

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateString, Locale.getDefault());
        try {

            Date date = dateFormat.parse(dateString);
            assert date != null;
            s = dateFormat.format(date);
        } catch (ParseException e) {

            Log.e(getClass().getSimpleName(), e.toString());
        }
        return s;
    }

    private void setSmileyRating(double rating) {

        assert binding.smileRating != null;
        if (rating == 0) {

            binding.smileRating.setRating(BaseRating.NONE, false);
            return;
        }

        double voteRating = rating / 2;

        if (voteRating < 1) {

            binding.smileRating.setRating(BaseRating.TERRIBLE, true);
        } else if (voteRating < 2) {

            binding.smileRating.setRating(BaseRating.BAD, true);
        } else if (voteRating < 3) {

            binding.smileRating.setRating(BaseRating.OKAY, true);
        } else if (voteRating < 4) {

            binding.smileRating.setRating(BaseRating.GOOD, true);
        } else {

            binding.smileRating.setRating(BaseRating.GREAT, true);
        }
    }

}

