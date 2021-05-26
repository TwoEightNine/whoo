package global.msnthrp.whoo.ui.map

import android.graphics.Color
import android.util.Log
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import global.msnthrp.whoo.domain.LocPoint


class WhooMap(
    private val map: MapboxMap
) {

    private lateinit var locPointSource: GeoJsonSource
    private lateinit var latestLocPoint: LocPoint

    private lateinit var tripSource: GeoJsonSource
    private lateinit var tripLocPoints: List<LocPoint>

    private lateinit var startPointSource: GeoJsonSource
    private lateinit var endPointSource: GeoJsonSource

    private var postponedMoveToMyLocPoint = false

    init {
        with(map) {
            setMinZoomPreference(ZOOM_MIN)
            setMaxZoomPreference(ZOOM_MAX)

            with(uiSettings) {
                isLogoEnabled = false
                isCompassEnabled = false
                isRotateGesturesEnabled = false
                isAttributionEnabled = false
            }
        }
        map.setStyle(Style.MAPBOX_STREETS) { style ->
            style.addSource(GeoJsonSource(LOC_POINT_SOURCE_ID).apply {
                locPointSource = this
            })
            style.addSource(GeoJsonSource(TRIP_SOURCE_ID).apply {
                tripSource = this
            })
            style.addSource(GeoJsonSource(START_POINT_SOURCE_ID).apply {
                startPointSource = this
            })
            style.addSource(GeoJsonSource(END_POINT_SOURCE_ID).apply {
                endPointSource = this
            })

            style.addLayer(
                LineLayer(TRIP_LAYER_ID, TRIP_SOURCE_ID)
                    .withProperties(
                        PropertyFactory.lineColor(TRIP_LINE_COLOR),
                        PropertyFactory.lineWidth(TRIP_LINE_WIDTH),
                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND)
                    )
            )
            style.addLayer(
                CircleLayer(START_POINT_LAYER_ID, START_POINT_SOURCE_ID)
                    .withProperties(
                        PropertyFactory.circleColor(START_POINT_COLOR),
                        PropertyFactory.circleRadius(TERMINAL_POINT_WIDTH),
                        PropertyFactory.circleStrokeColor(TERMINAL_POINT_STROKE_COLOR),
                        PropertyFactory.circleStrokeWidth(TERMINAL_POINT_STROKE_WIDTH)
                    )
            )
            style.addLayer(
                CircleLayer(END_POINT_LAYER_ID, END_POINT_SOURCE_ID)
                    .withProperties(
                        PropertyFactory.circleColor(END_POINT_COLOR),
                        PropertyFactory.circleRadius(TERMINAL_POINT_WIDTH),
                        PropertyFactory.circleStrokeColor(TERMINAL_POINT_STROKE_COLOR),
                        PropertyFactory.circleStrokeWidth(TERMINAL_POINT_STROKE_WIDTH)
                    )
            )
            style.addLayer(
                CircleLayer(LOC_POINT_LAYER_ID, LOC_POINT_SOURCE_ID)
                    .withProperties(
                        PropertyFactory.circleColor(LOC_POINT_COLOR),
                        PropertyFactory.circleRadius(LOC_POINT_SIZE),
                        PropertyFactory.circleStrokeColor(LOC_POINT_STROKE_COLOR),
                        PropertyFactory.circleStrokeWidth(LOC_POINT_STROKE_WIDTH)
                    )
            )
        }
    }

    fun updateLocPoint(locPoint: LocPoint) {
        latestLocPoint = locPoint
        locPoint.toPoint()
            .let(Feature::fromGeometry)
            .let(FeatureCollection::fromFeature)
            .let(locPointSource::setGeoJson)
        if (postponedMoveToMyLocPoint) {
            postponedMoveToMyLocPoint = false
            moveCameraToMyLocation()
        }
    }

    fun updateTrip(locPoints: List<LocPoint>) {
        tripLocPoints = locPoints
        tripLocPoints
            .map { it.toPoint() }
            .let(LineString::fromLngLats)
            .let(Feature::fromGeometry)
            .let(tripSource::setGeoJson)
        if (locPoints.isNotEmpty()) {
            locPoints.first().toPoint()
                .let(Feature::fromGeometry)
                .let(FeatureCollection::fromFeature)
                .let(startPointSource::setGeoJson)
            if (locPoints.size > 1) {
                locPoints.last().toPoint()
                    .let(Feature::fromGeometry)
                    .let(FeatureCollection::fromFeature)
                    .let(endPointSource::setGeoJson)
            }
        }
    }

    fun moveCameraToMyLocation() {
        if (!::latestLocPoint.isInitialized) {
            postponedMoveToMyLocPoint = true
        } else {
            moveCameraTo(latestLocPoint)
        }
    }

    private fun moveCameraTo(locPoint: LocPoint, zoom: Double = ZOOM_DEFAULT) {
        map.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(locPoint.toLatLng())
                    .zoom(zoom)
                    .build()
            )
        )
    }

    private fun LocPoint.toLatLng() = LatLng(latitude, longitude)
    private fun LocPoint.toPoint() = Point.fromLngLat(longitude, latitude)

    companion object {

        private const val ZOOM_MIN = 6.0
        private const val ZOOM_DEFAULT = 16.0
        private const val ZOOM_MAX = 16.0

        private const val LOC_POINT_LAYER_ID = "locPointLayerId"
        private const val TRIP_LAYER_ID = "tripLayerId"
        private const val START_POINT_LAYER_ID = "startPointLayerId"
        private const val END_POINT_LAYER_ID = "endPointLayerId"

        private const val LOC_POINT_SOURCE_ID = "locPointSourceId"
        private const val TRIP_SOURCE_ID = "tripSourceId"
        private const val START_POINT_SOURCE_ID = "startPointSourceId"
        private const val END_POINT_SOURCE_ID = "endPointSourceId"

        private const val LOC_POINT_COLOR = Color.BLUE
        private const val LOC_POINT_SIZE = 6f
        private const val LOC_POINT_STROKE_COLOR = Color.WHITE
        private const val LOC_POINT_STROKE_WIDTH = 2f

        private const val TRIP_LINE_COLOR = Color.BLUE
        private const val TRIP_LINE_WIDTH = 4f

        private const val TERMINAL_POINT_WIDTH = 6f
        private const val TERMINAL_POINT_STROKE_WIDTH = 2f
        private const val TERMINAL_POINT_STROKE_COLOR = Color.WHITE
        private const val START_POINT_COLOR = 0xff4DB34D.toInt()
        private const val END_POINT_COLOR = 0xffDC5E48.toInt()
    }

}