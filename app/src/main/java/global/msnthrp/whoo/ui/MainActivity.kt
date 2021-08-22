package global.msnthrp.whoo.ui

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.Mapbox
import global.msnthrp.whoo.R
import global.msnthrp.whoo.services.LocationService


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        if (isLightTheme()) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.status_bar)
        window.navigationBarColor = Color.TRANSPARENT

        setContentView(R.layout.activity_main)
        LocationService.launch(this)
        Mapbox.getInstance(this, getString(R.string.mapbox_public_token))
    }

    private fun isLightTheme(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES
    }
}