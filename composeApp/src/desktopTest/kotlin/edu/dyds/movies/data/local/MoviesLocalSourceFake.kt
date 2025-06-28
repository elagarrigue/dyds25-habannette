package edu.dyds.movies.data.local

import edu.dyds.movies.data.MoviesLocalSource
import edu.dyds.movies.domain.entity.Movie

class MoviesLocalSourceFake : MoviesLocalSource {
    private val cacheMovies = mutableListOf<Movie>()


    override suspend fun getMovies(): MutableList<Movie> {
        return cacheMovies.toMutableList()
    }


    override suspend fun isEmpty(): Boolean {
        return cacheMovies.isEmpty()
    }

    override suspend fun saveMovies(movies: List<Movie>) {
        cacheMovies.clear()
        cacheMovies.addAll(movies)
    }
}