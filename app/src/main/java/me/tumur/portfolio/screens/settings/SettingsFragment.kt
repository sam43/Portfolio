package me.tumur.portfolio.screens.settings

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import me.tumur.portfolio.R
import me.tumur.portfolio.R.string
import me.tumur.portfolio.R.xml
import me.tumur.portfolio.screens.MainViewModel
import me.tumur.portfolio.utils.constants.Constants
import me.tumur.portfolio.utils.theme.ThemeHelper


/**
 * An fragment that inflates a settings preferences xml.
 */

class SettingsFragment : PreferenceFragmentCompat() {

    /** VARIABLES * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /** ViewModel */

    /**
     * Returns a property delegate to access ViewModel
     * by default scoped to this Fragment:
     * Default scope may be overridden with parameter ownerProducer:
     * This property can be accessed only after
     * this Fragment is attached i.e.,after Fragment.onAttach,
     * and access prior to that will result in IllegalArgumentException.
     * */
    private val sharedViewModel: MainViewModel by activityViewModels()

    /** INITIALIZATION * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        /** Lock fragment in portrait screen orientation */
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        /** Set fragment state in shared view model */
        sharedViewModel.setFragmentStateHolder(Constants.FRAGMENT_SETTINGS)

        /** Set name for shared preference */
        preferenceManager.sharedPreferencesName = Constants.APP

        /** Set resource for preference screen */
        setPreferencesFromResource(xml.preferences, rootKey)

        /** Update summary and intent data of some preferences*/
        updatePreferenceSummary()

    }

    /**
     * Called when a preference in the tree rooted
     * at this PreferenceScreen has been clicked.
     * */

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {

        /** Preference keys */
        val appInfo = resources.getString(string.preference_key_app_version)
        val sourceCode = resources.getString(string.preference_key_source_code)
        val privacy = resources.getString(string.preference_key_privacy)
        val twitter = resources.getString(string.preference_key_tumur)

        /** OnClick handler */
        when(preference?.key){
            sourceCode -> startCustomTab(Constants.SOURCE_CODE_URL)
            privacy -> startCustomTab(Constants.PRIVACY_URL)
            twitter -> startCustomTab(Constants.TWITTER_URL)
            appInfo -> findNavController().navigate(R.id.action_global_to_app_info_dialog)
        }

        return super.onPreferenceTreeClick(preference)
    }

    /** FUNCTIONS * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Set summary values
     * for app version
     * preferences key
     * */
    private fun updatePreferenceSummary(){

        /** Preference keys */
        val rate = resources.getString(string.preference_key_rate)
        val appVersion = resources.getString(string.preference_key_app_version)
        val theme = resources.getString(string.preference_key_theme_option)

        /** Preference screen */
        val pref = preferenceManager.preferenceScreen

        /** Custom summary and intent data */
        val version = context?.packageName?.let { context?.packageManager?.getPackageInfo(it, 0)?.versionName }
        val uri = Uri.parse("market://details?id=" + context?.packageName)
        // App version
        pref.findPreference<Preference>(appVersion)?.summary = version
        // Rate this app
        pref.findPreference<Preference>(rate)?.intent?.data = uri
        // Theme
        val themePref = pref.findPreference<Preference>(theme)
        themePref?.let {
            it.onPreferenceChangeListener = OnPreferenceChangeListener { _, newValue ->
                val themeOption = newValue as String
                ThemeHelper.applyTheme(themeOption)
                true
            }
        }
    }

    /**
     * Starts custom tab
     * as an new intent
     * */
    private fun startCustomTab(url: String?){

        url?.let {
            /** Chrome custom tab  */
            val builder = CustomTabsIntent.Builder().apply {
                this.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                this.setShowTitle(true)
            }
            builder.build().launchUrl(context, (Uri.parse(url)))
        }
    }
}