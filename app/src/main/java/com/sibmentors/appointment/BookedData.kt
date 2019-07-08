package com.sibmentors.appointment

class BookedData(
    val name: String,
    val status: String,
    val dateslot: String,
    val timeslot: String

) {
    constructor() : this("", "", "", "")

}
