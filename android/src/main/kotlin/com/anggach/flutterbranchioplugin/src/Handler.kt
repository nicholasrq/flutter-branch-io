package com.anggach.flutterbranchioplugin.src

import android.content.Intent
import com.anggach.flutterbranchioplugin.FlutterBranchIoPlugin
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.util.BranchEvent
import io.branch.referral.util.LinkProperties
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.MethodChannel.Result
import org.json.JSONObject

private fun generateLink(registrar: PluginRegistry.Registrar, buo: BranchUniversalObject, lp: LinkProperties, result: Result) {
    buo.generateShortUrl(registrar.activeContext(), lp) { link, error ->
        if (error == null) {
            result.success(link)
        } else {
            result.error(error.errorCode.toString(), error.message, "")
        }
    }
}

fun getBranchLatestParam(): String {
    val params = Branch.getInstance().latestReferringParams
    if (params != null)
        return params.toString()
    return ""
}



fun getBranchFirstParam(): String {
    val params = Branch.getInstance().firstReferringParams
    if (params != null)
        return params.toString()
    return ""
}

fun generateLinkHandler(registrar: PluginRegistry.Registrar, call: MethodCall, result: Result) {
    val buoJson = JSONObject(call.argument<String>("buoJson"))
    val buo = BranchUniversalObject.createInstance(buoJson)
    val lp = LinkProperties()
    val channel = call.argument<String>("lp_channel")
    if (channel != null) lp.channel = channel
    val feature = call.argument<String>("lp_feature")
    if (feature != null) lp.feature = feature
    val campaign = call.argument<String>("lp_campaign")
    if (campaign != null) lp.campaign = campaign
    val stage = call.argument<String>("lp_stage")
    if (stage != null) lp.stage = stage

    call.argument<Map<String, String>>("lp_control_params")?.forEach { t, u -> lp.addControlParameter(t, u) }
    generateLink(registrar, buo, lp, result)
}

fun listOnGoogleSearch(registrar: PluginRegistry.Registrar, call: MethodCall) {
    val buoJson = JSONObject(call.argument<String>("buoJson"))
    val buo = BranchUniversalObject.createInstance(buoJson)
    val lp = LinkProperties()
    val channel = call.argument<String>("lp_channel")
    if (channel != null) lp.channel = channel
    val feature = call.argument<String>("lp_feature")
    if (feature != null) lp.feature = feature
    val campaign = call.argument<String>("lp_campaign")
    if (campaign != null) lp.campaign = campaign
    val stage = call.argument<String>("lp_stage")
    if (stage != null) lp.stage = stage
    call.argument<Map<String, String>>("lp_control_params")?.forEach { t, u -> lp.addControlParameter(t, u) }
    buo.listOnGoogleSearch(registrar.activeContext(), lp)
}

fun trackContent(registrar: PluginRegistry.Registrar, call: MethodCall) {
    val buoJson = JSONObject(call.argument<String>("buoJson"))
    val buo = BranchUniversalObject.createInstance(buoJson)
    val eventType = call.argument<String>("eventType")
    BranchEvent(eventType).addContentItems(buo).logEvent(registrar.activeContext())
}

fun setUserID(call: MethodCall) {
    val userId = call.argument<String>("userId")
    Branch.getInstance().setIdentity(userId ?: "")
}

fun clearUserID() {
    Branch.getInstance().logout()
}