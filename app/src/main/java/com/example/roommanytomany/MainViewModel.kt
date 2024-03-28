package com.example.roommanytomany

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.roommanytomany.data.Airport
import com.example.roommanytomany.data.AirportDao
import com.example.roommanytomany.data.FavouriteRouteCrossRef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val airportDao: AirportDao
) : ViewModel() {

    private val _status = MutableStateFlow("Saving airport data to db...")
    val status: StateFlow<String> = _status


    init {
        populateDb()
        _status.value = "Airport data is saved."
    }

    private fun populateDb() = viewModelScope.launch {
        airportDao.saveAllAirports(
            airports = listOf(
                Airport(1, "KRK", "John Paul II International Airport Kraków-Balice"),
                Airport(2, "KTW", "Katowice Wojciech Korfanty International Airport"),
                Airport(3, "AAA", "Anaa Airport"),
                Airport(4, "WAW", "Warsaw Chopin Airport"),
                Airport(5, "GDN", "Gdańsk Lech Wałęsa Airport")
            )
        )

        airportDao.saveAllFavouriteRoutes(
            routes = listOf(
                FavouriteRouteCrossRef(4, 5),   // WAW -> GDN
                FavouriteRouteCrossRef(2, 1),   // KTW -> KRK
                FavouriteRouteCrossRef(2, 5),   // KTW -> GDN
                FavouriteRouteCrossRef(5, 4)    // GDN -> WAW
            )
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MainViewModel(
                    airportDao = (this[APPLICATION_KEY] as RoomM2MApplication).airportDao
                )
            }
        }
    }
}