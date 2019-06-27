package com.munkhtulga.understand

class Book(val title: String) {
    val remarks: HashMap<Int, String> = HashMap()
    val remarkEnds: HashMap<Int, Int> = HashMap()
    var lastRemarkLocation: Int = 0
        private set

    fun addRemark(key: Int, value: String) = remarks.put(key, value)
    fun getRemark(key: Int): String? {
        lastRemarkLocation = key
            return remarks[key]
        }
}