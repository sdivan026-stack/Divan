package com.example.data.repository

import com.example.data.model.CalculationResult
import com.example.data.model.ConstructionType
import com.example.data.model.MaterialItem
import com.example.data.model.MaterialRates
import java.util.Locale
import kotlin.math.roundToInt

object CalculationEngine {

    fun calculate(
        baseAreaSqFt: Double,
        floors: Int,
        type: ConstructionType,
        rates: MaterialRates
    ): CalculationResult {
        val totalArea = (baseAreaSqFt * floors).coerceAtLeast(0.0)
        val items = mutableListOf<MaterialItem>()
        var srNo = 1

        val cementBags = totalArea * type.cementFactor
        val sandCft = totalArea * type.sandFactor
        val aggregateCft = totalArea * type.aggregateFactor
        val steelKg = totalArea * type.steelFactor
        val bricksPieces = totalArea * type.bricksFactor
        val laborAmount = totalArea * (rates.laborSqft * type.laborFactor)
        val miscAmount = totalArea * type.miscFactor

        // 1. Cement
        if (cementBags > 0) {
            val total = cementBags * rates.cement
            items.add(
                MaterialItem(
                    srNo = srNo++,
                    nameGu = "સીમેન્ટ (Cement Bags)",
                    nameEn = "Cement",
                    quantity = cementBags,
                    unitGu = "બેગ",
                    unitEn = "Bags",
                    rate = rates.cement,
                    rateUnitGu = "₹ / બેગ",
                    totalCost = total,
                    secondaryInfo = "~${(cementBags * 50.0 / 1000.0).roundTo(2)} ટન (50 kg બેગ)"
                )
            )
        }

        // 2. Sand
        if (sandCft > 0) {
            val total = sandCft * rates.sand
            val brass = (sandCft / 100.0).roundTo(2)
            items.add(
                MaterialItem(
                    srNo = srNo++,
                    nameGu = "રેતી / વાસરી (River Sand)",
                    nameEn = "Sand",
                    quantity = sandCft,
                    unitGu = "CFT",
                    unitEn = "CFT",
                    rate = rates.sand,
                    rateUnitGu = "₹ / CFT",
                    totalCost = total,
                    secondaryInfo = "લગભગ $brass બ્રાસ (1 બ્રાસ = 100 CFT)"
                )
            )
        }

        // 3. Kapchi / Aggregate
        if (aggregateCft > 0) {
            val total = aggregateCft * rates.aggregate
            val brass = (aggregateCft / 100.0).roundTo(2)
            items.add(
                MaterialItem(
                    srNo = srNo++,
                    nameGu = "કપચી / ગિટ્ટી (Aggregate)",
                    nameEn = "Aggregate",
                    quantity = aggregateCft,
                    unitGu = "CFT",
                    unitEn = "CFT",
                    rate = rates.aggregate,
                    rateUnitGu = "₹ / CFT",
                    totalCost = total,
                    secondaryInfo = "લગભગ $brass બ્રાસ"
                )
            )
        }

        // 4. Steel / TMT Bars
        if (steelKg > 0) {
            val total = steelKg * rates.steelKg
            val tonnes = (steelKg / 1000.0).roundTo(2)
            items.add(
                MaterialItem(
                    srNo = srNo++,
                    nameGu = "સળિયા / સ્ટીલ (TMT Steel Bars)",
                    nameEn = "Steel TMT",
                    quantity = steelKg,
                    unitGu = "કિલો",
                    unitEn = "Kg",
                    rate = rates.steelKg,
                    rateUnitGu = "₹ / કિલો",
                    totalCost = total,
                    secondaryInfo = "લગભગ $tonnes મેટ્રિક ટન"
                )
            )
        }

        // 5. Red Bricks
        if (bricksPieces > 0) {
            val total = bricksPieces * rates.brick
            items.add(
                MaterialItem(
                    srNo = srNo++,
                    nameGu = "ઈંટો / રેડ બ્રિક્સ (Red Bricks)",
                    nameEn = "Bricks",
                    quantity = bricksPieces,
                    unitGu = "નગ",
                    unitEn = "Nos",
                    rate = rates.brick,
                    rateUnitGu = "₹ / નગ",
                    totalCost = total,
                    secondaryInfo = "${(bricksPieces / 1000.0).roundTo(1)} હજાર નગ"
                )
            )
        }

        // 6. Labor Contract
        if (totalArea > 0 && type.laborFactor > 0) {
            val effectiveRate = rates.laborSqft * type.laborFactor
            items.add(
                MaterialItem(
                    srNo = srNo++,
                    nameGu = "મજૂરી / લેબર કોન્ટ્રાક્ટ (Labor)",
                    nameEn = "Labor Contract",
                    quantity = totalArea,
                    unitGu = "ચો.ફૂટ",
                    unitEn = "SqFt",
                    rate = effectiveRate,
                    rateUnitGu = "₹ / ચો.ફૂટ",
                    totalCost = laborAmount,
                    secondaryInfo = "ફેક્ટર: ${(type.laborFactor * 100).roundToInt()}%"
                )
            )
        }

        // 7. Shuttering & Misc
        if (totalArea > 0 && type.miscFactor > 0) {
            items.add(
                MaterialItem(
                    srNo = srNo++,
                    nameGu = "પરચુરણ ખર્ચ (શટરિંગ, પાણી ક્યુરિંગ)",
                    nameEn = "Shuttering & Misc",
                    quantity = totalArea,
                    unitGu = "ચો.ફૂટ",
                    unitEn = "SqFt",
                    rate = type.miscFactor,
                    rateUnitGu = "₹ / ચો.ફૂટ",
                    totalCost = miscAmount,
                    secondaryInfo = "શટરિંગ સામાન, પાણી ટેસ્ટિંગ"
                )
            )
        }

        val grandTotal = items.sumOf { it.totalCost }
        val costPerSqFt = if (totalArea > 0) grandTotal / totalArea else 0.0

        return CalculationResult(
            baseAreaSqFt = baseAreaSqFt,
            floors = floors,
            totalBuiltupArea = totalArea,
            constructionType = type,
            rates = rates,
            items = items,
            grandTotal = grandTotal,
            costPerSqFt = costPerSqFt
        )
    }

    private fun Double.roundTo(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (this * multiplier).roundToInt() / multiplier
    }
}
