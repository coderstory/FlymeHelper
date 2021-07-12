/*
 * Copyright 2013 Philip Schiffer
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.coderstory.flyme.tools.licensesdialog

import com.coderstory.flyme.tools.licensesdialog.licenses.ApacheSoftwareLicense20
import com.coderstory.flyme.tools.licensesdialog.licenses.GnuGeneralPublicLicense20
import com.coderstory.flyme.tools.licensesdialog.licenses.License
import java.util.*

object LicenseResolver {
    private const val INITIAL_LICENSES_COUNT = 4
    private val sLicenses: Map<String, License> = HashMap(LicenseResolver.INITIAL_LICENSES_COUNT)
    fun registerDefaultLicenses() {
        LicenseResolver.sLicenses.clear()
        LicenseResolver.registerLicense(ApacheSoftwareLicense20())
        LicenseResolver.registerLicense(GnuGeneralPublicLicense20())
    }

    /**
     * Register an additional license.
     *
     * @param license the license to register
     */
    fun registerLicense(license: License) {
        LicenseResolver.sLicenses[license.name] = license
    }

    /**
     * Get a license by name
     *
     * @param license license name
     * @return License
     * @throws IllegalStateException when unknown license is requested
     */
    fun read(license: String): License? {
        val trimmedLicense = license.trim { it <= ' ' }
        return if (LicenseResolver.sLicenses.containsKey(trimmedLicense)) {
            LicenseResolver.sLicenses[trimmedLicense]
        } else {
            throw IllegalStateException(String.format("no such license available: %s, did you forget to register it?", trimmedLicense))
        }
    }

    init {
        LicenseResolver.registerDefaultLicenses()
    }
}