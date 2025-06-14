package edu.dyds.movies.data.local


import edu.dyds.movies.domain.entity.Movie

interface MoviesLocalSource {
    suspend fun getMovies():  MutableList<Movie>
    suspend fun isEmpty():Boolean
    suspend fun saveMovies(movies: List<Movie>)
}