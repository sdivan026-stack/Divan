package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EstimateDao {

    @Query("SELECT * FROM estimates ORDER BY createdAt DESC")
    fun getAllEstimates(): Flow<List<EstimateEntity>>

    @Query("SELECT * FROM estimates WHERE id = :id LIMIT 1")
    suspend fun getEstimateById(id: Long): EstimateEntity?

    @Query("SELECT * FROM estimates WHERE customerName LIKE '%' || :query || '%' OR siteLocation LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchEstimates(query: String): Flow<List<EstimateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstimate(estimate: EstimateEntity): Long

    @Update
    suspend fun updateEstimate(estimate: EstimateEntity)

    @Delete
    suspend fun deleteEstimate(estimate: EstimateEntity)

    @Query("DELETE FROM estimates WHERE id = :id")
    suspend fun deleteById(id: Long)
}
