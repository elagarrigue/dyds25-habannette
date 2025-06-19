package data.local

import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.domain.entity.Movie

class MoviesExternalSourceFake(
    private val movies: List<Movie> = emptyList(),
    private val movieDetailsMap: Map<Int, Movie> = emptyMap()
) : MoviesExternalSource {

    override suspend fun getMovies(): List<Movie> {
        return movies
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return movieDetailsMap[id]
    }
}

