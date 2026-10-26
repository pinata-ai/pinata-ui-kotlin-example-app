package com.pinata.uikotlinexample

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.pinata.ui.MarketplaceOpenEvent
import com.pinata.ui.OnMarketplaceOpenListener
import com.pinata.ui.OnPinataErrorListener
import com.pinata.ui.PinataComponentView
import com.pinata.ui.PinataEnvironment
import com.pinata.ui.PinataFullShoppingView
import com.pinata.ui.PinataMarketplacePreviewView
import com.pinata.ui.PinataMarketplaceWalletView

/**
 * One screen per component, picked from the drawer. Wallet and Preview send a
 * `MarketplaceOpenEvent` when the renter wants the marketplace, and the app opens Full Shopping,
 * on the clicked item when the event carries one.
 */
class MainActivity : AppCompatActivity() {

  private lateinit var session: Session
  private lateinit var drawer: DrawerLayout
  private lateinit var navigation: NavigationView
  private lateinit var screen: FrameLayout

  /** Where Back goes from a Full Shopping opened by an event. */
  private var backTo: (() -> Unit)? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    session = Session(this)
    setContentView(R.layout.activity_main)
    drawer = findViewById(R.id.drawer)
    navigation = findViewById(R.id.navigation)
    screen = findViewById(R.id.screen)

    val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)
    ActionBarDrawerToggle(this, drawer, toolbar, R.string.open_menu, R.string.close_menu).also {
      drawer.addDrawerListener(it)
      it.syncState()
    }

    navigation.setNavigationItemSelectedListener { item ->
      drawer.closeDrawers()
      when (item.itemId) {
        R.id.wallet -> showWallet()
        R.id.preview -> showPreview()
        R.id.fullShopping -> showFullShopping(event = null, backTo = null)
        R.id.logout -> logout()
      }
      true
    }

    onBackPressedDispatcher.addCallback(this) {
      when {
        drawer.isDrawerOpen(GravityCompat.START) -> drawer.closeDrawers()
        backTo != null -> backTo?.invoke()
        else -> finish()
      }
    }

    showWallet()
  }

  private fun showWallet() = show(
    R.id.wallet,
    PinataMarketplaceWalletView(this).apply {
      onMarketplaceOpenListener = OnMarketplaceOpenListener { showFullShopping(it, ::showWallet) }
    },
  )

  private fun showPreview() = show(
    R.id.preview,
    PinataMarketplacePreviewView(this).apply {
      onMarketplaceOpenListener = OnMarketplaceOpenListener { showFullShopping(it, ::showPreview) }
    },
  )

  private fun showFullShopping(event: MarketplaceOpenEvent?, backTo: (() -> Unit)?) = show(
    R.id.fullShopping,
    PinataFullShoppingView(this).apply {
      detailsId = event?.detailsId
      detailsType = event?.detailsType
    },
    backTo,
  )

  // The views are single-use, so every screen change creates a new one.
  private fun show(menuItem: Int, view: PinataComponentView, backTo: (() -> Unit)? = null) {
    this.backTo = backTo
    navigation.setCheckedItem(menuItem)
    supportActionBar?.title = navigation.menu.findItem(menuItem).title
    view.environment = PinataEnvironment.SANDBOX
    view.userToken = session.userToken
    view.onErrorListener = OnPinataErrorListener { message ->
      Toast.makeText(this, "Pinata error: $message", Toast.LENGTH_LONG).show()
    }
    screen.removeAllViews()
    screen.addView(view)
    view.load()
  }

  private fun logout() {
    session.logout()
    startActivity(Intent(this, LoginActivity::class.java))
    finish()
  }
}
