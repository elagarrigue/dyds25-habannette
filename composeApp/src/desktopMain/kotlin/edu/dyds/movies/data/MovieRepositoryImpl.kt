package edu.dyds.movies.data

import edu.dyds.movies.domain.entity.RemoteMovie
import edu.dyds.movies.domain.repository.MoviesRepository
import io.ktor.client.HttpClient

class MovieRepositoryImpl (tmdbHttpClient: HttpClient): MoviesRepository{
    override  suspend fun getPopularMovies(): List<RemoteMovie> {
        return emptyList()
    }

    override suspend fun getMovieDetails(id: Int): RemoteMovie {
        TODO("Not yet implemented")
    }
}