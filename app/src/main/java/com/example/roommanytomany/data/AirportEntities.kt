package com.example.roommanytomany.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "airport")
data class Airport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "iata_code")
    val iataCode: String,
    val name: String
)

@Entity(
    tableName = "favourite",
    primaryKeys = ["departureId", "destinationId"],
    indices = [Index(value = ["destinationId"])],
    foreignKeys = [ForeignKey(
        entity = Airport::class,
        parentColumns = ["id"],
        childColumns = ["departureId"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Airport::class,
        parentColumns = ["id"],
        childColumns = ["destinationId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class FavouriteRouteCrossRef(
    val departureId: Int,
    val destinationId: Int
)

data class FavouriteRoute(
    @Embedded val departureAirport: Airport,
    @Embedded(prefix = "to_") val destinationAirport: Airport
)