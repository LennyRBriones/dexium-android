package com.anvorgueso.dexium.ui.screens.guessgame

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.anvorgueso.dexium.ui.components.GlassCard
import com.anvorgueso.dexium.ui.components.GlassTopBar
import com.anvorgueso.dexium.ui.components.GradientBackground
import com.anvorgueso.dexium.ui.components.LoadingIndicator
import com.anvorgueso.dexium.ui.theme.DexiumGlass
import com.anvorgueso.dexium.ui.theme.GlassBlue
import com.anvorgueso.dexium.ui.theme.GlassBlueSoft
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Generation accent colors
private val genAccentColors = listOf(
    Color(0xFFEF5350), // Gen 1 - Red
    Color(0xFFFFD740), // Gen 2 - Gold
    Color(0xFF66BB6A), // Gen 3 - Emerald
    Color(0xFF42A5F5), // Gen 4 - Diamond
    Color(0xFF78909C), // Gen 5 - Gray
    Color(0xFFEC407A), // Gen 6 - Pink
    Color(0xFFFF7043), // Gen 7 - Orange
    Color(0xFF5C6BC0), // Gen 8 - Indigo
    Color(0xFFAB47BC), // Gen 9 - Purple
)

@HiltViewModel
class GenerationSelectViewModel @Inject constructor(
    private val generationRepository: GenerationRepository
) : ViewModel() {

    private val _generations = MutableStateFlow<List<Generation>>(emptyList())
    val generations: StateFlow<List<Generation>> = _generations.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadGenerations()
    }

    private fun loadGenerations() {
        viewModelScope.launch {
            _isLoading.value = true
            generationRepository.getGenerations().collect { resource ->
                if (resource is Resource.Success) {
                    _generations.value = resource.data?.filter { it.id in 1..9 } ?: emptyList()
                    _isLoading.value = false
                }
            }
        }
    }
}

@Composable
fun GenerationSelectScreen(
    onBackClick: () -> Unit,
    onGenerationSelected: (String) -> Unit,
    onCustomClick: () -> Unit,
    viewModel: GenerationSelectViewModel = hiltViewModel()
) {
    val generations by viewModel.generations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val glass = DexiumGlass.colors

    GradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassTopBar(
                title = stringResource(R.string.guess_game_title),
                onBackClick = onBackClick
            )

            if (isLoading) {
                LoadingIndicator(message = stringResource(R.string.guess_loading))
                return@GradientBackground
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                // All Generations
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGenerationSelected("all") },
                        glowColor = GlassBlue,
                        contentPadding = 16.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shuffle,
                                contentDescription = null,
                                tint = GlassBlue,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.guess_all_generations),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.guess_all_generations_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                // Generation cards
                itemsIndexed(generations) { index, generation ->
                    val accentColor = genAccentColors.getOrElse(index) { GlassBlueSoft }
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGenerationSelected(generation.id.toString()) },
                        glowColor = accentColor,
                        contentPadding = 16.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Gen number badge
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Text(
                                    text = "${generation.id}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = accentColor,
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
                                Row {
                                    Text(
                                        text = generation.regionName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = accentColor.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.guess_pokemon_count, generation.pokemonCount),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Custom
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCustomClick() },
                        glowColor = Color(0xFFFFD740),
                        contentPadding = 16.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = Color(0xFFFFD740),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.guess_custom),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.guess_custom_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                // Bottom spacing
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
