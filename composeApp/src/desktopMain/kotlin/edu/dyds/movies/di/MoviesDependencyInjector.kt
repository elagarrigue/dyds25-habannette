package edu.dyds.movies.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.dyds.movies.domain.repository.MoviesRepository
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import edu.dyds.movies.data.MovieRepositoryImpl
import edu.dyds.movies.domain.usecase.GetMoviesDetailsUseCase
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.presentation.detail.MoviesDetailViewModel
import edu.dyds.movies.presentation.home.MoviesViewModel
import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.data.MoviesLocalSource
import edu.dyds.movies.data.external.MovieExternalBroker
import edu.dyds.movies.data.external.omdb.OMDBMoviesExternalSource
import edu.dyds.movies.data.external.tmdb.TMDBMoviesExternalSource
import edu.dyds.movies.data.MoviesLocalSourceImpl
import edu.dyds.movies.data.external.ExternalMoviesSourceGetDetail
import edu.dyds.movies.domain.usecase.GetMoviesDetailsUseCaseImpl
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCaseImpl

private const val TMDB_API_KEY = "d18da1b5da16397619c688b0263cd281"
private const val OMDB_API_KEY = "a96e7f78"

object MoviesDependencyInjector {

    private val tmdbHttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.themoviedb.org"
                    parameters.append("api_key", TMDB_API_KEY)
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }

    private val omdbHttpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "www.omdbapi.org"
                    parameters.append("apikey", OMDB_API_KEY)
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }
    private val tmdbExternalSource: MoviesExternalSource = TMDBMoviesExternalSource(tmdbHttpClient)
    private val omdbExternalSource: ExternalMoviesSourceGetDetail = OMDBMoviesExternalSource(omdbHttpClient)
    private val movieExternalBroker= MovieExternalBroker(
        tmdbExternalSource,
        omdbExternalSource
    )
    private val localRepository: MoviesLocalSource = MoviesLocalSourceImpl()
    val movieRepository: MoviesRepository = MovieRepositoryImpl(localRepository, movieExternalBroker)

    private val getPopularMoviesUseCase: GetPopularMoviesUseCase =
        GetPopularMoviesUseCaseImpl(movieRepository)

    private val getMoviesDetailsUseCase: GetMoviesDetailsUseCase =
        GetMoviesDetailsUseCaseImpl(movieRepository)


    @Composable
    fun getMoviesViewModel(): MoviesViewModel {
        return viewModel { MoviesViewModel(getPopularMoviesUseCase) }
    }

    @Composable
    fun getMoviesDetailViewModel(): MoviesDetailViewModel {
        return viewModel {MoviesDetailViewModel(getMoviesDetailsUseCase)}
    }
}