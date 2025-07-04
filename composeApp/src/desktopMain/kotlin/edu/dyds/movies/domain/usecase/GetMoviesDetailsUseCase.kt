package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository


interface GetMoviesDetailsUseCase {
    suspend fun getMovieDetails(title: String): Movie?
}

class GetMoviesDetailsUseCaseImpl(
    private val repository: MoviesRepository
) : GetMoviesDetailsUseCase {

    override suspend fun getMovieDetails(title: String) =
        repository.getMovieDetails(title)

}