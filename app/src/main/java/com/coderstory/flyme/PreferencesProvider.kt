package com.coderstory.flyme

import com.coderstory.flyme.preferences.PreferencesProvider

class PreferencesProvider : PreferencesProvider() {
    override val authorities: String
        get() = "com.coderstory.flyme.PreferencesProvider"
}