package global.msnthrp.whoo

import android.app.Application
import android.content.Context


class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        lateinit var context: Context
    }
}