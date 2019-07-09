package com.sibmentors.appointment

class Data(
    val id: String,
    val email: String,
    val pass: String,
    val name: String,
    val number: String,
    val studentId: String,
    val status: String,
    val user_type: String,
    val mentorreferal:String
) {
    constructor() : this("", "", "", "", "", "", "", "","")

}
