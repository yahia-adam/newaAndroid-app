package com.bythewayapp.ui.componets

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySearchBar(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onShowFilter: () -> Unit = {},
) {
    var query by remember { mutableStateOf("") }

    SearchBar(
        modifier = modifier.padding(bottom = 50.dp),
        query = query,
        onQueryChange = { query = it },
        onSearch = {
            // Handle search submission here
            onActiveChange(false)
        },
        active = isActive,
        onActiveChange = onActiveChange,
        placeholder = { Text("Search...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (isActive) {
                IconButton(onClick = {
                    // Close the search bar completely
                    query = ""
                    onActiveChange(false)
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close search"
                    )
                }
            } else {
                IconButton(
                    onClick = { onShowFilter() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Filter options"
                    )
                }
            }
        }
    ) {
        // Search suggestions or results go here
        // For example:
        // SearchSuggestions(query = query)
    }
}
