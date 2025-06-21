package domain

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCaseImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals


class GetPopularMoviesUseCaseTest {
    private lateinit var repository: MoviesRepository
    private lateinit var useCase: GetPopularMoviesUseCase


    @Test
    fun `getPopularMovies obtiene Movies y las ordena de forma descendente`() = runTest {
        //Arrange
        val m1 = Movie(
            id = 1, title = "A", "algo", "11/6/2025",
            "algo", "algo", "PeliculaA", "Ingles", 2.5, 8.0
        )
        val m2 = Movie(
            id = 2, title = "B", "algo", "11/6/2025",
            "algo", "algo", "PeliculaB", "Ingles", 2.5, 5.0
        )
        val m3 = Movie(
            id = 3, title = "C", "algo", "11/6/2025",
            "algo", "algo", "PeliculaC", "Ingles", 2.5, 7.0
        )

        repository = object : MoviesRepository {
            override suspend fun getPopularMovies(): List<Movie> =
                listOf(m1, m2, m3)

            override suspend fun getMovieDetails(id: Int): Movie? = null
        }
        useCase = GetPopularMoviesUseCaseImpl(repository)

        //Act
        val resultado = useCase.getPopularMovies()

        //Assert
        assertEquals(listOf(1, 3, 2), resultado.map { it.movie.id })
        assertEquals(true, resultado[0].isGoodMovie)
        assertEquals(true, resultado[1].isGoodMovie)
        assertEquals(false, resultado[2].isGoodMovie)

    }

    @Test
    fun `getPopularMovies con lista vacia retorna vacio`() = runTest {
        repository = object : MoviesRepository {
            override suspend fun getPopularMovies(): List<Movie> = emptyList()
            override suspend fun getMovieDetails(id: Int): Movie? = null
        }
        useCase = GetPopularMoviesUseCaseImpl(repository)

        val resultado = useCase.getPopularMovies()
        assertEquals(resultado.isEmpty(), true)
    }
}