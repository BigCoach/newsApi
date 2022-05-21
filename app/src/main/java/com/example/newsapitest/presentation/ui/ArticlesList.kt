package com.example.newsapitest.presentation.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.newsapitest.data.model.Article
import com.example.newsapitest.presentation.MainViewModel
import com.skydoves.landscapist.glide.GlideImage
import timber.log.Timber


private const val TAG_ARTICLES_LIST = "TAG_ARTICLES_LIST"

@ExperimentalPagingApi
@Composable
fun ArticlesList(
    modifier: Modifier,
    viewModel: MainViewModel,
) {
    Timber.tag(TAG_ARTICLES_LIST).d("ArticleList starts")
    val lazyArticleItems: LazyPagingItems<Article> =
        viewModel.articlesPagingFlow?.collectAsLazyPagingItems() ?: return
    Timber.tag(TAG_ARTICLES_LIST)
        .d("lazyArticleItems itemSnapshotList size: ${lazyArticleItems.itemSnapshotList.size}")
    Timber.tag(TAG_ARTICLES_LIST)
        .d("lazyArticleItems itemSnapshotList: ${lazyArticleItems.itemSnapshotList}")
    val listState = rememberLazyListState()

    val context = LocalContext.current

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
        reverseLayout = false,
        modifier = modifier,
        content = {
            itemsIndexed(lazyArticleItems) { index, article ->
                article?.let {
                    key(it.localId) {
                        CreateArticleComposable(
                            article = article,
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
                }
            }
            lazyArticleItems.apply {
                Timber.tag(TAG_ARTICLES_LIST).d("loadState: $loadState")
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { LoadingView(modifier = Modifier.fillParentMaxSize()) }
                    }
                    loadState.append is LoadState.Loading -> {
                        item { LoadingItem() }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val e = lazyArticleItems.loadState.refresh as LoadState.Error
                        item {
                            ErrorItem(
                                message = e.error.localizedMessage!!,
                                modifier = Modifier.fillParentMaxSize(),
                                onClickRetry = { retry() }
                            )
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        val e = lazyArticleItems.loadState.append as LoadState.Error
                        item {
                            ErrorItem(
                                message = e.error.localizedMessage!!,
                                onClickRetry = { retry() }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CreateArticleComposable(
    article: Article,
    onShareClick: (String) -> Unit,
    onDownloadImageClick: (String) -> Unit,
    onFavoriteClick: (Long) -> Unit,
) {
    Timber.d("Article: ${article.title}")

    Card(
        Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(), elevation = 12.dp
    ) {
        Column(Modifier.padding(8.dp)) {
            GlideImage(
                imageModel = article.urlToImage,
                requestOptions = {
                    RequestOptions()
                        .override(500, 500)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(296.dp)
                    .clip(RoundedCornerShape(4.dp)),
                alignment = Alignment.CenterStart,
                loading = {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val indicator = createRef()
                        CircularProgressIndicator(
                            modifier = Modifier.constrainAs(indicator) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                        )
                    }
                },
                failure = {
                    Text(text = "image request failed.")
                }
            )
            Text(
                text = article.publishedAt ?: "",
                modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                color = Color.DarkGray,
                style = TextStyle(color = Color.DarkGray, fontSize = 12.sp)
            )
            Text(
                text = article.title ?: "",
                fontSize = 26.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
            Text(
                text = article.description ?: "", modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (share, download, like) = createRefs()

                Text(text = "Share",
                    color = Color.White,
                    modifier = Modifier
                        .constrainAs(share){
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        }
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onShareClick(article.url ?: "")
                        }
                        .background(Color.Blue.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp, horizontal = 16.dp)

                )
                Text(text = "Download Image",
                    color = Color.White,
                    modifier = Modifier.constrainAs(download){
                        top.linkTo(parent.top)
                        start.linkTo(share.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onDownloadImageClick(article.urlToImage ?: "")
                    }
                    .background(Color.Blue.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)

                )
                val favIcon = if(!article.inFavorites) Icons.Outlined.FavoriteBorder else Icons.Outlined.Favorite
                Icon(
                    favIcon,
                    contentDescription = "favorites",
                    tint = Color.White,
                    modifier = Modifier
                        .constrainAs(like){
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                        .padding(8.dp)
                        .clickable {
                            onFavoriteClick(article.localId)
                        }
                        .background(Color.Blue.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                        .padding(vertical = 8.dp, horizontal = 16.dp))

            }
        }
    }
}
