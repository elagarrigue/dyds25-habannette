package edu.dyds.movies.data.local

import edu.dyds.movies.data.MoviesLocalSource
import edu.dyds.movies.data.MoviesLocalSourceImpl
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MoviesLocalSourceTest {

    private lateinit var localSource: MoviesLocalSource

    @BeforeTest
    fun setUp() {
        localSource = MoviesLocalSourceImpl()
    }

    @Test
    fun `cache inicial vacio`() = runTest {
        assertTrue(localSource.isEmpty())
        assertEquals(0, localSource.getMovies().size)
    }

    @Test
    fun `guarda y recupera una pelicula`() = runTest {
        val movie = Movie(
            id = 101,
            title = "Corazon de Leon",
            overview = "León Godoy, un arquitecto de gran renombre con una personalidad arrolladora: simpático, galante, carismático... y también divorciado",
            releaseDate = "2006-10-20",
            poster = "/poster.jpg",
            backdrop = "/backdrop.jpg",
            originalTitle = "The Prestige",
            originalLanguage = "en",
            popularity = 88.4,
            voteAverage = 8.5
        )

        localSource.saveMovies(listOf(movie))

        val storedMovies = localSource.getMovies()
        assertEquals(1, storedMovies.size)
        assertEquals(movie, storedMovies.first())
    }


    @Test
    fun `saveMovies debe borrar datos previos de la cache`() = runTest {
        val firstList = listOf(
            Movie(
                id = 1,
                title = "Old Movie",
                overview = "desc",
                releaseDate = "2000-01-01",
                poster = "/old.jpg",
                backdrop = null,
                originalTitle = "Oldie",
                originalLanguage = "en",
                popularity = 10.0,
                voteAverage = 5.0
            )
        )

        val secondList = listOf(
            Movie(
                id = 2,
                title = "New Movie",
                overview = "new desc",
                releaseDate = "2020-01-01",
                poster = "/new.jpg",
                backdrop = null,
                originalTitle = "Newie",
                originalLanguage = "en",
                popularity = 90.0,
                voteAverage = 9.0
            )
        )

        localSource.saveMovies(firstList)
        localSource.saveMovies(secondList)

        val movies = localSource.getMovies()
        assertEquals(1, movies.size)
        assertEquals("New Movie", movies[0].title)
        assertEquals(2, movies[0].id)
    }

}

