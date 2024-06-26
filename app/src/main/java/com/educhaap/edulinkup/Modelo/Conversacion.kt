package com.educhaap.edulinkup.Modelo

import com.google.firebase.Timestamp

data class Conversacion(
    var uid: String? = null,
    val usuarios: List<String> = listOf(),
    var ultimoMensaje: String? = null,
    var timestamp: Long? = null
)

