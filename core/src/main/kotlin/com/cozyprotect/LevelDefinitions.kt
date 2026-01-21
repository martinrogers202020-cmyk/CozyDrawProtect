package com.cozyprotect

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HazardType { BEE, ROCK, WIND, HOT_SOUP }

@Serializable
enum class MaterialType { RUBBER, STONE, ICE, CLOTH }

@Serializable
data class HazardDefinition(
    val type: HazardType,
    val positionX: Float,
    val positionY: Float
)

@Serializable
data class LevelDefinition(
    val id: String,
    @SerialName("characterX") val characterX: Float = 4f,
    @SerialName("characterY") val characterY: Float = 3f,
    val timeToSurvive: Int,
    val drawLimit: Int,
    val hazards: List<HazardDefinition> = emptyList(),
    val allowedMaterials: List<MaterialType> = listOf(
        MaterialType.RUBBER,
        MaterialType.STONE,
        MaterialType.ICE,
        MaterialType.CLOTH
    )
)

@Serializable
data class PackProgress(val completed: Int, val stars: Int)

@Serializable
data class LevelPack(
    val packId: String,
    val name: String,
    val levels: List<LevelDefinition>,
    val progress: PackProgress = PackProgress(0, 0)
)
