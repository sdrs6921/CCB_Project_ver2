package com.example.user.ccb_project_ver2.navigation

import android.os.Bundle
import android.support.design.R.id.container
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.user.ccb_project_ver2.R

class AlarmFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_alarm,container,false)
        return view
    }
}