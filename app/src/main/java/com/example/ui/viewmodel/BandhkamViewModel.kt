package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.EstimateEntity
import com.example.data.model.CalculationResult
import com.example.data.model.ConstructionType
import com.example.data.model.ContractorProfile
import com.example.data.model.MaterialRates
import com.example.data.repository.CalculationEngine
import com.example.data.repository.EstimateRepository
import com.example.data.model.MaterialItem
import com.example.util.GujaratiVoiceManager
import com.example.util.FormatUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val customerName: String = "રમેશભાઈ પટેલ",
    val customerPhone: String = "",
    val siteLocation: String = "અમદાવાદ, ગુજરાત",
    val baseAreaText: String = "1000",
    val floors: Int = 2,
    val constructionType: ConstructionType = ConstructionType.FULL_STRUCTURE,
    val rates: MaterialRates = MaterialRates.DEFAULT,
    val billNumber: String = FormatUtils.generateBillNumber(),
    val notes: String = "",
    val activeSavedEstimateId: Long? = null
)

class BandhkamViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EstimateRepository
    val voiceManager = GujaratiVoiceManager(application)

    val isSpeaking: StateFlow<Boolean> = voiceManager.isSpeaking
    val currentSpeechSection: StateFlow<String?> = voiceManager.currentSection
    val isGujaratiSupported: StateFlow<Boolean> = voiceManager.isGujaratiSupported

    init {
        val db = AppDatabase.getDatabase(application)
        repository = EstimateRepository(db.estimateDao(), application)
    }

    private val _uiState = MutableStateFlow(
        CalculatorUiState(
            rates = repository.getSavedDefaultRates(),
            billNumber = FormatUtils.generateBillNumber()
        )
    )
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _contractorProfile = MutableStateFlow(repository.getContractorProfile())
    val contractorProfile: StateFlow<ContractorProfile> = _contractorProfile.asStateFlow()

    val savedEstimates: StateFlow<List<EstimateEntity>> = repository.getAllEstimates()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Real-time calculation derived from current uiState
    val calculationResult: StateFlow<CalculationResult> = MutableStateFlow(computeResult(_uiState.value)).apply {
        // Updated whenever _uiState changes
    }

    private fun computeResult(state: CalculatorUiState): CalculationResult {
        val baseArea = state.baseAreaText.toDoubleOrNull() ?: 0.0
        return CalculationEngine.calculate(
            baseAreaSqFt = baseArea,
            floors = state.floors,
            type = state.constructionType,
            rates = state.rates
        )
    }

    fun getCalculation(): CalculationResult {
        return computeResult(_uiState.value)
    }

    fun setCustomerName(name: String) {
        _uiState.value = _uiState.value.copy(customerName = name)
    }

    fun setCustomerPhone(phone: String) {
        _uiState.value = _uiState.value.copy(customerPhone = phone)
    }

    fun setSiteLocation(location: String) {
        _uiState.value = _uiState.value.copy(siteLocation = location)
    }

    fun setBaseArea(area: String) {
        _uiState.value = _uiState.value.copy(baseAreaText = area)
    }

    fun setFloors(floors: Int) {
        _uiState.value = _uiState.value.copy(floors = floors.coerceIn(1, 10))
    }

    fun setConstructionType(type: ConstructionType) {
        _uiState.value = _uiState.value.copy(constructionType = type)
    }

    fun updateCementRate(rate: Double) {
        val cur = _uiState.value.rates
        _uiState.value = _uiState.value.copy(rates = cur.copy(cement = rate))
    }

    fun updateSandRate(rate: Double) {
        val cur = _uiState.value.rates
        _uiState.value = _uiState.value.copy(rates = cur.copy(sand = rate))
    }

    fun updateAggregateRate(rate: Double) {
        val cur = _uiState.value.rates
        _uiState.value = _uiState.value.copy(rates = cur.copy(aggregate = rate))
    }

    fun updateSteelRate(rate: Double) {
        val cur = _uiState.value.rates
        _uiState.value = _uiState.value.copy(rates = cur.copy(steelKg = rate))
    }

    fun updateBrickRate(rate: Double) {
        val cur = _uiState.value.rates
        _uiState.value = _uiState.value.copy(rates = cur.copy(brick = rate))
    }

    fun updateLaborRate(rate: Double) {
        val cur = _uiState.value.rates
        _uiState.value = _uiState.value.copy(rates = cur.copy(laborSqft = rate))
    }

    fun resetRatesToDefault() {
        val defaultRates = repository.getSavedDefaultRates()
        _uiState.value = _uiState.value.copy(rates = defaultRates)
    }

    fun generateNewBill() {
        _uiState.value = _uiState.value.copy(
            billNumber = FormatUtils.generateBillNumber(),
            activeSavedEstimateId = null
        )
    }

    fun saveEstimate(onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val state = _uiState.value
            val calc = computeResult(state)
            val entity = EstimateEntity(
                id = state.activeSavedEstimateId ?: 0L,
                billNumber = state.billNumber,
                customerName = state.customerName.ifBlank { "ગ્રાહક" },
                customerPhone = state.customerPhone,
                siteLocation = state.siteLocation.ifBlank { "ગુજરાત" },
                baseAreaSqFt = state.baseAreaText.toDoubleOrNull() ?: 0.0,
                floors = state.floors,
                constructionTypeId = state.constructionType.id,
                grandTotal = calc.grandTotal,
                rateCement = state.rates.cement,
                rateSand = state.rates.sand,
                rateAggregate = state.rates.aggregate,
                rateSteelKg = state.rates.steelKg,
                rateBrick = state.rates.brick,
                rateLaborSqft = state.rates.laborSqft,
                notes = state.notes
            )
            val id = repository.saveEstimate(entity)
            _uiState.value = _uiState.value.copy(activeSavedEstimateId = id)
            onSaved(id)
        }
    }

    fun loadEstimate(estimate: EstimateEntity) {
        _uiState.value = CalculatorUiState(
            customerName = estimate.customerName,
            customerPhone = estimate.customerPhone,
            siteLocation = estimate.siteLocation,
            baseAreaText = if (estimate.baseAreaSqFt % 1.0 == 0.0) estimate.baseAreaSqFt.toInt().toString() else estimate.baseAreaSqFt.toString(),
            floors = estimate.floors,
            constructionType = ConstructionType.fromId(estimate.constructionTypeId),
            rates = MaterialRates(
                cement = estimate.rateCement,
                sand = estimate.rateSand,
                aggregate = estimate.rateAggregate,
                steelKg = estimate.rateSteelKg,
                brick = estimate.rateBrick,
                laborSqft = estimate.rateLaborSqft
            ),
            billNumber = estimate.billNumber,
            notes = estimate.notes,
            activeSavedEstimateId = estimate.id
        )
    }

    fun deleteEstimate(estimate: EstimateEntity) {
        viewModelScope.launch {
            repository.deleteEstimate(estimate)
        }
    }

    fun updateProfile(profile: ContractorProfile) {
        repository.saveContractorProfile(profile)
        _contractorProfile.value = profile
    }

    fun saveAsDefaultRates(rates: MaterialRates) {
        repository.saveDefaultRates(rates)
    }

    fun speakCurrentBill() {
        val state = _uiState.value
        val calc = computeResult(state)
        voiceManager.speakBill(
            billNo = state.billNumber,
            customerName = state.customerName,
            siteLocation = state.siteLocation,
            result = calc
        )
    }

    fun speakItem(item: MaterialItem) {
        voiceManager.speakItem(item)
    }

    fun stopSpeaking() {
        voiceManager.stop()
    }

    fun setVoiceSpeed(speed: Float) {
        voiceManager.setSpeed(speed)
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
    }
}
