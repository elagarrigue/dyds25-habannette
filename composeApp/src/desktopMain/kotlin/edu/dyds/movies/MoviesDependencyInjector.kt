package edu.dyds.movies

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

private const val API_KEY = "d18da1b5da16397619c688b0263cd281"

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
                    parameters.append("api_key", API_KEY)
                }
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
            }
        }

    private val movieRepository: MoviesRepository by lazy{
        MovieRepositoryImpl(tmdbHttpClient)
    }

    private val getPopularMoviesUseCase: GetPopularMoviesUseCase by lazy{
        GetPopularMoviesUseCase(movieRepository)
    }

    private val getMoviesDetailsUseCase: GetMoviesDetailsUseCase by lazy{
        GetMoviesDetailsUseCase(movieRepository)
    }


    @Composable
    fun getMoviesViewModel(): MoviesViewModel {
        return viewModel { MoviesViewModel(getPopularMoviesUseCase, getMoviesDetailsUseCase) }
    }
}