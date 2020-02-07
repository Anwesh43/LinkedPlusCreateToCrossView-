package com.anwesh.uiprojects.linkedpluscreatetocrossview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.pluscreatetocrossview.PlusCreateToCrossView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlusCreateToCrossView.create(this)
    }
}
