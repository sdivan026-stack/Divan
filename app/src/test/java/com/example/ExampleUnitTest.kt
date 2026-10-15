package com.example

import com.example.data.model.ConstructionType
import com.example.data.model.MaterialRates
import com.example.data.repository.CalculationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testFullStructureCalculation() {
        // 1000 sqft base * 2 floors = 2000 total sqft
        val result = CalculationEngine.calculate(
            baseAreaSqFt = 1000.0,
            floors = 2,
            type = ConstructionType.FULL_STRUCTURE,
            rates = MaterialRates.DEFAULT
        )

        assertEquals(2000.0, result.totalBuiltupArea, 0.01)

        // Cement: 2000 * 0.35 = 700 bags
        val cementItem = result.items.find { it.nameGu.contains("સીમેન્ટ") }
        assertEquals(700.0, cementItem?.quantity ?: 0.0, 0.01)

        // Steel: 2000 * 3.5 = 7000 kg
        val steelItem = result.items.find { it.nameGu.contains("સળિયા") }
        assertEquals(7000.0, steelItem?.quantity ?: 0.0, 0.01)

        // Bricks must be present for full structure: 2000 * 15 = 30000 bricks
        val brickItem = result.items.find { it.nameGu.contains("ઈંટો") }
        assertEquals(30000.0, brickItem?.quantity ?: 0.0, 0.01)

        assertTrue(result.grandTotal > 0)
    }

    @Test
    fun testBeamAndSlabCastingCalculation() {
        // 1000 sqft base * 1 floor = 1000 total sqft
        val result = CalculationEngine.calculate(
            baseAreaSqFt = 1000.0,
            floors = 1,
            type = ConstructionType.BEAM_SLAB_CASTING,
            rates = MaterialRates.DEFAULT
        )

        assertEquals(1000.0, result.totalBuiltupArea, 0.01)

        // Bricks MUST be zero / excluded because walls are already built
        val brickItem = result.items.find { it.nameGu.contains("ઈંટો") }
        assertNull(brickItem)

        // Cement for beam & slab: 1000 * 0.20 = 200 bags
        val cementItem = result.items.find { it.nameGu.contains("સીમેન્ટ") }
        assertEquals(200.0, cementItem?.quantity ?: 0.0, 0.01)

        // Steel for beam & slab: 1000 * 3.0 = 3000 kg
        val steelItem = result.items.find { it.nameGu.contains("સળિયા") }
        assertEquals(3000.0, steelItem?.quantity ?: 0.0, 0.01)

        assertTrue(result.grandTotal > 0)
    }
}
