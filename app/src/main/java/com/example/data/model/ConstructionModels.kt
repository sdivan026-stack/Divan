package com.example.data.model

enum class ConstructionType(
    val id: String,
    val titleGu: String,
    val shortGu: String,
    val titleEn: String,
    val cementFactor: Double,
    val sandFactor: Double,
    val aggregateFactor: Double,
    val steelFactor: Double,
    val bricksFactor: Double,
    val laborFactor: Double,
    val miscFactor: Double
) {
    FULL_STRUCTURE(
        id = "full_structure",
        titleGu = "સંપૂર્ણ બાંધકામ (પાયાથી સ્લેબ સુધી)",
        shortGu = "સંપૂર્ણ બાંધકામ (પાયાથી સ્લેબ)",
        titleEn = "Full Structure",
        cementFactor = 0.35,
        sandFactor = 1.20,
        aggregateFactor = 1.00,
        steelFactor = 3.5,
        bricksFactor = 15.0,
        laborFactor = 1.0,
        miscFactor = 45.0
    ),
    BEAM_SLAB_CASTING(
        id = "beam_slab_casting",
        titleGu = "ફક્ત બીમ અને ધાબું/સ્લેબ (દીવાલો તૈયાર હોય ત્યારે)",
        shortGu = "બીમ અને ધાબું/સ્લેબ (દીવાલો તૈયાર)",
        titleEn = "Beam & Slab Casting (Walls Ready)",
        cementFactor = 0.20,
        sandFactor = 0.55,
        aggregateFactor = 0.80,
        steelFactor = 3.0,
        bricksFactor = 0.0, // Walls already made, exactly 0 bricks
        laborFactor = 0.55, // Shuttering, centering & slab labor
        miscFactor = 25.0
    ),
    SLAB_ONLY(
        id = "slab_only",
        titleGu = "માત્ર આરસીસી સ્લેબ / છત ભરાઈ (RCC Slab Only)",
        shortGu = "આરસીસી સ્લેબ",
        titleEn = "RCC Slab Only",
        cementFactor = 0.18,
        sandFactor = 0.50,
        aggregateFactor = 0.75,
        steelFactor = 2.8,
        bricksFactor = 0.0,
        laborFactor = 0.50,
        miscFactor = 20.0
    ),
    BRICK_PLASTER(
        id = "brick_plaster",
        titleGu = "માત્ર ઈંટ ચણતર અને પ્લાસ્ટર (Masonry & Plaster)",
        shortGu = "ઈંટ ચણતર + પ્લાસ્ટર",
        titleEn = "Masonry & Plaster",
        cementFactor = 0.12,
        sandFactor = 0.45,
        aggregateFactor = 0.05,
        steelFactor = 0.0,
        bricksFactor = 12.0,
        laborFactor = 0.35,
        miscFactor = 15.0
    ),
    FLOORING_FINISHING(
        id = "flooring_finishing",
        titleGu = "ફ્લોરિંગ અને ફિનિશિંગ કાર્ય (Flooring & Finishing)",
        shortGu = "ફ્લોરિંગ ફિનિશિંગ",
        titleEn = "Flooring & Finishing",
        cementFactor = 0.08,
        sandFactor = 0.30,
        aggregateFactor = 0.15,
        steelFactor = 0.0,
        bricksFactor = 0.0,
        laborFactor = 0.30,
        miscFactor = 10.0
    );

    companion object {
        fun fromId(id: String): ConstructionType {
            return entries.firstOrNull { it.id == id } ?: FULL_STRUCTURE
        }
    }
}

data class MaterialRates(
    val cement: Double = 370.0,       // ₹ / Bag
    val sand: Double = 38.0,          // ₹ / CFT
    val aggregate: Double = 32.0,     // ₹ / CFT
    val steelKg: Double = 68.0,       // ₹ / Kg
    val brick: Double = 7.50,         // ₹ / Piece
    val laborSqft: Double = 250.0,    // ₹ / SqFt
    val miscFactor: Double = 45.0     // ₹ / SqFt
) {
    companion object {
        val DEFAULT = MaterialRates()
    }
}

data class MaterialItem(
    val srNo: Int,
    val nameGu: String,
    val nameEn: String,
    val quantity: Double,
    val unitGu: String,
    val unitEn: String,
    val rate: Double,
    val rateUnitGu: String,
    val totalCost: Double,
    val secondaryInfo: String? = null
)

data class CalculationResult(
    val baseAreaSqFt: Double,
    val floors: Int,
    val totalBuiltupArea: Double,
    val constructionType: ConstructionType,
    val rates: MaterialRates,
    val items: List<MaterialItem>,
    val grandTotal: Double,
    val costPerSqFt: Double
)

data class ContractorProfile(
    val firmName: String = "શ્રી ગણેશ કન્સ્ટ્રક્શન",
    val proprietorName: String = "સિવિલ કોન્ટ્રાક્ટર",
    val phone: String = "+91 98765 43210",
    val location: String = "અમદાવાદ, ગુજરાત",
    val tagline: String = "વિશ્વાસપાત્ર અને ગુણવત્તાયુક્ત બાંધકામ સેવાઓ"
)
