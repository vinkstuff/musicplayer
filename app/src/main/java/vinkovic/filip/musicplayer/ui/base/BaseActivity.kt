package vinkovic.filip.musicplayer.ui.base

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import vinkovic.filip.musicplayer.MusicPlayerApplication
import vinkovic.filip.musicplayer.R
import vinkovic.filip.musicplayer.dagger.components.AppComponent

abstract class BaseActivity : AppCompatActivity(), BaseView {

    enum class MenuIcon {
        HAMBURGER, BACK, CLOSE, NONE
    }

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDependencies(MusicPlayerApplication.instance.appComponent)
    }

    abstract fun injectDependencies(appComponent: AppComponent)

    fun initToolbar(toolbar: Toolbar, title: String?, icon: MenuIcon) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        setToolbarIcon(icon)
    }

    fun setToolbarIcon(icon: MenuIcon) {
        val actionBar = supportActionBar ?: return

        when (icon) {
            MenuIcon.HAMBURGER -> {
                actionBar.setHomeButtonEnabled(true)
                actionBar.setDisplayShowHomeEnabled(true)
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_menu))
            }
            MenuIcon.BACK -> {
                actionBar.setHomeButtonEnabled(true)
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setDisplayUseLogoEnabled(false)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_back)
            }
            MenuIcon.CLOSE -> {
                actionBar.setHomeButtonEnabled(true)
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setDisplayUseLogoEnabled(false)
                actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_close))
            }
            else -> {
                actionBar.setDisplayHomeAsUpEnabled(false)
                actionBar.setDisplayUseLogoEnabled(false)
                actionBar.setHomeAsUpIndicator(null)
            }
        }
    }
    override fun showProgress() {
        val dialogShowing = progressDialog?.isShowing ?: false

        if (!dialogShowing && !isFinishing) {
            progressDialog = ProgressDialog(this)
            progressDialog?.setMessage(getString(R.string.loading))
            progressDialog?.show()
        }
    }

    override fun hideProgress() {
        val dialogShowing = progressDialog?.isShowing ?: false

        if (dialogShowing && !isFinishing) {
            progressDialog?.dismiss()
        }
    }

    override fun showMessage(message: String) {
        if (!isFinishing) {
            AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
        }
    }
}
