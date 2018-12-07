package com.github.jiramot.security.model

class UserContext(
    val id: String,
    var idCard: String,
    var phoneNumber: String,
    var scope: ArrayList<String>
)