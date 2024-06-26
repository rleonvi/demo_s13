package com.educhaap.edulinkup.Controlador

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.educhaap.edulinkup.Addtional_data
import com.educhaap.edulinkup.MainActivity
import com.educhaap.edulinkup.Modelo.Usuario
import com.educhaap.edulinkup.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class AuthManager(private val activity: Activity) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    fun signInWithGoogle() {
        try {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(activity, googleConf)
            val signInIntent = googleSignInClient.signInIntent
            activity.startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: Exception) {
            handleError("Error during Google sign-in configuration: ${e.message}")
        }
    }

    fun handleGoogleSignInResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val email = account.email
                    val idToken = account.idToken
                    val name = account.displayName
                    if (email != null && idToken != null && name != null) {
                        firebaseAuthWithGoogle(idToken, email, name)
                    } else {
                        handleError("Google sign in failed: Missing data")
                    }
                } else {
                    handleError("Google sign in failed: Account is null")
                }
            } catch (e: ApiException) {
                handleError("Google sign in failed: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, email: String, name: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    if (uid != null)
                    {
                        //Obtenemos el proveedor
                        //Si alguno de estos datos es nulo mandamos al usuario a la actividad de datos adicionales
                        //Sacamos el proveedor
                        val providerDataList = user.providerData
                        val providerId = if (providerDataList.size >1){
                            providerDataList[1].providerId
                        }else{
                            "desconocido"
                        }

                        //vaidamos si este usuario tiene sus datos completos de lo contrario redireccionamos a la pantalla de
                        //datos adicionales
                        getUserByEmail(email){usuario ->
                            if (usuario != null)
                            {
                                //Validamos si el usuario tiene su informacion completa
                                var codigoInstitucion : Int = usuario.codigoInstitucion
                                var codigoCarrera : Int = usuario.codigoCarrera
                                var primerNombre : String? = usuario.firstName
                                var segundoNombre : String? = usuario.secondName
                                var primerApellido : String? = usuario.firstLastName
                                var segundoApellido : String? = usuario.secondLastName

                                if(codigoCarrera == 0 || codigoInstitucion == 0 || primerNombre == null || segundoNombre == null || primerApellido == null || segundoApellido == null)
                                {
                                    startAddtionalDataActivity(activity,email, name, uid, providerId)
                                }
                                else //sino lo mandamos al main de un solo
                                {
                                    startMainActivity(activity,email, name)
                                }
                            }
                            else //si el usuario no existe lo mandamos a registrarse
                            {
                                startAddtionalDataActivity(activity,email, name, uid, providerId)
                                //handleError("usuario no encontrado error en firebaseAuthWithGoogle")
                            }
                        }
                        //saveUserToFirestore(user, uid, email, name)
                    }
                    else
                    {
                        handleError("UID is null after successful authentication")
                    }
                } else {
                    handleError("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    //Manejo de inicio de sesion con microsoft
    fun signInMicrosoft() {
        try {
            val provider = OAuthProvider.newBuilder("microsoft.com")
            provider.addCustomParameter("prompt", "select_account")
            provider.addCustomParameter("prompt", "consent")
            provider.scopes = listOf("User.Read")

            auth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val authResult = task.result
                        val user = authResult?.user
                        val email = user?.email
                        val credential = authResult?.credential as? OAuthCredential
                        val required = "Validation to microsoft"


                        if (credential != null && email != null) {
                            firebaseAuthWithMicrosoft(credential, email,required)
                        } else {
                            handleError("Credential or email is null")
                        }
                    } else {
                        handleError("Sign in with Microsoft failed: ${task.exception?.message}")
                    }
                }
                .addOnFailureListener { exception ->
                    handleError("Error during Microsoft sign-in: ${exception.message}")
                }
        }catch (e: Exception) {
            handleError("Error durante de inicio de session : ${e.message}")
        }
    }

    //Metodo de validacion credenciales con microsoft
    private fun firebaseAuthWithMicrosoft(credential: OAuthCredential, email: String, name: String) {
        try {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid

                        if (uid != null)
                        {
                            //Obtenemos el proveedor
                            //Si alguno de estos datos es nulo mandamos al usuario a la actividad de datos adicionales
                            //Sacamos el proveedor
                            val providerDataList = user.providerData
                            val providerId = if (providerDataList.size >1){
                                providerDataList[1].providerId
                            }else{
                                "desconocido"
                            }

                            //vaidamos si este usuario tiene sus datos completos de lo contrario redireccionamos a la pantalla de
                            //datos adicionales
                            getUserByEmail(email){usuario ->
                                if (usuario != null)
                                {
                                    //Validamos si el usuario tiene su informacion completa
                                    var codigoInstitucion : Int = usuario.codigoInstitucion
                                    var codigoCarrera : Int = usuario.codigoCarrera
                                    var primerNombre : String? = usuario.firstName
                                    var segundoNombre : String? = usuario.secondName
                                    var primerApellido : String? = usuario.firstLastName
                                    var segundoApellido : String? = usuario.secondLastName

                                    if(codigoCarrera == 0 || codigoInstitucion == 0 || primerNombre == null || segundoNombre == null || primerApellido == null || segundoApellido == null)
                                    {
                                        startAddtionalDataActivity(activity,email, name, uid, providerId)
                                    }
                                    else //sino lo mandamos al main de un solo
                                    {
                                        startMainActivity(activity,email, name)
                                    }
                                }
                                else //si el usuario no existe lo mandamos a registrarse
                                {
                                    startAddtionalDataActivity(activity,email, name, uid, providerId)
                                    //handleError("usuario no encontrado error en firebaseAuthWithMicrosoft")
                                }
                            }
                            //saveUserToFirestore(user,uid,email,name)
                        }
                        else
                        {
                            handleError("UID is null after successful authentication")
                        }
                    } else {
                        handleError("Authentication failed: ${task.exception?.message}")
                    }
                }
        }catch (e: Exception) {
            handleError("Error durante la validacion de credenciales en el inicio de session : ${e.message}")
        }
    }

    //Metodo para guardar datos de usuario en la base de datos
    public fun saveUserToFirestore(providerId : String, uid: String, email: String, name: String, primerNombre: String, segundoNombre: String, primerApellido: String, segundoApellido: String, institucion: Int, carrera: Int) {
        try {

            val usuario = Usuario(uid,email,name,providerId, primerNombre, segundoNombre, primerApellido, segundoApellido, institucion, carrera)
            db.collection("usuarios").document(uid)
                .set(usuario)
                .addOnCompleteListener {
                    startMainActivity(activity,email, name)
                    Log.d("AuthMange", "Usuario guardado en Firestore")
                }
                .addOnFailureListener { exception ->
                    handleError("Error al guardar los datos de usuario: ${exception.message}")
                }
        }catch (e: Exception) {
            handleError("Error al momento de guardar datos: ${e.message}")
        }
    }

    //Metodo de validacion de sesion activa
    fun checkSesion() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Recuperar los datos del usuario desde Firestore
            val uid = currentUser.uid
            val docRef = db.collection("usuarios").document(uid)
            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val email = document.getString("email") ?: ""
                    val name = document.getString("name") ?: ""
                    startMainActivity(activity,email, name)
                } else {
                    Log.d("SignInActivity", "No hay document")
                }
            }.addOnFailureListener { exception ->
                Log.d("SignInActivity", "error al obtener document ", exception)
            }
        }
    }

    //Metodo para mandar a llamar intents
    private fun startMainActivity(context: Context, email: String, name: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("EXTRA_EMAIL",email)
            putExtra("EXTRA_NAME",name)
        }
        context.startActivity(intent)
        if(context is Activity){
            context.finish()
        }
    }

    //Metodo que redirecciona a la actividad de datos adicionales
    private fun startAddtionalDataActivity(context: Context, email: String, name: String, uid: String, user: String) {
        try
        {
            val intent = Intent(context, Addtional_data::class.java).apply {
                putExtra("EXTRA_EMAIL",email)
                putExtra("EXTRA_NAME",name)
                putExtra("EXTRA_UID",uid)
                putExtra("EXTRA_PROVIDER_ID",user)

            }
            context.startActivity(intent)
            if(context is Activity){
                context.finish()
            }
        }
        catch(ex : Exception)
        {
            Log.e("AuthManager","Error en startAddtionalDataActivity: "+ex.message)
        }
    }

    //Metodo para buscar usuario por correo y retorna por medio de un callback el usuario
    private fun getUserByEmail(email : String, callback: (Usuario?) -> Unit)
    {
        db.collection("usuarios")
            .whereEqualTo("email",email)
            .get()
            .addOnSuccessListener { resultado ->
                if(!resultado.isEmpty)
                {
                    val documento = resultado.documents[0]
                    val usuario = documento.toObject(Usuario::class.java)
                    //DEVOLVEMOS AL USUARIO
                    callback(usuario)
                }
                else
                {
                    callback(null) // No se encontró el usuario
                }
            }
            .addOnFailureListener{exception ->
                Log.w("AuthManager", "Error al consultar el usuario por correo", exception)
                callback(null) // En caso de error, devolver nul
            }
    }

    //Metodo para manejar errores
    private fun handleError(message: String) {
        Log.e("AuthMange", message)
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    //constante de solicitud para inicio de sesion con google
    companion object{
        const val RC_SIGN_IN = 9001
    }

}