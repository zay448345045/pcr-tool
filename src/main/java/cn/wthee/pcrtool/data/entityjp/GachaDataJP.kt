package cn.wthee.pcrtool.data.entityjp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gacha_data")
data class GachaDataJP(
    @PrimaryKey
    @ColumnInfo(name = "gacha_id") val gacha_id: Int,
    @ColumnInfo(name = "gacha_name") val gacha_name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "gacha_detail") val gacha_detail: Int,
    @ColumnInfo(name = "gacha_cost_type") val gacha_cost_type: Int,
    @ColumnInfo(name = "price") val price: Int,
    @ColumnInfo(name = "free_gacha_type") val free_gacha_type: Int,
    @ColumnInfo(name = "free_gacha_interval_time") val free_gacha_interval_time: Int,
    @ColumnInfo(name = "free_gacha_count") val free_gacha_count: Int,
    @ColumnInfo(name = "discount_price") val discount_price: Int,
    @ColumnInfo(name = "gacha_odds") val gacha_odds: String,
    @ColumnInfo(name = "gacha_odds_star2") val gacha_odds_star2: String,
    @ColumnInfo(name = "gacha_type") val gacha_type: Int,
    @ColumnInfo(name = "movie_id") val movie_id: Int,
    @ColumnInfo(name = "start_time") val start_time: String,
    @ColumnInfo(name = "end_time") val end_time: String,
    @ColumnInfo(name = "ticket_id") val ticket_id: Int,
    @ColumnInfo(name = "special_id") val special_id: Int,
    @ColumnInfo(name = "exchange_id") val exchange_id: Int,
    @ColumnInfo(name = "ticket_id_10") val ticket_id_10: Int,
    @ColumnInfo(name = "rarity_odds") val rarity_odds: String,
    @ColumnInfo(name = "chara_odds_star1") val chara_odds_star1: String,
    @ColumnInfo(name = "chara_odds_star2") val chara_odds_star2: String,
    @ColumnInfo(name = "chara_odds_star3") val chara_odds_star3: String,
    //jp
    @ColumnInfo(name = "description_2") val description_2: String,
    @ColumnInfo(name = "description_sp") val description_sp: String,
    @ColumnInfo(name = "parallel_id") val parallel_id: Int,
    @ColumnInfo(name = "pickup_badge") val pickup_badge: Int,
    @ColumnInfo(name = "gacha10_special_odds_star1") val gacha10_special_odds_star1: String,
    @ColumnInfo(name = "gacha10_special_odds_star2") val gacha10_special_odds_star2: String,
    @ColumnInfo(name = "gacha10_special_odds_star3") val gacha10_special_odds_star3: String,
    @ColumnInfo(name = "prizegacha_id") val prizegacha_id: Int,
    @ColumnInfo(name = "gacha_bonus_id") val gacha_bonus_id: Int,
    @ColumnInfo(name = "gacha_times_limit10") val gacha_times_limit10: Int,
)