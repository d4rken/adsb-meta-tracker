package eu.darken.adsbmt.main.core

import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.darken.adsbmt.common.datastore.PreferenceScreenData
import eu.darken.adsbmt.common.datastore.PreferenceStoreMapper
import eu.darken.adsbmt.common.datastore.createValue
import eu.darken.adsbmt.common.debug.logging.logTag
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralSettings @Inject constructor(
    @ApplicationContext private val context: Context,
) : PreferenceScreenData {

    private val Context.dataStore by preferencesDataStore(name = "settings_core")

    override val dataStore: DataStore<Preferences>
        get() = context.dataStore

    val deviceLabel = dataStore.createValue("core.device.label", Build.DEVICE)

    override val mapper = PreferenceStoreMapper(
        deviceLabel
    )

    companion object {
        internal val TAG = logTag("Core", "Settings")
    }
}