package com.lightningkite.koolui.layout

import com.lightningkite.koolui.geometry.Measurement

interface DimensionCalculator {
    var dimensionAccess: DimensionAccess

    val measurement: Measurement
    fun layout(start: Float, end: Float)
    fun invalidate()
}