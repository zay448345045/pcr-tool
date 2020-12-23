package cn.wthee.pcrtool.data.db.dao

import androidx.room.*
import cn.wthee.pcrtool.data.db.entity.PvpLikedData

@Dao
interface PvpDao {

    @Query("SELECT * FROM pvp_like WHERE region = :region ORDER BY date DESC")
    suspend fun getAll(region: Int): List<PvpLikedData>

    @Query("SELECT * FROM pvp_like WHERE atks = :atks AND defs = :defs AND region = :region")
    suspend fun get(atks: String, defs: String, region: Int): PvpLikedData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: PvpLikedData)


    @Query("DELETE  FROM pvp_like WHERE atks = :atks AND defs = :defs AND region = :region")
    suspend fun delete(atks: String, defs: String, region: Int)

    @Delete
    suspend fun delete(data: PvpLikedData)
}