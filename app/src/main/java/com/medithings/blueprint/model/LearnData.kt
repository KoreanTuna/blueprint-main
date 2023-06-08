package com.medithings.blueprint.model

data class LearnData(
    val yValue: Float, // y값 (ml)
    val sjData: MutableList<List<Float>> = mutableListOf(), // x값 2중배열
)
