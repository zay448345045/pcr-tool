package cn.wthee.pcrtool.data.enums

/**
 * 角色列表展示类型
 */
enum class CharacterListShowType(val type: Int) {
    CARD(0),
    ICON_TAG(1),
    ICON(2);

    companion object {
        fun getByValue(value: Int) = CharacterListShowType.entries
            .find { it.type == value } ?: CARD
    }
}