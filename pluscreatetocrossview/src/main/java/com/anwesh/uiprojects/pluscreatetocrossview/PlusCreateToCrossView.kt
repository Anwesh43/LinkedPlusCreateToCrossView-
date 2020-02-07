package com.anwesh.uiprojects.pluscreatetocrossview

/**
 * Created by anweshmishra on 07/02/20.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.app.Activity
import android.content.Context

val nodes : Int = 5
val lines : Int = 4
val scGap : Float = 0.02f
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#4CAF50")
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20
val deg : Float = 45f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawPlusCreateLine(i : Int, sf : Float, size : Float, paint : Paint) {
    val sfi : Float = sf.divideScale(i, lines)
    save()
    rotate(90f * i + 90f * sfi)
    drawLine(0f, 0f, 0f, -size, paint)
    restore()
}

fun Canvas.drawPlusCreateLineToCross(scale : Float, size : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sf1 = sf.divideScale(0, 2)
    val sf2 : Float = sf.divideScale(1, 2)
    val scDiv : Double = 1.0 / lines
    val k : Int = Math.floor(sf1 / scDiv).toInt()
    save()
    rotate(deg * sf2)
    for (j in 0..k) {
        drawPlusCreateLine(j, sf1, size, paint)
    }
    restore()
}

fun Canvas.drawPCCNode(scale : Float, i : Int, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(gap * (i + 1), h / 2)
    drawPlusCreateLineToCross(scale, size, paint)
    restore()
}

class PlusCreateToCrossView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}