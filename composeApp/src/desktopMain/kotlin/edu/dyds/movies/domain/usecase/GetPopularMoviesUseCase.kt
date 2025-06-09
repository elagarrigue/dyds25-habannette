package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
private const val MIN_VOTE_AVERAGE = 6.0

interface GetPopularMoviesUseCase {
    suspend fun getPopularMovies(): List<QualifiedMovie>

}

class GetPopularMoviesUseCaseImpl (
    private val repository: MoviesRepository
): GetPopularMoviesUseCase{


    override  suspend fun getPopularMovies(): List<QualifiedMovie> {
        return repository.getPopularMovies().mapToQualifiedMovie()
    }

    private fun List<Movie>.mapToQualifiedMovie(): List<QualifiedMovie> {
        return this.map { movie ->
                QualifiedMovie(
                    movie       = movie,
                    isGoodMovie = movie.voteAverage >= MIN_VOTE_AVERAGE
                )
            }
    }


}