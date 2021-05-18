package fi.organization.skycast

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class PreferencesFrag : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    /** Update title and subtitle */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)?.supportActionBar?.title = "Settings"
        (activity as AppCompatActivity)?.supportActionBar?.subtitle = null
    }
}