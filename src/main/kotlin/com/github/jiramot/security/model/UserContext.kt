package com.github.jiramot.security.model

class UserContext(
    val id: String,
    var scope: ArrayList<String>,
    var payload: HashMap<String, String>
)