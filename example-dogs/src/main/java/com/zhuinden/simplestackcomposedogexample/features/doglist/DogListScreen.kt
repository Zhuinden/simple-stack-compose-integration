package com.zhuinden.simplestackcomposedogexample.features.doglist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.size.PixelSize
import com.zhuinden.simplestackcomposedogexample.core.models.contentDescription
import com.zhuinden.simplestackcomposedogexample.data.models.Dog
import com.zhuinden.simplestackcomposedogexample.features.dogdetail.DogDetailKey
import com.zhuinden.simplestackcomposeintegration.core.LocalBackstack
import okhttp3.HttpUrl

@Composable
fun DogListScreen(dogs: List<Dog>?) {
    @Composable
    fun DogItem(dog: Dog) {
        val context = LocalContext.current
        val backstack = LocalBackstack.current

        var fullWidth by remember { mutableStateOf(0) }

        val density = LocalDensity.current

        Layout(content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                if (fullWidth == 0) {
                    CircularProgressIndicator()
                } else {
                    val painter = rememberImagePainter(ImageRequest.Builder(context)
                        .size(density.run { PixelSize(fullWidth, 160.dp.toPx().toInt()) })
                        .data(HttpUrl.parse(dog.imageUrl))
                        .build())

                    Image(
                        painter = painter,
                        contentDescription = dog.contentDescription(),
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.clickable {
                            backstack.goTo(DogDetailKey(dog))
                        }.width(density.run { fullWidth.toDp() }),
                    )
                }
            }
        }, measurePolicy = { measurables, constraints ->
            val placeables = measurables.fastMap { it.measure(constraints) }
            val maxWidth = placeables.fastMaxBy { it.width }?.width ?: 0
            val maxHeight = placeables.fastMaxBy { it.height }?.height ?: 0

            if (fullWidth == 0) {
                fullWidth = maxWidth
            }

            layout(maxWidth, maxHeight) {
                placeables.fastForEach { placeable ->
                    placeable.place(0, 0)
                }
            }
        })
    }

    @Composable
    fun DogList(dogs: List<Dog>) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize(), content = {
                this.items(dogs.size, itemContent = { index ->
                    DogItem(dogs[index])
                })
            })
        }
    }

    @Composable
    fun LoadingIndicator() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    @Composable
    fun Header() {
        TopAppBar(modifier = Modifier.fillMaxWidth(), title = {
            Row {
                Text("Adopt A Dog")
            }
        })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Header()

        if (dogs == null) {
            LoadingIndicator()
        } else {
            DogList(dogs)
        }
    }
}


