package com.anvorgueso.dexium.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anvorgueso.dexium.domain.model.DexCategory
import com.anvorgueso.dexium.ui.components.DexSelector
import com.anvorgueso.dexium.ui.components.DexiumFilterChip
import com.anvorgueso.dexium.ui.components.DexiumSearchBar
import com.anvorgueso.dexium.ui.components.EmptySearchState
import com.anvorgueso.dexium.ui.components.ErrorState
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.components.GlassTopBar
import com.anvorgueso.dexium.ui.components.LoadingIndicator
import com.anvorgueso.dexium.ui.components.PokemonCard
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import androidx.compose.ui.res.stringResource
import com.anvorgueso.dexium.R

@Composable
fun HomeScreen(
    onPokemonClick: (Int) -> Unit,
    onAboutClick: () -> Unit,
    onGuessGameClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()
    val glass = DexiumGlass.colors
    var showFabMenu by remember { mutableStateOf(false) }

    LaunchedEffect(gridState) {
        snapshotFlow {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = gridState.layoutInfo.totalItemsCount
            lastVisibleItem to totalItems
        }.collect { (lastVisible, total) ->
            if (total > 0 && lastVisible >= total - 8) {
                viewModel.loadNextPage()
            }
        }
    }

    GradientBackground {
        Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(
                title = "Dexium",
                centerContent = {
                    DexSelector(
                        selectedCategory = uiState.selectedDexCategory,
                        onCategorySelected = viewModel::onDexCategorySelected
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = glass.accent
                        )
                    }
                    IconButton(onClick = onAboutClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = glass.accent
                        )
                    }
                }
            )

            DexiumSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = stringResource(R.string.search_placeholder),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )

            if (uiState.selectedDexCategory == DexCategory.FORMS) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    item {
                        DexiumFilterChip(
                            label = stringResource(R.string.filter_all),
                            isSelected = uiState.selectedFormRegion == null,
                            onClick = { viewModel.onFormRegionSelected(null) }
                        )
                    }
                    val regions = listOf("alola" to "Alola", "galar" to "Galar", "hisui" to "Hisui", "paldea" to "Paldea")
                    items(regions) { (key, label) ->
                        DexiumFilterChip(
                            label = label,
                            isSelected = uiState.selectedFormRegion == key,
                            onClick = { viewModel.onFormRegionSelected(key) }
                        )
                    }
                    item {
                        DexiumFilterChip(
                            label = stringResource(R.string.filter_other),
                            isSelected = uiState.selectedFormRegion == "other",
                            onClick = { viewModel.onFormRegionSelected("other") }
                        )
                    }
                }
            } else if (uiState.selectedDexCategory == DexCategory.NATIONAL && uiState.generations.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    item {
                        DexiumFilterChip(
                            label = stringResource(R.string.filter_all),
                            isSelected = uiState.selectedGenerationId == null,
                            onClick = { viewModel.onGenerationSelected(null) }
                        )
                    }
                    items(uiState.generations) { generation ->
                        DexiumFilterChip(
                            label = generation.regionName,
                            isSelected = uiState.selectedGenerationId == generation.id,
                            onClick = { viewModel.onGenerationSelected(generation.id) }
                        )
                    }
                }
            }

            when {
                uiState.isLoading && uiState.pokemonList.isEmpty() -> {
                    LoadingIndicator(message = stringResource(R.string.loading_creatures))
                }
                uiState.error != null && uiState.pokemonList.isEmpty() -> {
                    ErrorState(
                        message = uiState.error ?: "Unknown error",
                        onRetry = viewModel::retry
                    )
                }
                !uiState.isLoading && uiState.pokemonList.isEmpty() &&
                        (uiState.searchQuery.isNotEmpty() || uiState.selectedGenerationId != null || uiState.selectedFormRegion != null) -> {
                    EmptySearchState(
                        query = uiState.searchQuery.ifEmpty {
                            uiState.selectedFormRegion ?: "this filter"
                        }
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        state = gridState,
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 16.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding()
                    ) {
                        items(
                            items = uiState.pokemonList,
                            key = { it.id }
                        ) { pokemon ->
                            PokemonCard(
                                pokemon = pokemon,
                                onClick = { onPokemonClick(pokemon.id) }
                            )
                        }

                        if (uiState.isLoadingMore) {
                            item(span = { GridItemSpan(3) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = glass.accent,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

            // FAB overlay menu backdrop
            if (showFabMenu) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { showFabMenu = false }
                )
            }

            // FAB menu item
            AnimatedVisibility(
                visible = showFabMenu,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 90.dp)
                    .navigationBarsPadding(),
                enter = scaleIn(
                    animationSpec = tween(200),
                    initialScale = 0.8f
                ) + fadeIn(tween(200)),
                exit = scaleOut(
                    animationSpec = tween(150),
                    targetScale = 0.8f
                ) + fadeOut(tween(150))
            ) {
                val menuShape = RoundedCornerShape(16.dp)
                Box(
                    modifier = Modifier
                        .clip(menuShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    Color.White.copy(alpha = 0.06f)
                                )
                            ),
                            menuShape
                        )
                        .clickable {
                            showFabMenu = false
                            onGuessGameClick()
                        }
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = glass.accent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.guess_game),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Glass FAB
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .navigationBarsPadding()
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                glass.accent.copy(alpha = 0.3f),
                                glass.accent.copy(alpha = 0.1f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                glass.accent.copy(alpha = 0.6f),
                                glass.accent.copy(alpha = 0.15f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .drawWithContent {
                        drawContent()
                        // Top highlight
                        drawCircle(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = size.height * 0.5f
                            ),
                            radius = size.width * 0.48f
                        )
                    }
                    .clickable { showFabMenu = !showFabMenu },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = "Games",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        } // Box
    }
}
