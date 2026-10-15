package com.example.util

import com.example.data.model.CalculationResult
import com.example.data.model.ContractorProfile
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object FormatUtils {

    /**
     * Formats number in Indian Rupee comma system (e.g. 12,34,567)
     */
    fun formatIndianNumber(value: Double, includeDecimals: Boolean = false): String {
        val longVal = value.toLong()
        val formatted = try {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "IN")) as DecimalFormat
            val dfs = format.decimalFormatSymbols
            dfs.currencySymbol = ""
            format.decimalFormatSymbols = dfs
            if (!includeDecimals) {
                format.maximumFractionDigits = 0
            } else {
                format.maximumFractionDigits = 2
            }
            format.format(value).trim()
        } catch (_: Exception) {
            String.format(Locale.getDefault(), "%,d", longVal)
        }
        return formatted
    }

    fun formatCurrency(amount: Double): String {
        return "₹ " + formatIndianNumber(amount, false)
    }

    fun formatDate(timestamp: Long = System.currentTimeMillis()): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun generateBillNumber(): String {
        val randomNum = Random.nextInt(100, 999)
        return "GUJ-2026-$randomNum"
    }

    fun generateWhatsAppSummary(
        billNo: String,
        customerName: String,
        customerPhone: String,
        siteLocation: String,
        result: CalculationResult,
        profile: ContractorProfile
    ): String {
        val sb = StringBuilder()
        sb.append("🏗️ *મકાન બાંધકામ અંદાજિત બિલ સમરી*\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("📋 *બિલ નંબર:* $billNo\n")
        sb.append("📅 *તારીખ:* ${formatDate()}\n")
        sb.append("👤 *ગ્રાહક:* $customerName\n")
        if (customerPhone.isNotBlank()) {
            sb.append("📞 *સંપર્ક:* $customerPhone\n")
        }
        sb.append("📍 *સ્થળ:* $siteLocation\n")
        sb.append("📐 *કુલ બાંધકામ:* ${formatIndianNumber(result.baseAreaSqFt)} sqft (માળ: ${result.floors}, કુલ: ${formatIndianNumber(result.totalBuiltupArea)} sqft)\n")
        sb.append("🏢 *બાંધકામ પ્રકાર:* ${result.constructionType.shortGu}\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("📦 *મટિરિયલ અને ખર્ચ અંદાજ:*\n")

        result.items.forEachIndexed { index, item ->
            val qtyStr = formatIndianNumber(item.quantity, item.quantity % 1.0 != 0.0)
            sb.append("${index + 1}. *${item.nameGu}:* $qtyStr ${item.unitGu} × ₹${formatIndianNumber(item.rate, true)} = *₹${formatIndianNumber(item.totalCost)}*\n")
            if (item.secondaryInfo != null) {
                sb.append("    └ (${item.secondaryInfo})\n")
            }
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("💰 *કુલ અંદાજિત રકમ:* *₹${formatIndianNumber(result.grandTotal)}*\n")
        sb.append("📊 *દર ચોરસ ફૂટ અંદાજ:* ₹${formatIndianNumber(result.costPerSqFt)} / sqft\n\n")
        sb.append("📌 *નોંધ:* આ બિલ સિવિલ માનક ગુણાંક પર આધારિત અંદાજિત પત્રક છે.\n\n")
        sb.append("🏢 *સંપર્ક:* ${profile.firmName}\n")
        sb.append("📱 ફોન: ${profile.phone} | ${profile.location}\n")

        return sb.toString()
    }
}
