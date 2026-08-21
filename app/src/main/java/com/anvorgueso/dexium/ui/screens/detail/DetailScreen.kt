package com.anvorgueso.dexium.ui.screens.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anvorgueso.dexium.core.util.TypeChart
import com.anvorgueso.dexium.core.util.formatPokemonId
import com.anvorgueso.dexium.core.util.toHeightString
import com.anvorgueso.dexium.core.util.toWeightString
import com.anvorgueso.dexium.domain.model.EncounterLocation
import com.anvorgueso.dexium.domain.model.PokemonDetail
import com.anvorgueso.dexium.ui.components.ErrorState
import com.anvorgueso.dexium.ui.components.GameSelector
import com.anvorgueso.dexium.ui.components.GlassCard
import com.anvorgueso.dexium.ui.components.GlassTopBar
import com.anvorgueso.dexium.ui.components.HologramStage
import com.anvorgueso.dexium.ui.components.LocationsShimmer
import com.anvorgueso.dexium.ui.components.StatBar
import com.anvorgueso.dexium.ui.components.TypeEffectivenessGroup
import com.anvorgueso.dexium.ui.components.formatMultiplier
import com.anvorgueso.dexium.ui.components.TypeBadge
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.PokemonTypeColors
import com.anvorgueso.dexium.ui.theme.TextSecondary
import com.anvorgueso.dexium.ui.theme.TextTertiary
import androidx.compose.ui.res.stringResource
import com.anvorgueso.dexium.R

@Composable
fun DetailScreen(
    onBackClick: () -> Unit,
    onPokemonClick: (Int, Boolean) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading && uiState.pokemonDetail == null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(DexiumGlass.colors.backgroundStart, DexiumGlass.colors.backgroundEnd)
                        )
                    )
            ) {
                GlassTopBar(title = "", onBackClick = onBackClick)
                DetailShimmer()
            }
        }
        uiState.error != null && uiState.pokemonDetail == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(DexiumGlass.colors.backgroundStart, DexiumGlass.colors.backgroundEnd)
                        )
                    )
            ) {
                GlassTopBar(title = "", onBackClick = onBackClick)
                ErrorState(message = uiState.error ?: "Unknown error", onRetry = viewModel::retry)
            }
        }
        uiState.pokemonDetail != null -> {
            DetailContent(
                pokemon = uiState.pokemonDetail!!,
                uiState = uiState,
                useImperialUnits = uiState.useImperialUnits,
                onBackClick = onBackClick,
                onPokemonClick = onPokemonClick,
                onPlayCry = viewModel::playCry,
                onGameSelected = viewModel::onGameSelected
            )
        }
    }
}

@Composable
private fun DetailContent(
    pokemon: PokemonDetail,
    uiState: DetailUiState,
    useImperialUnits: Boolean,
    onBackClick: () -> Unit,
    onPokemonClick: (Int, Boolean) -> Unit,
    onPlayCry: () -> Unit,
    onGameSelected: (String) -> Unit
) {
    val primaryType = pokemon.types.firstOrNull() ?: "Normal"
    val typeColor = PokemonTypeColors.getColor(primaryType)
    val glass = DexiumGlass.colors

    val shiny = uiState.isShiny

    var showSprites by remember { mutableStateOf(false) }
    // Arriving from the Shiny Dex, the sprite viewer starts on shiny too.
    var showShiny by remember(pokemon.id) { mutableStateOf(shiny) }
    var show3dModel by remember(pokemon.id) { mutableStateOf(false) }
    // The animated sprite is missing for the newest gen 9 entries, so fall back to artwork
    // rather than leaving the hero blank.
    var animatedFailed by remember(pokemon.id) { mutableStateOf(false) }

    val animated3d = if (shiny) pokemon.shinyAnimated3dUrl else pokemon.animated3dUrl
    val staticHero = if (shiny) pokemon.shinySpriteUrl ?: pokemon.imageUrl else pokemon.imageUrl

    val has3dSprite = animated3d != null
    val showing3d = show3dModel && has3dSprite && !animatedFailed
    val heroImageUrl = if (showing3d) animated3d else staticHero

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        typeColor.copy(alpha = 0.35f),
                        glass.backgroundStart.copy(alpha = 0.95f),
                        glass.backgroundEnd
                    )
                )
            )
    ) {
        GlassTopBar(title = pokemon.name, onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = pokemon.id.formatPokemonId(),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextTertiary
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        typeColor.copy(alpha = 0.35f),
                                        typeColor.copy(alpha = 0.1f),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width / 2, size.height / 2),
                                    radius = size.width * 0.5f
                                )
                            )
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.08f),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width / 2, size.height * 0.45f),
                                    radius = size.width * 0.25f
                                )
                            )

                        }
                ) {
                    val heroImage: @Composable () -> Unit = {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(heroImageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = pokemon.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(if (showing3d) 140.dp else 200.dp),
                            onError = { if (showing3d) animatedFailed = true }
                        )
                    }

                    if (showing3d) {
                        HologramStage(
                            accentColor = typeColor,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Lifted off centre so the sprite reads as floating above the plate.
                            Box(modifier = Modifier.offset(y = (-18).dp)) { heroImage() }
                        }
                    } else {
                        heroImage()
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pokemon.types.forEach { type -> TypeBadge(type = type) }
                    CryButton(
                        isPlaying = uiState.isPlayingCry,
                        accentColor = typeColor,
                        onClick = onPlayCry
                    )
                }

                if (pokemon.isLegendary || pokemon.isMythical) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (pokemon.isLegendary) stringResource(R.string.label_legendary) else stringResource(R.string.label_mythical),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                Column {
                    Text(
                        text = stringResource(R.string.section_sprites),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.toggle_show_3d),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = stringResource(R.string.toggle_show_3d_desc),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                        }
                        Switch(
                            checked = showing3d,
                            enabled = has3dSprite && !animatedFailed,
                            onCheckedChange = { show3dModel = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = glass.accent,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = glass.surface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.toggle_show_sprite),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Switch(
                            checked = showSprites,
                            onCheckedChange = {
                                showSprites = it
                                if (!it) showShiny = false
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = glass.accent,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = glass.surface
                            )
                        )
                    }

                    if (showSprites) {
                        val spriteUrl = pokemon.animatedImageUrl ?: pokemon.imageUrl
                        var useFallback by remember(pokemon.id) { mutableStateOf(false) }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(if (useFallback) pokemon.imageUrl else spriteUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "${pokemon.name} sprite",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(80.dp),
                                onError = { if (!useFallback) useFallback = true }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.toggle_show_shiny),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Switch(
                                checked = showShiny,
                                onCheckedChange = { showShiny = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFFFD700),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = glass.surface
                                )
                            )
                        }

                        if (showShiny) {
                            val shinyUrl = pokemon.shinyAnimatedSpriteUrl
                                ?: pokemon.shinySpriteUrl
                            var useShinyFallback by remember(pokemon.id) { mutableStateOf(false) }
                            var shinyFailed by remember(pokemon.id) { mutableStateOf(false) }

                            if (shinyUrl != null && !shinyFailed) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(
                                                if (useShinyFallback) pokemon.shinySpriteUrl ?: shinyUrl
                                                else shinyUrl
                                            )
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "${pokemon.name} shiny sprite",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(80.dp),
                                        onError = {
                                            if (!useShinyFallback) {
                                                useShinyFallback = true
                                            } else {
                                                shinyFailed = true
                                            }
                                        }
                                    )
                                }

                                Text(
                                    text = stringResource(R.string.label_shiny),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color(0xFFFFD700),
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.label_no_shiny),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (pokemon.evolutionChain.size > 1) {
                GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                    Column {
                        Text(
                            text = stringResource(R.string.section_evolution),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            pokemon.evolutionChain.forEachIndexed { index, stage ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable {
                                        if (stage.pokemonId != pokemon.id) {
                                            onPokemonClick(stage.pokemonId, shiny)
                                        }
                                    }
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(if (shiny) stage.shinyImageUrl else stage.imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = stage.pokemonName,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(64.dp)
                                    )
                                    Text(
                                        text = stage.pokemonName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (stage.pokemonId == pokemon.id) typeColor else TextSecondary
                                    )
                                    stage.minLevel?.let {
                                        Text(
                                            text = "Lv.$it",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextTertiary
                                        )
                                    }
                                }

                                if (index < pokemon.evolutionChain.lastIndex) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = TextTertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                Column {
                    Text(
                        text = stringResource(R.string.section_stats),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    pokemon.stats.forEachIndexed { index, stat ->
                        StatBar(stat = stat, animDelay = index * 100)
                        if (index < pokemon.stats.lastIndex) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                Column {
                    Text(
                        text = stringResource(R.string.section_about),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = pokemon.genus,
                        style = MaterialTheme.typography.bodyMedium,
                        color = typeColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = pokemon.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InfoItem(stringResource(R.string.label_height), pokemon.height.toHeightString(useImperialUnits))
                        InfoItem(stringResource(R.string.label_weight), pokemon.weight.toWeightString(useImperialUnits))
                        InfoItem(stringResource(R.string.label_base_exp), pokemon.baseExperience.toString())
                    }

                    pokemon.habitat?.let { habitat ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Text(stringResource(R.string.label_habitat) + ": ", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                            Text(habitat, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                Column {
                    Text(
                        text = stringResource(R.string.section_abilities),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    pokemon.abilities.forEach { ability ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = ability.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                            if (ability.isHidden) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.label_hidden),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(glass.surface)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                Column {
                    Text(
                        text = stringResource(R.string.section_weaknesses),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val locale = LocalConfiguration.current.locales[0].language
                    // pokemon.types is already localized, so the chart canonicalizes internally.
                    val weak = TypeChart.weaknesses(pokemon.types)
                    val resist = TypeChart.resistances(pokemon.types)
                    val immune = TypeChart.immunities(pokemon.types)

                    TypeEffectivenessGroup(
                        label = stringResource(R.string.weak_to),
                        entries = weak.entries
                            .sortedByDescending { it.value }
                            .map { it.key to formatMultiplier(it.value) },
                        locale = locale
                    )
                    if (weak.isNotEmpty() && resist.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    TypeEffectivenessGroup(
                        label = stringResource(R.string.resists),
                        entries = resist.entries
                            .sortedBy { it.value }
                            .map { it.key to formatMultiplier(it.value) },
                        locale = locale
                    )
                    if (immune.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TypeEffectivenessGroup(
                            label = stringResource(R.string.immune_to),
                            entries = immune.map { it to formatMultiplier(0f) },
                            locale = locale
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.section_locations),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        if (uiState.encounters.isNotEmpty()) {
                            GameSelector(
                                games = uiState.encounters,
                                selectedSlug = uiState.selectedGameSlug,
                                onGameSelected = onGameSelected,
                                accentColor = typeColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val selectedGame = uiState.selectedGame
                    when {
                        uiState.isLoadingEncounters -> LocationsShimmer()
                        uiState.encountersError == "NO_INTERNET" -> LocationsPlaceholder(
                            text = stringResource(R.string.locations_no_internet)
                        )
                        uiState.encountersError != null -> LocationsPlaceholder(
                            text = stringResource(R.string.locations_error)
                        )
                        selectedGame == null -> LocationsPlaceholder(
                            text = stringResource(R.string.locations_empty),
                            // PokeAPI simply has no gen 9 encounter data yet; say so instead
                            // of letting the section read as a bug.
                            hint = if (pokemon.id in 906..1025) {
                                stringResource(R.string.locations_empty_hint)
                            } else {
                                null
                            }
                        )
                        else -> {
                            Text(
                                text = if (selectedGame.locations.size == 1) {
                                    stringResource(R.string.locations_count_one)
                                } else {
                                    stringResource(R.string.locations_count, selectedGame.locations.size)
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            selectedGame.locations.forEach { location ->
                                EncounterRow(
                                    location = location,
                                    accentColor = typeColor,
                                    glassSurface = glass.surface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CryButton(
    isPlaying: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isPlaying) 1.12f else 1f,
        animationSpec = tween(300),
        label = "cryScale"
    )

    GlassCard(
        modifier = Modifier
            .size(34.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(onClick = onClick),
        cornerRadius = 17.dp,
        contentPadding = 0.dp,
        glowColor = accentColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = stringResource(R.string.detail_play_cry),
                tint = if (isPlaying) accentColor else Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun LocationsPlaceholder(text: String, hint: String? = null) {
    Column {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        if (hint != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = hint,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun EncounterRow(
    location: EncounterLocation,
    accentColor: Color,
    glassSurface: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = location.locationName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            Text(
                text = location.method,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }

        Text(
            text = if (location.minLevel == location.maxLevel) {
                stringResource(R.string.locations_level_single, location.minLevel)
            } else {
                stringResource(R.string.locations_level_range, location.minLevel, location.maxLevel)
            },
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(glassSurface)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = stringResource(R.string.locations_chance, location.chance),
            style = MaterialTheme.typography.labelSmall,
            color = accentColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(38.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
        )
    }
}
