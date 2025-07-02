package edu.dyds.movies.domain

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.domain.usecase.GetMoviesDetailsUseCase
import edu.dyds.movies.domain.usecase.GetMoviesDetailsUseCaseImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class GetMoviesDetailsUseCaseTest {
    private lateinit var repository: MoviesRepository
    private lateinit var useCase: GetMoviesDetailsUseCase

    @BeforeTest
    fun setup() {
        val m1 = Movie(
            id = 1, title = "TitleA", "algo", "11/6/2025",
            "algo", "algo", "PeliculaA", "Ingles", 2.5, 8.0
        )
        repository = object : MoviesRepository {
            override suspend fun getPopularMovies(): List<Movie> = emptyList()
            override suspend fun getMovieDetails(title: String): Movie? {
                if (title == m1.title)
                    return m1
                else
                    return null
            }
        }
        useCase = GetMoviesDetailsUseCaseImpl(repository)
    }

    @Test
    fun `getMoviesDetails retorna Movie cuando existe`() = runTest {
        //Arrange se genera en setup
        //Act
        val resultado = useCase.getMovieDetails("TitleA")

        //Assert
        assertEquals("TitleA", resultado?.title)
    }

    @Test
    fun `getMoviesDetails retorna null cuando no existe`() = runTest {
        //Arrange se genera en setup
        //Act
        val resultado = useCase.getMovieDetails("TitleB")

        //Assert
        assertEquals(null, resultado)
    }
}