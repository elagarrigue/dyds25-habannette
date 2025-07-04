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
    fun `getMovieDetails retorna pelicula fusionada si TMDB y OMDB responden`() = runTest {
        val broker = MovieExternalBrokerFake(
            tmdbDetailsMap = mapOf("Inception" to movieTMDB),
            omdbDetailsMap = mapOf("Inception" to movieOMDB)
        )

        val result = broker.getMovieDetails("Inception")!!

        assertEquals("Inception", result.title)
        assertTrue(result.overview.contains("TMDB: Sci-fi TMDB"))
        assertTrue(result.overview.contains("OMDB: Thriller OMDB"))
        assertEquals(8.0, result.voteAverage)      // promedio de 8.5 y 6.5
        assertEquals(8.0, result.popularity)       // promedio de 9.0 y 7.0
    }

    @Test
    fun `getMovieDetails retorna TMDB si OMDB no responde`() = runTest {
        val broker = MovieExternalBrokerFake(
            tmdbDetailsMap = mapOf("Inception" to movieTMDB),
            omdbDetailsMap = emptyMap()
        )

        val result = broker.getMovieDetails("Inception")!!

        assertTrue(result.overview.startsWith("TMDB: "))
        assertEquals(movieTMDB.title, result.title)
    }

    @Test
    fun `getMovieDetails retorna OMDB si TMDB no responde`() = runTest {
        val broker = MovieExternalBrokerFake(
            tmdbDetailsMap = emptyMap(),
            omdbDetailsMap = mapOf("Inception" to movieOMDB)
        )

        val result = broker.getMovieDetails("Inception")!!

        assertTrue(result.overview.startsWith("OMDB: "))
        assertEquals(movieOMDB.title, result.title)
    }

    @Test
    fun `getMovieDetails retorna null si ninguna fuente responde`() = runTest {
        val broker = MovieExternalBrokerFake(
            tmdbDetailsMap = emptyMap(),
            omdbDetailsMap = emptyMap()
        )

        val result = broker.getMovieDetails("Inception")

        assertNull(result)
    }

    @Test
    fun `getMovies delega correctamente a TMDB`() = runTest {
        val movies = listOf(movieTMDB, movieOMDB)
        val broker = MovieExternalBrokerFake(tmdbMovies = movies)

        val result = broker.getMovies()

        assertEquals(movies, result)
    }
}
