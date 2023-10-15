package com.star.myapplication.proto

import kotlinx.serialization.Serializable

@Serializable
data class Upload(val temp: Int, val humidity: Int)