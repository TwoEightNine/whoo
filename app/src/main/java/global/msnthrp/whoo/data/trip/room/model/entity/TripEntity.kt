package global.msnthrp.whoo.data.trip.room.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class TripEntity(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "start_time")
    val startTime: Long? = null,
    @ColumnInfo(name = "end_time")
    val endTime: Long? = null,

    @ColumnInfo(name = "max_speed")
    val maxSpeed: Float = 0f,
    @ColumnInfo(name = "avg_speed")
    val avgSpeed: Float = 0f,

    @ColumnInfo(name = "max_altitude")
    val maxAltitude: Double = 0.0,
    @ColumnInfo(name = "min_altitude")
    val minAltitude: Double = 0.0,

    val distance: Float = 0f
)