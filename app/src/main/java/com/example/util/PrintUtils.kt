package com.example.util

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.data.model.CalculationResult
import com.example.data.model.ContractorProfile

object PrintUtils {

    fun generateHtmlInvoice(
        billNo: String,
        customerName: String,
        customerPhone: String,
        siteLocation: String,
        result: CalculationResult,
        profile: ContractorProfile,
        dateStr: String = FormatUtils.formatDate()
    ): String {
        val rowsBuilder = StringBuilder()
        result.items.forEach { item ->
            val qtyStr = FormatUtils.formatIndianNumber(item.quantity, item.quantity % 1.0 != 0.0)
            val rateStr = FormatUtils.formatIndianNumber(item.rate, item.rate % 1.0 != 0.0)
            val costStr = FormatUtils.formatIndianNumber(item.totalCost, false)
            val extra = if (item.secondaryInfo != null) "<br><small style='color:#64748b;'>${item.secondaryInfo}</small>" else ""
            rowsBuilder.append(
                """
                <tr>
                    <td style="padding: 8px 10px; border-bottom: 1px solid #e2e8f0; text-align: center;">${item.srNo}</td>
                    <td style="padding: 8px 10px; border-bottom: 1px solid #e2e8f0; font-weight: 600; color: #1e293b;">
                        ${item.nameGu}$extra
                    </td>
                    <td style="padding: 8px 10px; border-bottom: 1px solid #e2e8f0; text-align: right; font-weight: 500;">
                        $qtyStr ${item.unitGu}
                    </td>
                    <td style="padding: 8px 10px; border-bottom: 1px solid #e2e8f0; text-align: right;">
                        ₹$rateStr / ${item.unitGu}
                    </td>
                    <td style="padding: 8px 10px; border-bottom: 1px solid #e2e8f0; text-align: right; font-weight: bold; color: #047857;">
                        ₹$costStr
                    </td>
                </tr>
                """.trimIndent()
            )
        }

        return """
        <!DOCTYPE html>
        <html lang="gu">
        <head>
            <meta charset="UTF-8">
            <title>બાંધકામ બિલ - $billNo</title>
            <style>
                body {
                    font-family: 'Noto Sans Gujarati', 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                    margin: 0;
                    padding: 24px;
                    color: #0f172a;
                    background: #ffffff;
                }
                .header-table {
                    width: 100%;
                    border-bottom: 3px solid #047857;
                    padding-bottom: 16px;
                    margin-bottom: 16px;
                }
                .firm-title {
                    font-size: 22px;
                    font-weight: bold;
                    color: #047857;
                    margin: 0;
                }
                .firm-sub {
                    font-size: 13px;
                    color: #475569;
                    margin: 4px 0 0 0;
                }
                .bill-badge {
                    background: #ecfdf5;
                    border: 1px solid #a7f3d0;
                    color: #065f46;
                    padding: 6px 12px;
                    border-radius: 8px;
                    font-weight: bold;
                    font-size: 13px;
                    display: inline-block;
                }
                .info-box {
                    background: #f8fafc;
                    border: 1px solid #e2e8f0;
                    border-radius: 10px;
                    padding: 12px 16px;
                    margin-bottom: 20px;
                    display: flex;
                    justify-content: space-between;
                }
                .info-col {
                    display: inline-block;
                    width: 24%;
                    vertical-align: top;
                }
                .info-label {
                    font-size: 11px;
                    color: #64748b;
                    text-transform: uppercase;
                    display: block;
                    margin-bottom: 2px;
                }
                .info-val {
                    font-size: 13px;
                    font-weight: bold;
                    color: #1e293b;
                }
                table.data-table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-bottom: 24px;
                }
                th {
                    background: #f1f5f9;
                    color: #334155;
                    font-size: 12px;
                    font-weight: bold;
                    padding: 10px;
                    border-bottom: 2px solid #cbd5e1;
                    text-align: left;
                }
                .total-card {
                    background: #ecfdf5;
                    border: 2px solid #a7f3d0;
                    border-radius: 10px;
                    padding: 16px 20px;
                    text-align: right;
                    margin-bottom: 24px;
                }
                .total-label {
                    font-size: 12px;
                    color: #065f46;
                    font-weight: bold;
                    text-transform: uppercase;
                }
                .total-amount {
                    font-size: 26px;
                    font-weight: 800;
                    color: #047857;
                    margin-top: 4px;
                }
                .notes {
                    font-size: 11px;
                    color: #64748b;
                    line-height: 1.6;
                    border-top: 1px solid #e2e8f0;
                    padding-top: 12px;
                    margin-bottom: 36px;
                }
                .signatures {
                    margin-top: 40px;
                    display: flex;
                    justify-content: space-between;
                }
                .sig-box {
                    display: inline-block;
                    width: 45%;
                    font-size: 12px;
                    color: #334155;
                }
            </style>
        </head>
        <body>
            <table class="header-table">
                <tr>
                    <td>
                        <h1 class="firm-title">${profile.firmName}</h1>
                        <p class="firm-sub">${profile.proprietorName} • ${profile.phone} • ${profile.location}</p>
                        <p style="font-size: 12px; color: #059669; margin: 4px 0 0 0;">${profile.tagline}</p>
                    </td>
                    <td style="text-align: right; vertical-align: top;">
                        <div class="bill-badge">બિલ નં: $billNo</div>
                        <div style="font-size: 12px; color: #64748b; margin-top: 6px;">તારીખ: $dateStr</div>
                    </td>
                </tr>
            </table>

            <div class="info-box">
                <div class="info-col">
                    <span class="info-label">ગ્રાહકનું નામ</span>
                    <span class="info-val">$customerName</span>
                    ${if (customerPhone.isNotBlank()) "<br><small style='color:#64748b;'>$customerPhone</small>" else ""}
                </div>
                <div class="info-col">
                    <span class="info-label">સાઇટનું સ્થળ</span>
                    <span class="info-val">$siteLocation</span>
                </div>
                <div class="info-col">
                    <span class="info-label">બાંધકામ માપ</span>
                    <span class="info-val" style="color: #047857;">${FormatUtils.formatIndianNumber(result.baseAreaSqFt)} sqft (${result.floors} માળ)</span>
                    <br><small style="color:#64748b;">કુલ: ${FormatUtils.formatIndianNumber(result.totalBuiltupArea)} sqft</small>
                </div>
                <div class="info-col">
                    <span class="info-label">બાંધકામ પ્રકાર</span>
                    <span class="info-val">${result.constructionType.shortGu}</span>
                </div>
            </div>

            <table class="data-table">
                <thead>
                    <tr>
                        <th style="width: 8%; text-align: center;">ક્રમ</th>
                        <th style="width: 44%;">સામગ્રી / કામગીરીની વિગત</th>
                        <th style="width: 18%; text-align: right;">જરૂરી જથ્થો</th>
                        <th style="width: 15%; text-align: right;">બજાર દર</th>
                        <th style="width: 15%; text-align: right;">અંદાજિત રકમ</th>
                    </tr>
                </thead>
                <tbody>
                    $rowsBuilder
                </tbody>
            </table>

            <div class="total-card">
                <div class="total-label">કુલ અંદાજિત રકમ (Grand Total)</div>
                <div class="total-amount">${FormatUtils.formatCurrency(result.grandTotal)}</div>
                <div style="font-size: 12px; color: #065f46; margin-top: 4px;">
                    અંદાજિત બાંધકામ ખર્ચ: ₹${FormatUtils.formatIndianNumber(result.costPerSqFt)} પ્રતિ ચોરસ ફૂટ
                </div>
            </div>

            <div class="notes">
                <strong>* નોંધ (Terms & Conditions):</strong><br>
                ૧. આ બિલ બાંધકામ ક્ષેત્રના સ્ટાન્ડર્ડ સિવિલ એન્જિનિયરિંગ ગુણાંક (IS Factor) પર આધારિત અંદાજિત પત્રક છે.<br>
                ૨. મટિરિયલના બજાર ભાવ અને લોકલ ટ્રાન્સપોર્ટ વહન ખર્ચ મુજબ દરોમાં ફેરફાર થઈ શકે છે.<br>
                ૩. આ અંદાજ ૩૦ દિવસ સુધી માન્ય ગણાશે.
            </div>

            <table style="width: 100%; margin-top: 30px;">
                <tr>
                    <td style="width: 50%; font-size: 12px; color: #475569;">
                        ગ્રાહકની સહી:<br><br><br>
                        ___________________________
                    </td>
                    <td style="width: 50%; text-align: right; font-size: 12px; color: #475569;">
                        કોન્ટ્રાક્ટર / એન્જિનિયરની સહી:<br><br><br>
                        ___________________________
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """.trimIndent()
    }

    fun printBill(
        context: Context,
        billNo: String,
        customerName: String,
        customerPhone: String,
        siteLocation: String,
        result: CalculationResult,
        profile: ContractorProfile
    ) {
        val htmlContent = generateHtmlInvoice(
            billNo = billNo,
            customerName = customerName,
            customerPhone = customerPhone,
            siteLocation = siteLocation,
            result = result,
            profile = profile
        )

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
                val printAdapter = webView.createPrintDocumentAdapter("બાંધકામ_બિલ_$billNo")
                val printJobName = "Bandhkam_Bill_$billNo"
                printManager?.print(printJobName, printAdapter, PrintAttributes.Builder().build())
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }
}
