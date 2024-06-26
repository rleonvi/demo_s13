package com.educhaap.edulinkup.Modelo

data class Usuario(
    val uid: String? = null,
    val email: String? = null,
    val name: String? = null,
    val provider: String? = null,
    val firstName: String? = null,
    val secondName: String? = null,
    val firstLastName: String? = null,
    val secondLastName: String? = null,
    var codigoInstitucion: Int = 0,
    var codigoCarrera: Int = 0,
    var mensajeNoLeido: Int = 0,
    var ultimoMensaje: String? = null


)
