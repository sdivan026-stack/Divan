package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.EstimateDao
import com.example.data.local.EstimateEntity
import com.example.data.model.ContractorProfile
import com.example.data.model.MaterialRates
import kotlinx.coroutines.flow.Flow

class EstimateRepository(
    private val dao: EstimateDao,
    private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("bandhkam_contractor_prefs", Context.MODE_PRIVATE)

    fun getAllEstimates(): Flow<List<EstimateEntity>> = dao.getAllEstimates()

    fun searchEstimates(query: String): Flow<List<EstimateEntity>> = dao.searchEstimates(query)

    suspend fun getEstimateById(id: Long): EstimateEntity? = dao.getEstimateById(id)

    suspend fun saveEstimate(estimate: EstimateEntity): Long = dao.insertEstimate(estimate)

    suspend fun deleteEstimate(estimate: EstimateEntity) = dao.deleteEstimate(estimate)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    // Contractor profile preferences
    fun getContractorProfile(): ContractorProfile {
        return ContractorProfile(
            firmName = prefs.getString("firm_name", "શ્રી ગણેશ કન્સ્ટ્રક્શન") ?: "શ્રી ગણેશ કન્સ્ટ્રક્શન",
            proprietorName = prefs.getString("proprietor_name", "સિવિલ કોન્ટ્રાક્ટર") ?: "સિવિલ કોન્ટ્રાક્ટર",
            phone = prefs.getString("phone", "+91 98765 43210") ?: "+91 98765 43210",
            location = prefs.getString("location", "અમદાવાદ, ગુજરાત") ?: "અમદાવાદ, ગુજરાત",
            tagline = prefs.getString("tagline", "વિશ્વાસપાત્ર અને ગુણવત્તાયુક્ત બાંધકામ") ?: "વિશ્વાસપાત્ર અને ગુણવત્તાયુક્ત બાંધકામ"
        )
    }

    fun saveContractorProfile(profile: ContractorProfile) {
        prefs.edit()
            .putString("firm_name", profile.firmName)
            .putString("proprietor_name", profile.proprietorName)
            .putString("phone", profile.phone)
            .putString("location", profile.location)
            .putString("tagline", profile.tagline)
            .apply()
    }

    // Default rates preference
    fun getSavedDefaultRates(): MaterialRates {
        return MaterialRates(
            cement = prefs.getFloat("rate_cement", 370f).toDouble(),
            sand = prefs.getFloat("rate_sand", 38f).toDouble(),
            aggregate = prefs.getFloat("rate_aggregate", 32f).toDouble(),
            steelKg = prefs.getFloat("rate_steel", 68f).toDouble(),
            brick = prefs.getFloat("rate_brick", 7.5f).toDouble(),
            laborSqft = prefs.getFloat("rate_labor", 250f).toDouble(),
            miscFactor = prefs.getFloat("rate_misc", 45f).toDouble()
        )
    }

    fun saveDefaultRates(rates: MaterialRates) {
        prefs.edit()
            .putFloat("rate_cement", rates.cement.toFloat())
            .putFloat("rate_sand", rates.sand.toFloat())
            .putFloat("rate_aggregate", rates.aggregate.toFloat())
            .putFloat("rate_steel", rates.steelKg.toFloat())
            .putFloat("rate_brick", rates.brick.toFloat())
            .putFloat("rate_labor", rates.laborSqft.toFloat())
            .putFloat("rate_misc", rates.miscFactor.toFloat())
            .apply()
    }
}
