package cn.wthee.pcrtool.data.enums

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.colorBlue
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.ui.theme.colorPink
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorSilver
import cn.wthee.pcrtool.utils.Constants



/**
 *菜单
 */
enum class ToolMenuType(val id: Int, @StringRes val titleId: Int, val iconType: MainIconType) {
    CHARACTER(200, R.string.character, MainIconType.CHARACTER),
    EQUIP(201, R.string.tool_equip, MainIconType.EQUIP),
    GUILD(202, R.string.tool_guild, MainIconType.GUILD),
    CLAN(203, R.string.tool_clan, MainIconType.CLAN),
    RANDOM_AREA(204, R.string.random_area, MainIconType.RANDOM_AREA),
    GACHA(205, R.string.tool_gacha, MainIconType.GACHA),
    STORY_EVENT(206, R.string.tool_event, MainIconType.EVENT),
    NEWS(207, R.string.tool_news, MainIconType.NEWS),
    FREE_GACHA(208, R.string.tool_free_gacha, MainIconType.FREE_GACHA),
    PVP_SEARCH(209, R.string.tool_pvp, MainIconType.PVP_SEARCH),
    LEADER(210, R.string.tool_leader, MainIconType.LEADER),
    TWEET(211, R.string.tweet, MainIconType.TWEET),
    COMIC(212, R.string.comic_4, MainIconType.COMIC),

    ALL_EQUIP(214, R.string.calc_equip_count, MainIconType.EQUIP_CALC),
    MOCK_GACHA(215, R.string.tool_mock_gacha, MainIconType.MOCK_GACHA),
    BIRTHDAY(216, R.string.tool_birthday, MainIconType.BIRTHDAY),
    CALENDAR_EVENT(217, R.string.tool_calendar_event, MainIconType.CALENDAR),
    EXTRA_EQUIP(218, R.string.tool_extra_equip, MainIconType.EXTRA_EQUIP),
    TRAVEL_AREA(219, R.string.tool_travel, MainIconType.EXTRA_EQUIP_DROP),
    WEBSITE(220, R.string.tool_website, MainIconType.WEBSITE_BOOKMARK),
    LEADER_TIER(221, R.string.tool_leader_tier, MainIconType.LEADER_TIER),
    ALL_QUEST(222, R.string.tool_all_quest, MainIconType.ALL_QUEST),
    UNIQUE_EQUIP(223, R.string.tool_unique_equip, MainIconType.UNIQUE_EQUIP),
    LOAD_COMIC(224, R.string.tool_load_comic, MainIconType.LOAD_COMIC),
    TALENT_LIST(225, R.string.unit_talent, MainIconType.TALENT),
    UNKNOWN_SKILL_LIST(999, R.string.skill, MainIconType.SKILL_LOOP),
    ;


    companion object {
        fun getByValue(value: Int) = entries
            .find { it.id == value }
    }
}

/**
 * 站位
 */
enum class PositionType(val type: Int, val typeNameId: Int, val color: Color, val iconId: Int) {
    UNKNOWN(0, R.string.unknown, colorGray, R.drawable.unknown_item),
    POSITION_FRONT(1, R.string.position_0, colorRed, R.drawable.ic_position_0),
    POSITION_MIDDLE(2, R.string.position_1, colorGold, R.drawable.ic_position_1),
    POSITION_BACK(2, R.string.position_2, colorCyan, R.drawable.ic_position_2), ;

    companion object {
        fun getPositionType(position: Int) = when (position) {
            in 1..299 -> POSITION_FRONT
            in 300..599 -> POSITION_MIDDLE
            in 600..9999 -> POSITION_BACK
            else -> UNKNOWN
        }
    }
}

/**
 * 技能类型
 */
enum class SkillType {
    ALL,
    NORMAL,
    SP
}

/**
 * 属性值类型
 */
enum class AttrValueType {
    INT,
    DOUBLE,
    PERCENT
}

/**
 * 首页
 */
enum class OverviewType(val id: Int) {
    CHARACTER(0),
    EQUIP(1),
    TOOL(2),
    NEWS(3),
    IN_PROGRESS_EVENT(4),
    COMING_SOON_EVENT(5),
    UNIQUE_EQUIP(6)
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.id == value } ?: CHARACTER
    }
}

/**
 * 设置枚举
 */
enum class SettingSwitchType {
    /**
     * 振动设置
     */
    VIBRATE,

    /**
     * 动画效果设置
     */
    ANIMATION,

    /**
     * 动态色彩设置
     */
    DYNAMIC_COLOR,

    /**
     * 使用ip访问（仅网络异常使用）
     */
    USE_IP
}


/**
 * 公告
 */
enum class NewsType(val stringId: Int) {
    UPDATE(R.string.update),
    SYSTEM(R.string.system),
    NEWS(R.string.tool_news),
    EVENT(R.string.event),
    SHOP(R.string.shop),
    LOCAL(R.string.local_note);
}

/**
 * 查询关键词
 */
enum class KeywordType(val type: Int) {
    NEWS(1),
    TWEET(2);
}

/**
 * 角色评级
 */
enum class LeaderTierType(val type: Int) {
    ALL(0),
    PVP_ATK(1),
    PVP_DEF(2),
    CLAN(3);

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.type == value } ?: ALL
    }
}

/**
 * 技能下标类型
 */
enum class SkillIndexType(val index: Int) {
    UNKNOWN(-1),
    UB(0),
    UB_PLUS(0),
    MAIN_SKILL_1(1),
    MAIN_SKILL_2(2),
    MAIN_SKILL_3(3),
    MAIN_SKILL_4(4),
    MAIN_SKILL_5(5),
    MAIN_SKILL_6(6),
    MAIN_SKILL_7(7),
    MAIN_SKILL_8(8),
    MAIN_SKILL_9(9),
    MAIN_SKILL_10(10),
    MAIN_SKILL_1_PLUS(1),
    MAIN_SKILL_2_PLUS(2),
    EX_1(1),
    EX_2(2),
    EX_3(3),
    EX_4(4),
    EX_5(5),
    EX_1_PLUS(1),
    EX_2_PLUS(2),
    EX_3_PLUS(3),
    EX_4_PLUS(4),
    EX_5_PLUS(5),
    SP_UB(0),
    SP_SKILL_1(1),
    SP_SKILL_2(2),
    SP_SKILL_3(3),
    SP_SKILL_4(4),
    SP_SKILL_5(5),
    SP_SKILL_1_PLUS(1),
    SP_SKILL_2_PLUS(2),
    ;
}

/**
 * 区服
 */
enum class RegionType(val value: Int, val stringId: Int, val code: String) {
    CN(2, R.string.db_cn, "cn"),
    TW(3, R.string.db_tw, "tw"),
    JP(4, R.string.db_jp, "jp"),
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.value == value } ?: CN
    }
}

/**
 * 活动日程类型
 */
enum class CalendarEventType(val type: Int) {
    UNKNOWN(404),
    TOWER(1),
    SP_DUNGEON(-1),
    TDF(-2),
    COLOSSEUM(-3),
    DAILY(18),
    LOGIN(19),
    FORTUNE(20),
    N_DROP(31),
    N_MANA(41),
    H_DROP(32),
    H_MANA(42),
    VH_DROP(39),
    VH_MANA(49),
    EXPLORE(34),
    SHRINE(37),
    TEMPLE(38),
    DUNGEON(45),
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.type == value } ?: UNKNOWN
    }
}


/**
 * 角色详情模块
 */
enum class CharacterDetailModuleType(val id: Int, val titleId: Int) {
    UNKNOWN(299, R.string.unknown),
    CARD(300, R.string.character_card),
    COE(301, R.string.character_power),
    TOOLS(302, R.string.character_tool),
    STAR(303, R.string.title_rarity),
    LEVEL(304, R.string.title_unit_level),
    ATTR(305, R.string.character_attr),
    OTHER_TOOLS(306, R.string.character_other_tool),
    EQUIP(307, R.string.tool_equip),
    UNIQUE_EQUIP(308, R.string.tool_unique_equip),
    SKILL_LOOP(309, R.string.tip_skill_loop),
    SKILL(310, R.string.skill),
    UNIT_ICON(311, R.string.character_icon_info),
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.id == value } ?: UNKNOWN
    }
}

/**
 * 视频类型
 */
enum class VideoType(val value: Int, val typeName: Int) {
    UNKNOWN(0, R.string.unknown),
    UB_SKILL(1, R.string.union_burst),
    CHARACTER_CARD(2, R.string.character),
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.value == value } ?: UNKNOWN
    }
}


/**
 * 图标类型
 */
enum class IconResourceType {
    CHARACTER,
    EQUIP,
    UNIQUE_EQUIP,
    EX_EQUIP,
    ;
}

/**
 * 角色获取类型
 */
enum class CharacterLimitType(val type: Int, val color: Color, val typeNameId: Int) {
    NORMAL(1, colorGold, R.string.type_normal),
    LIMIT(2, colorRed, R.string.type_limit),
    EVENT(3, colorGreen, R.string.type_event_limit),
    EXTRA(4, colorCyan, R.string.type_extra_character);

    companion object {
        fun getByType(type: Int) = CharacterLimitType.entries
            .find { it.type == type } ?: NORMAL
    }
}

/**
 * 装备品级、RANk 颜色
 */
enum class RankColor(val type: Int, val color: Color, val typeNameId: Int) {
    UNKNOWN(0, colorGray, R.string.unknown),
    BLUE(1, colorBlue, R.string.color_blue),
    COPPER(2, colorCopper, R.string.color_copper),
    SILVER(3, colorSilver, R.string.color_silver),
    GOLD(4, colorGold, R.string.color_gold),
    PURPLE(5, colorPurple, R.string.color_purple),
    RED(6, colorRed, R.string.color_red),
    GREEN(7, colorGreen, R.string.color_green),
    ORANGE(8, colorOrange, R.string.color_orange),
    CYAN(9, colorCyan, R.string.color_cyan),
    PINK(10, colorPink, R.string.color_pink),
    ;

    companion object {
        fun getByType(type: Int) = RankColor.entries
            .find { it.type == type } ?: UNKNOWN


        /**
         * rank 颜色
         * @param rank rank数值
         */
        fun getRankColor(rank: Int): Color {
            return when (rank) {
                1 -> BLUE.color
                in 2..3 -> COPPER.color
                in 4..6 -> SILVER.color
                in 7..10 -> GOLD.color
                in 11..17 -> PURPLE.color
                in 18..20 -> RED.color
                in 21..23 -> GREEN.color
                in 24..27 -> ORANGE.color
                in 28..99 -> CYAN.color
                else -> UNKNOWN.color
            }
        }
    }
}

/**
 * ex装备品级
 */
enum class ExtraEquipLevelColor(val type: Int, val color: Color, val typeName: String) {
    UNKNOWN(0, colorGray, Constants.UNKNOWN),
    COPPER(1, colorCopper, "★1"),
    SILVER(2, colorSilver, "★2"),
    GOLD(3, colorGold, "★3"),
    PINK(4, colorPink, "★4"),
    ;

    companion object {
        fun getByType(type: Int) = ExtraEquipLevelColor.entries
            .find { it.type == type } ?: UNKNOWN
    }
}

/**
 * 攻击类型
 */
enum class AtkType(val type: Int, val color: Color, val typeNameId: Int, val iconId: Int) {
    UNKNOWN(0, colorGray, R.string.unknown, R.drawable.unknown_item),
    PHYSICAL(1, colorGold, R.string.physical, R.drawable.ic_atk_type_1),
    MAGIC(2, colorPurple, R.string.magic, R.drawable.ic_atk_type_2)
    ;

    companion object {
        fun getByType(type: Int) = AtkType.entries
            .find { it.type == type } ?: UNKNOWN
    }
}


/**
 * 角色排行排序类型枚举
 */
enum class LeaderboardSortType(val type: Int, val titleId: Int) {
    ALL(type = 0, titleId = R.string.all),
    TALENT(type = 1, titleId = R.string.talent),
    PVP(type = 2, titleId = R.string.jjc),
    CLAN_BATTLE(type = 3, titleId = R.string.clan),
    ;

    companion object {
        fun getByType(type: Int) = entries
            .find { it.type == type } ?: ALL
    }
}