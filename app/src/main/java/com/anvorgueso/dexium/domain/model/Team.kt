package com.anvorgueso.dexium.domain.model

data class Team(
    val id: Long,
    val name: String,
    val members: List<Pokemon>
) {
    companion object {
        const val MAX_MEMBERS = 6
    }
}

/**
 * How one attacking type fares against a whole team. Carries the actual vulnerable members
 * rather than just a count, so the UI can show *who* takes the hit and how hard — a 4x
 * weakness on one member matters more than 2x on two, and a bare count hides that.
 */
data class TeamTypeCoverage(
    val type: String,
    val weakMembers: List<WeakMember>,
    val resistCount: Int,
    val immuneCount: Int
) {
    val weakCount: Int get() = weakMembers.size

    /** Highest multiplier any member takes, for flagging the 4x cases. */
    val worstMultiplier: Float get() = weakMembers.maxOfOrNull { it.multiplier } ?: 1f

    /** Nobody resists and nobody is immune: no safe answer to this type. */
    val hasNoAnswer: Boolean get() = resistCount == 0 && immuneCount == 0
}

data class WeakMember(
    val pokemon: Pokemon,
    val multiplier: Float
)
