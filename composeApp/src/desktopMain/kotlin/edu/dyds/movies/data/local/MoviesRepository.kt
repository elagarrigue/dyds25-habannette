package edu.dyds.movies.data.local
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie

    interface MovieRepository {
        suspend fun getPopularMovies(): List<QualifiedMovie>
        suspend fun getMovieDetails(id: Int): Movie?
    }

