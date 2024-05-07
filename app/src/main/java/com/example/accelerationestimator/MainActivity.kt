package com.example.accelerationestimator

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.accelerationestimator.databinding.ActivityMainBinding
import com.example.accelerationestimator.ui.HomeFirstTabFragment
import com.google.android.material.navigation.NavigationView
import com.example.accelerationestimator.ui.getAllViews
import com.example.accelerationestimator.ui.getSpeedMeasureUnit
import com.example.accelerationestimator.ui.setSpeedMeasureUnit
import java.lang.NullPointerException


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var binding: ActivityMainBinding? = null
    private var mAppBarConfiguration: AppBarConfiguration? = null
    private var drawer: DrawerLayout? = null
    private var navController: NavController? = null
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            layoutInflater
        )

        Log.i("speedM", getSpeedMeasureUnit(this).toString())

        setContentView(binding!!.getRoot())
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = binding!!.drawerLayout
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_home, R.id.menu_settings, R.id.menu_about
        )
            .setOpenableLayout(binding!!.drawerLayout)
            .build()
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        NavigationUI.setupActionBarWithNavController(this, navController!!, mAppBarConfiguration!!)
        binding!!.navView.setNavigationItemSelectedListener(this)


        // hide tablayout when keyboard appears
        val rootView = findViewById<View>(android.R.id.content)
        rootView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.height ?: 0
            val visibleHeight = r.bottom - r.top
            var lastVisibleHeight: Int? = null
            try{
                if (lastVisibleHeight != -1) {
                    if (lastVisibleHeight != visibleHeight) {
                        val heightDiff = screenHeight - visibleHeight
                        if (heightDiff > screenHeight * 0.15) { // Keyboard is showing
                            findViewById<View>(R.id.tabLayout).visibility = View.GONE

                        } else { // Keyboard is hiding
                            findViewById<View>(R.id.tabLayout).visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: NullPointerException) {
            // handler
            }
            lastVisibleHeight = visibleHeight
        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_settings) navController!!.navigate(R.id.settingsfrag)
        else if (id == R.id.menu_about) navController!!.navigate(R.id.aboutfrag)
        drawer!!.closeDrawer(GravityCompat.START)
        return false
    }




}