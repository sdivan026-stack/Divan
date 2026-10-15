package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estimates")
data class EstimateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val billNumber: String,
    val customerName: String,
    val customerPhone: String,
    val siteLocation: String,
    val baseAreaSqFt: Double,
    val floors: Int,
    val constructionTypeId: String,
    val grandTotal: Double,
    val rateCement: Double,
    val rateSand: Double,
    val rateAggregate: Double,
    val rateSteelKg: Double,
    val rateBrick: Double,
    val rateLaborSqft: Double,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
