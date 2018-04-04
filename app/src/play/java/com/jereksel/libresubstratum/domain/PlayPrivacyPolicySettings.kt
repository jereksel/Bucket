package com.jereksel.libresubstratum.domain

import javax.inject.Inject

class PlayPrivacyPolicySettings @Inject constructor(): PrivacyPolicySettings {
    override fun isPrivacyPolicyRequired() = true
}

