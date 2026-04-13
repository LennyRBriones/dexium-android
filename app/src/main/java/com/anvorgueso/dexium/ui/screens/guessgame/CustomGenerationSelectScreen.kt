package com.anvorgueso.dexium.ui.screens.guessgame

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.R
import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.Generation
import com.anvorgueso.dexium.domain.repository.GenerationRepository
import com.anvorgueso.dexium.ui.components.GlassTopBar
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.components.LoadingIndicator
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.GlassBlueSoft
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val genAccentColors = listOf(
    Color(0xFFEF5350), Color(0xFFFFD740), Color(0xFF66BB6A),
    Color(0xFF42A5F5), Color(0xFF78909C), Color(0xFFEC407A),
    Color(0xFFFF7043), Color(0xFF5C6BC0), Color(0xFFAB47BC)
)

data class CustomSelectUiState(
    val generations: List<Generation> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CustomGenerationSelectViewModel @Inject constructor(
    private val generationRepository: GenerationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomSelectUiState())
    val uiState: StateFlow<CustomSelectUiState> = _uiState.asStateFlow()

    init {
        loadGenerations()
    }

    private fun loadGenerations() {
        viewModelScope.launch {
            generationRepository.getGenerations().collect { resource ->
                if (resource is Resource.Success) {
                    _uiState.update {
                        it.copy(
                            generations = resource.data?.filter { gen -> gen.id in 1..9 } ?: emptyList(),
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun toggleGeneration(id: Int) {
        _uiState.update {
            val newSet = if (id in it.selectedIds) {
                it.selectedIds - id
            } else {
                it.selectedIds + id
            }
            it.copy(selectedIds = newSet)
        }
    }

    fun getSelectedIdsString(): String {
        return _uiState.value.selectedIds.sorted().joinToString(",")
    }
}

@Composable
fun CustomGenerationSelectScreen(
    onBackClick: () -> Unit,
    onStartGame: (String) -> Unit,
    viewModel: CustomGenerationSelectViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val glass = DexiumGlass.colors
    val canStart = uiState.selectedIds.size >= 2

    GradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(
                title = stringResource(R.string.guess_custom_title),
                onBackClick = onBackClick
            )

            if (uiState.isLoading) {
                LoadingIndicator(message = stringResource(R.string.guess_loading))
                return@GradientBackground
            }


            Text(
                text = stringResource(R.string.guess_custom_min),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            if (uiState.selectedIds.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.guess_selected_count, uiState.selectedIds.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = glass.accent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(uiState.generations) { index, generation ->
                    val isSelected = generation.id in uiState.selectedIds
                    val accentColor = genAccentColors.getOrElse(index) { GlassBlueSoft }

                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) accentColor.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.1f),
                        animationSpec = tween(200),
                        label = "border"
                    )

                    val bgAlpha by animateColorAsState(
                        targetValue = if (isSelected) accentColor.copy(alpha = 0.15f) else Color.Transparent,
                        animationSpec = tween(200),
                        label = "bg"
                    )

                    val shape = RoundedCornerShape(16.dp)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .border(1.dp, borderColor, shape)
                            .background(bgAlpha, shape)
                            .clickable { viewModel.toggleGeneration(generation.id) }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Text(
                                    text = "${generation.id}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = if (isSelected) accentColor else accentColor.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = generation.displayName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${generation.regionName} - ${stringResource(R.string.guess_pokemon_count, generation.pokemonCount)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(accentColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                val startShape = RoundedCornerShape(20.dp)
                val startColor = if (canStart) glass.accent else Color.White.copy(alpha = 0.2f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(startShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = if (canStart) listOf(
                                    glass.accent.copy(alpha = 0.3f),
                                    glass.accent.copy(alpha = 0.15f)
                                ) else listOf(
                                    Color.White.copy(alpha = 0.05f),
                                    Color.White.copy(alpha = 0.03f)
                                )
                            ),
                            startShape
                        )
                        .border(1.dp, startColor.copy(alpha = 0.4f), startShape)
                        .clickable(enabled = canStart) {
                            onStartGame(viewModel.getSelectedIdsString())
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = startColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.guess_start_game),
                            style = MaterialTheme.typography.titleMedium,
                            color = startColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
