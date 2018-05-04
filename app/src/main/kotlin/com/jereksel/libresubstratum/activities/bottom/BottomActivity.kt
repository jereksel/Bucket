package com.jereksel.libresubstratum.activities.bottom

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.about.AboutFragment
import com.jereksel.libresubstratum.activities.themelist.IThemeListViewViewModel
import com.jereksel.libresubstratum.activities.themelist.ThemeListView
import com.jereksel.libresubstratum.activities.themelist.ThemeListViewViewModelProvider
import com.jereksel.libresubstratum.extensions.getLogger
import kotlinx.android.synthetic.main.activity_bottom.*
import javax.inject.Inject

class BottomActivity: AppCompatActivity(), ThemeListViewViewModelProvider {

    val log = getLogger()

    val THEME_LIST_TAG = "THEME_LIST_TAG"
    val ABOUT_TAG = "ABOUT_TAG"

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    lateinit var _themeListViewViewModel: IThemeListViewViewModel

    lateinit var fragment1: ThemeListView
    lateinit var fragment2: AboutFragment

    override fun getThemeListViewViewModel(): IThemeListViewViewModel {
        return _themeListViewViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom)
        (applicationContext as App).getAppComponent(this).inject(this)
        title = "BOTTOM ACTIVITY"

        val fragmentManager = supportFragmentManager

//        if (savedInstanceState != null) {
//            fragment1 = fragmentManager.getFragment(savedInstanceState, "fragment1") as ThemeListView
//            fragment2 = fragmentManager.getFragment(savedInstanceState, "fragment2") as AboutFragment
//        } else {
            fragment1 = ThemeListView()
            fragment2 = AboutFragment()
//        }

//        val fragment1 = ThemeListView()
//
//        if (savedInstanceState != null) {
//            supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment1).commit()
//        }

//        var fragment1 = fragmentManager.findFragmentByTag("a")

        _themeListViewViewModel = ViewModelProviders.of(this, factory).get(IThemeListViewViewModel::class.java)


//        val fragment1 = ThemeListView()
//
//        val fragment2 = AboutFragment()

//        if (fragment1 == null) {
//            fragment1 = ThemeListView()
//            fragmentManager.beginTransaction().add(fragment1, "a").commit()
//        }

        bottom_navigation.setOnNavigationItemSelectedListener { item: MenuItem ->
            when(item.itemId) {
                R.id.action_theme_list -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment1, THEME_LIST_TAG).commit()
                    true
                }
                R.id.action_about -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment2, ABOUT_TAG).commit()
                    true
                }
                else -> false
            }

//            supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment1, TAG).commit()

//            true
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment, fragment1, THEME_LIST_TAG).commit()
        }


    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
//        supportFragmentManager.putFragment(outState, "fragment1", fragment1)
//        supportFragmentManager.putFragment(outState, "fragment2", fragment2)
    }

}