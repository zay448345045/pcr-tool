package cn.wthee.pcrtool.data.enums

/**
 * 模拟卡池类型
 */
enum class MockGachaType(val type: Int) {
    PICK_UP(0),
    FES(1),
    PICK_UP_SINGLE(2);

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.type == value } ?: PICK_UP
    }
}
