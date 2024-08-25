package com.scanner

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Find the ImageView and apply the animation
        val splashLogo = findViewById<ImageView>(R.id.splash_logo)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade)
        splashLogo.startAnimation(fadeInAnimation)

        // Wait for the animation to finish and then start the main activity
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                finish()  // Close the splash activity so the user can't go back to it
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}
