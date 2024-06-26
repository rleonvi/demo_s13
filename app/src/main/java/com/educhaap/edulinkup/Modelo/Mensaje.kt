package com.educhaap.edulinkup.Modelo



data class Mensaje(
    val enviaUid: String ? = null,
    val recibeUid: String ? = null,
    val contenido: String ? = null,
    val timestamp: Long? = System.currentTimeMillis(),
    val leido: Boolean = false // Nuevo campo para rastrear el estado de lectura
)

