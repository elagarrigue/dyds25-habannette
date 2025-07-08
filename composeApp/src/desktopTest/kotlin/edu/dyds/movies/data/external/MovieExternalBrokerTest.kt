package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class MovieExternalBrokerTest {

    private lateinit var movieTMDB: Movie
    private lateinit var movieOMDB: Movie

    @BeforeTest
    fun setup() {
        movieTMDB = Movie(1, "Inception", "Sci-fi TMDB", "2010", "poster1", null, "Inception", "en", 9.0, 8.5)
        movieOMDB = Movie(1, "Inception", "Thriller OMDB", "2010", "poster2", null, "Inception", "en", 7.0, 7.5)
    }

    @Test
    fun `getMovieDetails fusiona detalles de TMDB y OMDB correctamente`() = runTest {
        val tmdbFake = MoviesExternalSourceFake(
            movieDetailsMap = mapOf("Inception" to movieTMDB)
        )
        val omdbFake = MoviesExternalSourceFake(
            movieDetailsMap = mapOf("Inception" to movieOMDB)
        )

        val broker = MovieExternalBroker(tmdbFake, omdbFake)

        val result = broker.getMovieDetails("Inception")!!

        assertEquals("Inception", result.title)
        assertTrue(result.overview.contains("TMDB: Sci-fi TMDB"))
        assertTrue(result.overview.contains("OMDB: Thriller OMDB"))
        assertEquals(8.0, result.voteAverage)  // Promedio entre 8.5 y 6.5
        assertEquals(8.0, result.popularity)   // Promedio entre 9.0 y 7.0
    }

    @Test
    fun `getMovieDetails retorna detalle de TMDB si OMDB no responde`() = runTest {

        val tmdbFake = MoviesExternalSourceFake(
            movieDetailsMap = mapOf("Inception" to movieTMDB)
        )
        val omdbFake = MoviesExternalSourceFake() // OMDB no devuelve nada

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertEquals(movieTMDB.copy(overview = "TMDB: ${movieTMDB.overview}"), result)
    }

    @Test
    fun `getMovieDetails retorna detalle de OMDB si TMDB no responde`() = runTest {

        val tmdbFake = MoviesExternalSourceFake() // TMDB no devuelve nada
        val omdbFake = MoviesExternalSourceFake(
            movieDetailsMap = mapOf("Inception" to movieOMDB)
        )

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertEquals(movieOMDB.copy(overview = "OMDB: ${movieOMDB.overview}"), result)
    }

    @Test
    fun `getMovieDetails retorna null si ambos servicios fallan`() = runTest {
        // Ambos fakes no devuelven nada
        val tmdbFake = MoviesExternalSourceFake()
        val omdbFake = MoviesExternalSourceFake()

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertNull(result)
    }

    @Test
    fun `getMovies delega correctamente a TMDB`() = runTest {
        val movies = listOf(movieTMDB, movieOMDB)
        val tmdbFake = MoviesExternalSourceFake(movies = movies)
        val omdbFake = MoviesExternalSourceFake() // OMDB no se usa

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovies()

        assertEquals(movies, result)
    }
    @Test
    fun `getMovieDetails retorna detalle de OMDB si TMDB lanza excepcion`() = runTest {
        val tmdbFake = MoviesExternalSourceFake(
            exceptionGetMovieDetails = true
        )
        val omdbFake = MoviesExternalSourceFake(
            movieDetailsMap = mapOf("Inception" to movieOMDB)
        )

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertEquals(movieOMDB.copy(overview = "OMDB: ${movieOMDB.overview}"), result)
    }
    @Test
    fun `getMovieDetails retorna detalle de TMDB si OMDB lanza excepcion`() = runTest {
        val tmdbFake = MoviesExternalSourceFake(
            movieDetailsMap = mapOf("Inception" to movieTMDB)
        )
        val omdbFake = MoviesExternalSourceFake(
            exceptionGetMovieDetails = true
        )

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertEquals(movieTMDB.copy(overview = "TMDB: ${movieTMDB.overview}"), result)
    }
    @Test
    fun `getMovieDetails retorna null si ambos servicios lanzan excepcion`() = runTest {
        val tmdbFake = MoviesExternalSourceFake(exceptionGetMovieDetails = true)
        val omdbFake = MoviesExternalSourceFake(exceptionGetMovieDetails = true)

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertNull(result)
    }
    @Test
    fun `getMovies retorna lista vacia si TMDB lanza excepcion`() = runTest {
        val tmdbFake = MoviesExternalSourceFake(exceptionGetMovies = true)
        val omdbFake = MoviesExternalSourceFake() // OMDB no se usa

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovies()

        assertTrue(result.isEmpty())
    }



}
