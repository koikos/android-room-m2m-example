package com.example.roommanytomany.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {

    @Query(
        "SELECT departure.id, departure.iata_code, departure.name, destination.id AS to_id, " +
                "destination.iata_code AS to_iata_code, destination.name AS to_name " +
                "FROM favourite " +
                "INNER JOIN airport AS departure ON (departure.id = departureId)" +
                "INNER JOIN airport AS destination ON (destinationId = destination.id )" +
                "ORDER BY departure.iata_code ASC, destination.iata_code"
    )
    fun getAllFavouriteRoutes(): Flow<List<FavouriteRoute>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveAllAirports(airports: List<Airport>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveAllFavouriteRoutes(routes: List<FavouriteRouteCrossRef>)
}