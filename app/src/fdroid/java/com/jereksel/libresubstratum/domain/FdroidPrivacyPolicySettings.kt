package com.jereksel.libresubstratum.domain

import javax.inject.Inject

class FdroidPrivacyPolicySettings @Inject constructor(): PrivacyPolicySettings {
    override fun isPrivacyPolicyRequired() = false
}

