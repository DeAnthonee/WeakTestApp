package com.kingseptic.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kingseptic.domain.ServiceRequestValidator

/**
 * Business contact details. Edit the values in res/values/strings.xml
 * (the "Business details" block) rather than this class.
 */
class BusinessInfo(context: Context) {
    val name: String = context.getString(R.string.business_name)
    val tagline: String = context.getString(R.string.business_tagline)
    val phone: String = context.getString(R.string.business_phone)
    val phoneDisplay: String = ServiceRequestValidator.formatPhone(phone)
    val email: String = context.getString(R.string.business_email)
    val serviceArea: String = context.getString(R.string.business_service_area)
    val hours: String = context.getString(R.string.business_hours)
    val website: String = context.getString(R.string.business_website)

    fun dialIntent(): Intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))

    fun emailIntent(subject: String, body: String): Intent =
        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }

    fun websiteIntent(): Intent = Intent(Intent.ACTION_VIEW, Uri.parse(website))
}
