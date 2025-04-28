package com.bythewayapp.ui.componets

import androidx.compose.foundation.layout.fillMaxWidth
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun MySearchBar(
    onShowFilter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier.fillMaxWidth(),
        query = query,
        onQueryChange = { query = it },
        onSearch = {
            // Handle search submission here
            active = false
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text("Search...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (active) {
                IconButton(onClick = {
                    // Close the search bar completely
                    query = ""
                    active = false
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
                        contentDescription = "Clear search"
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