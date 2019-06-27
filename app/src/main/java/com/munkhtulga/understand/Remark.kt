package com.munkhtulga.understand

data class Remark (
    val content: String,
    val locationStart: Int,
    val locationEnd: Int,
    val author: String,
    val bookName: String
)