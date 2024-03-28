# Room: example of relation many-to-many

The usual way of retrieving objects of relation is described on
[Android Developers: define many-to-many](https://developer.android.com/training/data-storage/room/relationships#many-to-many). 

It results in having a cross reference table, e.g.:
```kotlin
@Entity(primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long
)
```
and a structure embedding objects of one type and list of objects of another one, e.g:
```kotlin
data class PlaylistWithSongs(
    @Embedded val playlist: Playlist,
    @Relation(
         parentColumn = "playlistId",
         entityColumn = "songId",
         associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<Song>
)

data class SongWithPlaylists(
    @Embedded val song: Song,
    @Relation(
         parentColumn = "songId",
         entityColumn = "playlistId",
         associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val playlists: List<Playlist>
)
```

## Problem ##

Imagine we stored a favourite route from one airport to another as cross reference table: 

```kotlin
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
```

**How to get each row from that table with actual data?**
I.e.:
```kotlin
listOf(
    FavouriteRoute(
        departureAirport = Airport(1, "LAX"),
        destinationAirport = Airport(2, "JFK"),
    ),
    //....
)
```

## Solution ##

1. Define POJO class for data to be fetched
2. Annotate fields with `@Embedded` annotation - it indicates, that SQL query will return fields that
will populate the annotated object.
3. Second object needs to be annotated with `@Embedded(prefix = ...)` to avoid Room complaining about
the same field names in both objects

```kotlin
data class FavouriteRoute(
    @Embedded val departureAirport: Airport,
    @Embedded(prefix = "to_") val destinationAirport: Airport
)
```
4. In Dao create a Query that uses the same prefix
``` kotlin
// In Dao:
@Query(
    "SELECT departure.id, departure.iata_code, departure.name, destination.id AS to_id, " +
            "destination.iata_code AS to_iata_code, destination.name AS to_name " +
            "FROM favourite " +
            "INNER JOIN airport AS departure ON (departure.id = departureId)" +
            "INNER JOIN airport AS destination ON (destinationId = destination.id )" +
            "ORDER BY departure.iata_code ASC, destination.iata_code"
)
fun getAllFavouriteRoutes(): Flow<List<FavouriteRoute>>
```