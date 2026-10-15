package com.example.util

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.CalculationResult
import com.example.data.model.MaterialItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

class GujaratiVoiceManager(context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentSection = MutableStateFlow<String?>(null)
    val currentSection: StateFlow<String?> = _currentSection.asStateFlow()

    private val _isGujaratiSupported = MutableStateFlow(true)
    val isGujaratiSupported: StateFlow<Boolean> = _isGujaratiSupported.asStateFlow()

    private var speechRate = 0.95f

    init {
        tts = TextToSpeech(appContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsEngine = tts ?: return
            val gujaratiLocale = Locale("gu", "IN")
            val result = ttsEngine.isLanguageAvailable(gujaratiLocale)

            if (result == TextToSpeech.LANG_AVAILABLE ||
                result == TextToSpeech.LANG_COUNTRY_AVAILABLE ||
                result == TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE
            ) {
                ttsEngine.language = gujaratiLocale
                _isGujaratiSupported.value = true
            } else {
                // Fallback to Hindi or Indian English if Gujarati specific TTS is not installed
                val hindiLocale = Locale("hi", "IN")
                if (ttsEngine.isLanguageAvailable(hindiLocale) >= TextToSpeech.LANG_AVAILABLE) {
                    ttsEngine.language = hindiLocale
                } else {
                    ttsEngine.language = Locale.getDefault()
                }
                _isGujaratiSupported.value = false
            }

            ttsEngine.setSpeechRate(speechRate)
            ttsEngine.setPitch(1.0f)

            ttsEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentSection.value = null
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _currentSection.value = null
                }
            })

            isInitialized = true
        }
    }

    fun speakBill(
        billNo: String,
        customerName: String,
        siteLocation: String,
        result: CalculationResult
    ) {
        if (!isInitialized) return
        stop()

        val cust = if (customerName.isNotBlank()) customerName else "ગ્રાહક"
        val loc = if (siteLocation.isNotBlank()) siteLocation else "ગુજરાત"
        val totalArea = result.totalBuiltupArea.roundToInt()
        val grandTotal = result.grandTotal.roundToInt()

        val sb = StringBuilder()
        sb.append("નમસ્કાર. આ છે ગ્રાહક $cust માટેનું અંદાજિત બાંધકામ બિલ. ")
        sb.append("સ્થળ: $loc. ")
        sb.append("કુલ બાંધકામ એરિયા: $totalArea ચોરસ ફૂટ, ${result.floors} માળ. ")
        if (result.constructionType == com.example.data.model.ConstructionType.BEAM_SLAB_CASTING) {
            sb.append("બાંધકામનો પ્રકાર: ફક્ત બીમ અને ધાબું કે સ્લેબ. દીવાલો તૈયાર હોવાથી ઈંટોનો ખર્ચ શૂન્ય છે. માત્ર બીમ અને સ્લેબ માટે જરૂરી સામગ્રી: ")
        } else {
            sb.append("બાંધકામનો પ્રકાર: ${result.constructionType.shortGu}. જરૂરી સામગ્રીની વિગત: ")
        }

        result.items.forEach { item ->
            val qty = FormatUtils.formatIndianNumber(item.quantity, item.quantity % 1.0 != 0.0)
            val cost = item.totalCost.roundToInt()
            sb.append("${item.nameGu}: $qty ${item.unitGu}, કુલ રકમ $cost રૂપિયા. ")
        }

        sb.append("કુલ અંદાજિત ખર્ચ થાય છે: $grandTotal રૂપિયા. ")
        sb.append("પ્રતિ ચોરસ ફૂટ અંદાજ: ${result.costPerSqFt.roundToInt()} રૂપિયા. આભાર.")

        val textToSpeak = sb.toString()
        _currentSection.value = "બિલ બોલાઈ રહ્યું છે..."

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "BILL_UTTERANCE_$billNo")
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "BILL_UTTERANCE_$billNo")
        _isSpeaking.value = true
    }

    fun speakItem(item: MaterialItem) {
        if (!isInitialized) return
        stop()

        val qty = FormatUtils.formatIndianNumber(item.quantity, item.quantity % 1.0 != 0.0)
        val rate = item.rate.roundToInt()
        val total = item.totalCost.roundToInt()

        val text = "${item.nameGu}. જરૂરી જથ્થો: $qty ${item.unitGu}. બજાર દર: $rate રૂપિયા. કુલ રકમ: $total રૂપિયા."
        _currentSection.value = item.nameGu

        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ITEM_UTTERANCE_${item.srNo}")
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "ITEM_UTTERANCE_${item.srNo}")
        _isSpeaking.value = true
    }

    fun setSpeed(rate: Float) {
        speechRate = rate.coerceIn(0.5f, 2.0f)
        tts?.setSpeechRate(speechRate)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _currentSection.value = null
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
