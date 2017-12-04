package com.jereksel.libresubstratum.activities.database

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.jereksel.libresubstratum.App

import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.database.DatabaseActivity.PlaceholderFragment.Companion.FragmentType.*
import com.jereksel.libresubstratum.adapters.DatabaseAdapter
import com.jereksel.libresubstratum.domain.DatabaseViewModelFactory
import com.jereksel.libresubstratum.domain.SubstratumDatabaseTheme
import com.jereksel.libresubstratum.extensions.getLogger
import kotlinx.android.synthetic.main.activity_database.*
import kotlinx.android.synthetic.main.fragment_database.*
import javax.inject.Inject

class DatabaseActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    /**
     * The [ViewPager] that will host the section contents.
     */
    private lateinit var mViewPager: ViewPager

    val log = getLogger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById<View>(R.id.container) as ViewPager
        mViewPager.adapter = mSectionsPagerAdapter

        tabLayout.setupWithViewPager(mViewPager)
//
//        val viewMode = ViewModelProviders.of(this).get(DatabaseViewModel::class.java)
//
//        viewMode.getApps().observe(this, Observer<List<SubstratumDatabaseTheme>> {
//            val data = it
//            if (data != null) {
//                log.debug("Apps: {}", data)
//            }
//        })

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_database, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return PlaceholderFragment.Companion.FragmentType.get(position).name
//            when (position) {
//                0 -> return "SECTION 1"
//                1 -> return "SECTION 2"
//                2 -> return "SECTION 3"
//            }
//            return null
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        @Inject
        lateinit var viewModelFactory: DatabaseViewModelFactory

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_database, container, false)

            val type = FragmentType.get(arguments.getInt(ARG_SECTION_NUMBER))

            (context.applicationContext as App).getAppComponent(context).inject(this)

            val viewModel = ViewModelProviders.of(this, viewModelFactory).get(DatabaseViewModel::class.java)

            val observable = when(type) {
                CLEAR -> viewModel.getClearTheme()
                DARK -> viewModel.getDarkTheme()
                LIGHT -> viewModel.getLightTheme()
            }

            observable
                    .observe(this, Observer<List<SubstratumDatabaseTheme>> {

                        recyclerView.apply {
                            layoutManager = LinearLayoutManager(context)
                            itemAnimator = DefaultItemAnimator()
                            adapter = DatabaseAdapter((it ?: listOf()))
                        }

                    })

            return rootView
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }

            enum class FragmentType(val id: Int) {
                CLEAR(0),
                DARK(1),
                LIGHT(2);

                companion object {
                    fun get(id: Int) = values().first { it.id == id }
                }
            }
        }
    }
}
