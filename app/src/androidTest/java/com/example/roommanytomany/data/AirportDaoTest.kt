package com.example.roommanytomany.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AirportDaoTest {
    private val airports: List<Airport> = listOf(
        Airport(1, "KRK", "John Paul II International Airport Kraków-Balice"),
        Airport(2, "KTW", "Katowice Wojciech Korfanty International Airport"),
        Airport(3, "AAA", "Anaa Airport"),
        Airport(4, "WAW", "Warsaw Chopin Airport"),
        Airport(5, "GDN", "Gdańsk Lech Wałęsa Airport")
    )

    private val routes: List<FavouriteRouteCrossRef> = listOf(
        FavouriteRouteCrossRef(4, 5),   // WAW -> GDN
        FavouriteRouteCrossRef(2, 1),   // KTW -> KRK
        FavouriteRouteCrossRef(2, 5),   // KTW -> GDN
        FavouriteRouteCrossRef(5, 4)    // GDN -> WAW
    )

    private lateinit var airportDatabase: AirportDatabase
    private lateinit var airportDao: AirportDao

    @Before
    fun createDb() = runBlocking {
        val context: Context = ApplicationProvider.getApplicationContext()
        airportDatabase = Room.inMemoryDatabaseBuilder(context, AirportDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        airportDao = airportDatabase.airportDao()
        airportDao.saveAllAirports(airports)
        airportDao.saveAllFavouriteRoutes(routes)
    }

    @After
    fun closeDb() {
        airportDatabase.close()
    }

    @Test
    fun getAllFavouriteRoutes__givenFavouriteRoutes__onlyFavouriteRoutesAreReturned_or() = runTest {
        val expectedRouteOrdering: Comparator<FavouriteRoute> = compareBy(
            { it.departureAirport.iataCode },
            { it.destinationAirport.iataCode }
        )
        val expectedFavouriteRoutes = listOf(
            FavouriteRoute(airports[3], airports[4]), // WAW -> GDN
            FavouriteRoute(airports[1], airports[0]), // KTW -> KRK
            FavouriteRoute(airports[1], airports[4]), // KTW -> GDN
            FavouriteRoute(airports[4], airports[3])  // GDN -> WAW
        ).sortedWith(expectedRouteOrdering)

        val actualFavouriteRoutes = airportDao.getAllFavouriteRoutes().first()
        assertEquals(expectedFavouriteRoutes, actualFavouriteRoutes)
    }
}
