package com.simplemobiletools.commons.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import com.simplemobiletools.commons.R
import com.simplemobiletools.commons.dialogs.ConfirmationDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.*
import com.simplemobiletools.commons.models.FAQItem
import kotlinx.android.synthetic.main.activity_about.*
import java.util.*

class AboutActivity : BaseSimpleActivity() {
    private var appName = ""
    private var linkColor = 0

    override fun getAppIconIDs() = intent.getIntegerArrayListExtra(APP_ICON_IDS) ?: ArrayList()

    override fun getAppLauncherName() = intent.getStringExtra(APP_LAUNCHER_NAME) ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        appName = intent.getStringExtra(APP_NAME) ?: ""
        linkColor = getAdjustedPrimaryColor()
    }

    override fun onResume() {
        super.onResume()
        updateTextColors(about_holder)

        setupWebsite()
        setupEmail()
        setupFAQ()
    }

    private fun setupWebsite() {
        val websiteText = String.format(getString(R.string.two_string_placeholder), getString(R.string.website_label), getString(R.string.my_website))
        about_website.text = websiteText
    }

    private fun setupEmail() {
        val label = getString(R.string.email_label)
        val email = getString(R.string.my_email)

        val appVersion = String.format(getString(R.string.app_version, intent.getStringExtra(APP_VERSION_NAME)))
        val deviceOS = String.format(getString(R.string.device_os), Build.VERSION.RELEASE)
        val newline = "%0D%0A"
        val separator = "------------------------------"
        val body = "$appVersion$newline$deviceOS$newline$separator$newline$newline$newline"
        val href = "$label<br><a href=\"mailto:$email?subject=$appName&body=$body\">$email</a>"
        about_email.text = Html.fromHtml(href)

        if (intent.getBooleanExtra(SHOW_FAQ_BEFORE_MAIL, false) && !baseConfig.wasBeforeAskingShown) {
            about_email.setOnClickListener {
                baseConfig.wasBeforeAskingShown = true
                about_email.movementMethod = LinkMovementMethod.getInstance()
                about_email.setOnClickListener(null)
                ConfirmationDialog(this, "", R.string.before_asking_question_read_faq, R.string.read_it, R.string.skip) {
                    about_faq_label.performClick()
                }
            }
        } else {
            about_email.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun setupFAQ() {
        val faqItems = intent.getSerializableExtra(APP_FAQ) as ArrayList<FAQItem>
        about_faq_label.beVisibleIf(faqItems.isNotEmpty())
        about_faq_label.setOnClickListener {
            openFAQ(faqItems)
        }

        about_faq.beVisibleIf(faqItems.isNotEmpty())
        about_faq.setOnClickListener {
            openFAQ(faqItems)
        }

        about_faq.setTextColor(linkColor)
        about_faq.underlineText()
    }

    private fun openFAQ(faqItems: ArrayList<FAQItem>) {
        Intent(applicationContext, FAQActivity::class.java).apply {
            putExtra(APP_ICON_IDS, getAppIconIDs())
            putExtra(APP_LAUNCHER_NAME, getAppLauncherName())
            putExtra(APP_FAQ, faqItems)
            startActivity(this)
        }
    }


    private fun getStoreUrl() = "https://play.google.com/store/apps/details?id=${packageName.removeSuffix(".debug")}"
}
