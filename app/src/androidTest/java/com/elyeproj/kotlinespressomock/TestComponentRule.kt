package com.elyeproj.kotlinespressomock

import android.content.Context
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.MockAppComponent
import com.jereksel.libresubstratum.domain.IPackageManager

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestComponentRule(val context: Context) : TestRule {

    private var mTestComponent: MockAppComponent? = null

    val mockInjectedData: IPackageManager
        get() = mTestComponent!!.getPackageManager()

    private fun setupDaggerTestComponentInApplication() {
        val application = context.applicationContext as App
//        mTestComponent = DaggerApplicationTestComponent.builder().applicationTestModule(ApplicationTestModule(application)).build()
//        application.component = mTestComponent as ApplicationComponent
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                try {
                    setupDaggerTestComponentInApplication()
                    base.evaluate()
                } finally {
                    mTestComponent = null
                }
            }
        }
    }
}
