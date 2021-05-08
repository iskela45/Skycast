package fi.organization.skycast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels

class WeekFragment : Fragment(R.layout.fragment_week) {

    private val model : WeekViewModel by activityViewModels()
    

}