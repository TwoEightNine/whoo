package global.msnthrp.whoo.data.trip.room.model

import androidx.room.*
import global.msnthrp.whoo.data.trip.room.model.entity.TripEntity
import global.msnthrp.whoo.domain.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Query("SELECT * FROM TripEntity")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateTrip(trip: TripEntity)

    @Query("SELECT id FROM TripEntity ORDER BY id DESC LIMIT 1")
    fun getMaxTripId(): List<Int>

}