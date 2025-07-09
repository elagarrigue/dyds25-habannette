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
        val tmdbFake = ExternalMovieSourceGetDetailsFake(
            movie = movieTMDB
        )
        val omdbFake = ExternalMovieSourceGetDetailsFake(
            movie = movieOMDB
        )

        val broker = MovieExternalBroker(tmdbFake, omdbFake)

        val result = broker.getMovieDetails("Inception")!!

        val expected = Movie(
            id = movieTMDB.id,
            title = movieTMDB.title,
            overview = "TMDB: ${movieTMDB.overview}\n\nOMDB: ${movieOMDB.overview}",
            releaseDate = movieTMDB.releaseDate,
            poster = movieTMDB.poster,
            backdrop = movieTMDB.backdrop,
            originalTitle = movieTMDB.originalTitle,
            originalLanguage = movieTMDB.originalLanguage,
            popularity = (movieTMDB.popularity + movieOMDB.popularity) / 2.0,
            voteAverage = (movieTMDB.voteAverage + movieOMDB.voteAverage) / 2.0
        )

        assertEquals(expected, result)
    }

    @Test
    fun `getMovieDetails retorna detalle de TMDB si OMDB no responde`() = runTest {

        val tmdbFake = ExternalMovieSourceGetDetailsFake(
            movie = movieTMDB
        )
        val omdbFake = ExternalMovieSourceGetDetailsFake(null) // OMDB no devuelve nada

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertEquals(movieTMDB.copy(overview = "TMDB: ${movieTMDB.overview}"), result)
    }

    @Test
    fun `getMovieDetails retorna detalle de OMDB si TMDB no responde`() = runTest {

        val tmdbFake = ExternalMovieSourceGetDetailsFake(null) // TMDB no devuelve nada
        val omdbFake = ExternalMovieSourceGetDetailsFake(
            movie = movieOMDB
        )

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertEquals(movieOMDB.copy(overview = "OMDB: ${movieOMDB.overview}"), result)
    }

    @Test
    fun `getMovieDetails retorna null si ambos servicios fallan`() = runTest {
        // Ambos fakes no devuelven nada
        val tmdbFake = ExternalMovieSourceGetDetailsFake(null)
        val omdbFake = ExternalMovieSourceGetDetailsFake(null)

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertNull(result)
    }

    @Test
    fun `getMovieDetails retorna detalle de OMDB si TMDB lanza excepcion`() = runTest {
        val tmdbFake = ExternalMovieSourceGetDetailsFake(
            movie=null,
            exceptionDetails = true
        )
        val omdbFake = ExternalMovieSourceGetDetailsFake(
            movie=movieOMDB
        )

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertEquals(movieOMDB.copy(overview = "OMDB: ${movieOMDB.overview}"), result)

    }
    @Test
    fun `getMovieDetails retorna detalle de TMDB si OMDB lanza excepcion`() = runTest {
        val tmdbFake = ExternalMovieSourceGetDetailsFake(
            movie = movieTMDB
        )
        val omdbFake = ExternalMovieSourceGetDetailsFake(
            movie=null,
            exceptionDetails = true
        )

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertEquals(movieTMDB.copy(overview = "TMDB: ${movieTMDB.overview}"), result)
    }
    @Test
    fun `getMovieDetails retorna null si ambos servicios lanzan excepcion`() = runTest {
        val tmdbFake = ExternalMovieSourceGetDetailsFake(movie=null,exceptionDetails = true)
        val omdbFake = ExternalMovieSourceGetDetailsFake(movie=null,exceptionDetails = true)

        val broker = MovieExternalBroker(tmdbFake, omdbFake)
        val result = broker.getMovieDetails("Inception")

        assertNull(result)
    }



}
