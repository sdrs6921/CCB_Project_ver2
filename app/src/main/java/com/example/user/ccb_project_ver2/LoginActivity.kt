package com.example.user.ccb_project_ver2

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.util.Base64
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var callbackManager : CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        email_login_button.setOnClickListener {
            createAndroidLoginEmail()
        }

        google_sign_in_button.setOnClickListener {
            googleLogin()
        }

        facebook_login_button.setOnClickListener {
            facebookLogin()
        }
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))//firebase json파일에 정의되어 있음
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso) // google Login Class 완성
        callbackManager  = CallbackManager.Factory.create()//Facebook callback Manager 초기화
    }

    fun createAndroidLoginEmail(){
        //계정을 만드는 Logic
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())?.addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                //Create new account
                moveMainPage(auth?.currentUser)
                //Toast.makeText(this,"아이디 생성이 완료되었습니다.",Toast.LENGTH_LONG).show()
            }else if(task.exception?.message.isNullOrEmpty()){
                //Error
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
            }else{
                //Login
                signinEmail()
            }
        }
    }

    fun signinEmail(){
        //로그인이 실제로 되는 로직
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())?.addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                moveMainPage(auth?.currentUser)
                //Toast.makeText(this,"로그인에 성공하였습니다.",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun moveMainPage(user : FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount){
        var credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth?.signInWithCredential(credential)
    }

    fun facebookLogin(){
        LoginManager
                .getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile","email"))
        LoginManager
                .getInstance()
                .registerCallback(callbackManager, object:FacebookCallback<LoginResult>{
                    override fun onSuccess(result: LoginResult?) {
                        handelFacebookAccessToken(result?.accessToken)
                    }

                    override fun onCancel() {

                    }

                    override fun onError(error: FacebookException?) {

                    }
                })
    }

    fun handelFacebookAccessToken(token:AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)?.addOnCompleteListener {
            task ->
            if(task.isSuccessful){
                moveMainPage(auth?.currentUser)
                Toast.makeText(this,"로그인에 성공하였습니다.",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode,resultCode,data)

        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                var account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            }
        }
    }
}
