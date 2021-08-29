package com.nick.moviecatalogservice.resources;

import com.nick.moviecatalogservice.models.CatalogItem;
import com.nick.moviecatalogservice.models.Movie;
import com.nick.moviecatalogservice.models.Rating;
import com.nick.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResources {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);

        return ratings.getUserRating().stream().map(rating -> {

            // For each movie ID, call movie info service and get details
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

//                    Movie movie = webClientBuilder.build()
//                            .get()
//                            .uri("http://localhost:8082/movies/" + rating.getMovieId())
//                            .retrieve()
//                            .bodyToMono(Movie.class)
//                            .block();

            // Put all them together
            return new CatalogItem(movie.getName(), "decs", rating.getRating());
        })
        .collect(Collectors.toList());

    }
}
