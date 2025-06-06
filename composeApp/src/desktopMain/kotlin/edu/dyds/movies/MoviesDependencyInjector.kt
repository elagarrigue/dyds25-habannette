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
import edu.dyds.movies.presentation.detail.MoviesDetailViewModel
import edu.dyds.movies.presentation.home.MoviesViewModel
import edu.dyds.movies.data.external.ExternalRepository
import edu.dyds.movies.data.local.LocalRepository
import edu.dyds.movies.data.external.ExternalRepositoryImpl
import edu.dyds.movies.data.local.LocalRepositoryImpl
import edu.dyds.movies.domain.usecase.GetMoviesDetailsUseCaseImpl
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCaseImpl

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

    private val localRepository: LocalRepository = LocalRepositoryImpl()
    private val externalRepository: ExternalRepository = ExternalRepositoryImpl(tmdbHttpClient)
    val movieRepository: MoviesRepository = MovieRepositoryImpl(localRepository, externalRepository)

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