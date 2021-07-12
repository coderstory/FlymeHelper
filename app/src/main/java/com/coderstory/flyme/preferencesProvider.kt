package com.coderstory.flyme

import com.coderstory.flyme.preferences.PreferencesProvider

class preferencesProvider : PreferencesProvider() {
    override fun getAuthorities(): String {
        return "com.coderstory.flyme.preferencesProvider"
    }
}