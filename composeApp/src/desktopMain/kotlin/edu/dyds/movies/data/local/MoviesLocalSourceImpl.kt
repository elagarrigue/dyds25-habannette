package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie

class MoviesLocalSourceImpl : MoviesLocalSource{
    private val cacheMovies = mutableListOf<Movie>()
    override suspend fun getMovies():  MutableList<Movie>{
            return cacheMovies
    }
    override suspend fun isEmpty(): Boolean {
        return cacheMovies.isEmpty()
    }
    override suspend fun saveMovies(movies: List<Movie>) {
        cacheMovies.clear()
        getMovies().addAll(movies)
    }
}