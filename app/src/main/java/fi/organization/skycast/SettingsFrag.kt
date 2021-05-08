package fi.organization.skycast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup

class SettingsFrag : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check metric by default
        val radioGroup : RadioGroup = view.findViewById(R.id.radioGroup)
        radioGroup.check(R.id.radio1)

    }
}