package com.anvorgueso.dexium.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anvorgueso.dexium.core.util.Constants
import com.anvorgueso.dexium.core.util.Resource
import com.anvorgueso.dexium.domain.model.DexCategory
import com.anvorgueso.dexium.domain.model.Generation
import com.anvorgueso.dexium.domain.model.Pokemon
import com.anvorgueso.dexium.domain.repository.GenerationRepository
import com.anvorgueso.dexium.domain.repository.PokemonRepository
import com.anvorgueso.dexium.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val pokemonList: List<Pokemon> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedGenerationId: Int? = null,
    val selectedFormRegion: String? = null,
    val selectedDexCategory: DexCategory = DexCategory.NATIONAL,
    val generations: List<Generation> = emptyList(),
    val isRefreshing: Boolean = false,
    val currentPage: Int = 0,
    val canLoadMore: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    private val generationRepository: GenerationRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    private val _selectedGeneration = MutableStateFlow<Int?>(null)
    private var loadJob: Job? = null

    init {
        observeFilters()
        observePreferenceChanges()
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            ensureGenerationsLoaded()
            loadPokemonPage(0)
            precacheNames()
        }
    }

    private suspend fun ensureGenerationsLoaded() {
        generationRepository.getGenerations().first { resource ->
            if (resource is Resource.Success) {
                _uiState.update { it.copy(generations = resource.data ?: emptyList()) }
                true
            } else {
                false
            }
        }
    }

    private fun precacheNames() {
        viewModelScope.launch {
            pokemonRepository.precachePokemonNames()
            refreshDisplayedTypes()
        }
    }

    /**
     * The name precache and its type backfill run off the critical path, so the grid can
     * already be showing rows that were still placeholders when they were read. Patch those
     * in place instead of reloading, which would reset pagination and the scroll position.
     */
    private suspend fun refreshDisplayedTypes() {
        val stale = _uiState.value.pokemonList.filterNot { it.hasRealType }
        if (stale.isEmpty()) return

        val refreshed = pokemonRepository.getPokemonByIds(stale.map { it.id })
            .filter { it.hasRealType }
            .associateBy { it.id }
        if (refreshed.isEmpty()) return

        _uiState.update { state ->
            state.copy(pokemonList = state.pokemonList.map { refreshed[it.id] ?: it })
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeFilters() {
        combine(
            _searchQuery.debounce(Constants.SEARCH_DEBOUNCE_MS),
            _selectedGeneration
        ) { query, genId ->
            Pair(query, genId)
        }
            .drop(1)
            .onEach { (query, genId) ->
                handleFilterChange(query, genId)
            }.launchIn(viewModelScope)
    }

    private fun observePreferenceChanges() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences
                .drop(1)
                .collect {
                    val state = _uiState.value
                    _uiState.update { it.copy(pokemonList = emptyList(), currentPage = 0) }
                    handleFilterChange(state.searchQuery, state.selectedGenerationId)
                }
        }
    }

    private fun handleFilterChange(query: String, generationId: Int?) {
        val state = _uiState.value
        val category = state.selectedDexCategory

        when {
            query.isNotEmpty() -> {
                searchPokemon(query, category, generationId, state.selectedFormRegion)
            }
            category == DexCategory.FORMS -> {
                loadFormRegion(state.selectedFormRegion)
            }
            generationId != null -> {
                loadGeneration(generationId, category)
            }
            else -> {
                if (category.isPaginated) {
                    _uiState.update { it.copy(pokemonList = emptyList(), currentPage = 0, canLoadMore = true) }
                    loadPokemonPage(0)
                } else {
                    loadAllByCategory(category)
                }
            }
        }
    }

    private fun loadGenerations() {
        viewModelScope.launch {
            ensureGenerationsLoaded()
        }
    }

    private fun loadPokemonPage(page: Int) {
        loadJob?.cancel()
        val category = _uiState.value.selectedDexCategory
        loadJob = viewModelScope.launch {
            pokemonRepository.getPokemonList(page, Constants.PAGE_SIZE, category).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update {
                            if (page == 0) it.copy(isLoading = true, error = null)
                            else it.copy(isLoadingMore = true)
                        }
                    }
                    is Resource.Success -> {
                        val newPokemon = resource.data ?: emptyList()
                        _uiState.update {
                            val updatedList = if (page == 0) newPokemon
                            else (it.pokemonList + newPokemon).distinctBy { p -> p.id }
                            it.copy(
                                pokemonList = updatedList,
                                isLoading = false,
                                isLoadingMore = false,
                                error = null,
                                currentPage = page,
                                canLoadMore = newPokemon.size >= Constants.PAGE_SIZE
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, isLoadingMore = false, error = resource.message)
                        }
                    }
                }
            }
        }
    }

    private fun loadAllByCategory(category: DexCategory) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            pokemonRepository.getAllByCategory(category).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(pokemonList = resource.data ?: emptyList(), isLoading = false, isLoadingMore = false, canLoadMore = false)
                        }
                    }
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, isLoadingMore = false) }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, isLoadingMore = false, error = resource.message) }
                }
            }
        }
    }

    private fun searchPokemon(query: String, category: DexCategory, generationId: Int?, formRegion: String?) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            pokemonRepository.searchPokemon(query, category, generationId, formRegion).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(pokemonList = resource.data ?: emptyList(), isLoading = false, isLoadingMore = false, canLoadMore = false)
                        }
                    }
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, isLoadingMore = false) }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, isLoadingMore = false, error = resource.message) }
                }
            }
        }
    }

    private fun loadGeneration(generationId: Int, category: DexCategory) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            pokemonRepository.getPokemonByGeneration(generationId, category).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(pokemonList = resource.data ?: emptyList(), isLoading = false, isLoadingMore = false, canLoadMore = false)
                        }
                    }
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, isLoadingMore = false) }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, isLoadingMore = false, error = resource.message) }
                }
            }
        }
    }

    private fun loadFormRegion(formRegion: String?) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            pokemonRepository.getPokemonByFormRegion(formRegion).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(pokemonList = resource.data ?: emptyList(), isLoading = false, isLoadingMore = false, canLoadMore = false)
                        }
                    }
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, isLoadingMore = false) }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, isLoadingMore = false, error = resource.message) }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    fun onGenerationSelected(generationId: Int?) {
        _uiState.update {
            it.copy(
                selectedGenerationId = generationId,
                pokemonList = emptyList(),
                currentPage = 0,
                canLoadMore = generationId == null && it.searchQuery.isEmpty()
            )
        }
        _selectedGeneration.value = generationId
    }

    fun onDexCategorySelected(category: DexCategory) {
        _uiState.update {
            it.copy(
                selectedDexCategory = category,
                pokemonList = emptyList(),
                currentPage = 0,
                canLoadMore = category.isPaginated,
                selectedGenerationId = null,
                selectedFormRegion = null,
                searchQuery = ""
            )
        }
        _searchQuery.value = ""
        _selectedGeneration.value = null

        when {
            category.isPaginated -> loadPokemonPage(0)
            category == DexCategory.FORMS -> loadFormRegion(null)
            else -> loadAllByCategory(category)
        }
    }

    fun onFormRegionSelected(formRegion: String?) {
        _uiState.update {
            it.copy(
                selectedFormRegion = formRegion,
                pokemonList = emptyList(),
                canLoadMore = false
            )
        }
        if (_uiState.value.searchQuery.isNotEmpty()) {
            searchPokemon(_uiState.value.searchQuery, DexCategory.FORMS, null, formRegion)
        } else {
            loadFormRegion(formRegion)
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.canLoadMore ||
            state.searchQuery.isNotEmpty() || state.selectedGenerationId != null ||
            state.selectedDexCategory == DexCategory.FORMS) return
        loadPokemonPage(state.currentPage + 1)
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            try {
                pokemonRepository.refreshPokemonData()
                generationRepository.refreshGenerations()
            } catch (_: Exception) {}
            _uiState.update {
                it.copy(
                    isRefreshing = false, pokemonList = emptyList(),
                    currentPage = 0, canLoadMore = true,
                    selectedGenerationId = null, selectedFormRegion = null,
                    searchQuery = "", selectedDexCategory = DexCategory.NATIONAL
                )
            }
            _searchQuery.value = ""
            _selectedGeneration.value = null
            ensureGenerationsLoaded()
            precacheNames()
            loadPokemonPage(0)
        }
    }

    fun retry() {
        _uiState.update { it.copy(error = null) }
        val state = _uiState.value
        handleFilterChange(state.searchQuery, state.selectedGenerationId)
    }
}
