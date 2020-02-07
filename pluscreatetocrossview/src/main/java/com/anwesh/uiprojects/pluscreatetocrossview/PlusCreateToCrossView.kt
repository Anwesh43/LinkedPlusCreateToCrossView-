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

fun Canvas.drawPCCNode(i : Int, scale : Float, paint : Paint) {
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
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class PCCNode(var i : Int, val state : State = State()) {

        private var prev : PCCNode? = null
        private var next : PCCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = PCCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawPCCNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : PCCNode {
            var curr : PCCNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class PlusCreateToCross(var i : Int) {

        private val root : PCCNode = PCCNode(0)
        private var curr : PCCNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *=- 1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : PlusCreateToCrossView) {

        private val animator : Animator = Animator(view)
        private val pcc : PlusCreateToCross = PlusCreateToCross(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            pcc.draw(canvas, paint)
            animator.animate {
                pcc.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            pcc.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : PlusCreateToCrossView {
            val view : PlusCreateToCrossView = PlusCreateToCrossView(activity)
            activity.setContentView(view)
            return view
        }
    }
}