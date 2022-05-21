package com.example.newsapitest.presentation.ui

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.newsapitest.presentation.MainViewModel
import timber.log.Timber

@Composable
fun FavouritesList(
    modifier: Modifier,
    viewModel: MainViewModel,
) {

    val favItemsState by viewModel.favoritesLiveData.observeAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    Timber.d("favItemsState: ${favItemsState}")
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
        reverseLayout = false,
        modifier = modifier,
    ) {
        items(favItemsState?.toList() ?: arrayListOf(), itemContent = {
            CreateArticleComposable(
                article = it,
                onShareClick = {
                    val i = Intent(Intent.ACTION_SEND)
                    i.type = "text/plain"
                    i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL")
                    i.putExtra(Intent.EXTRA_TEXT, it)
                    context.startActivity(Intent.createChooser(i, "Share URL"))
                },
                onDownloadImageClick = { url ->
                    viewModel.saveFileToPictures(url)
                },
                onFavoriteClick = { id ->
                    viewModel.onLikeClicked(id)
                }
            )
            Divider(color = Color.Red.copy(alpha = 0f), thickness = 16.dp)
        }
        )
    }
}