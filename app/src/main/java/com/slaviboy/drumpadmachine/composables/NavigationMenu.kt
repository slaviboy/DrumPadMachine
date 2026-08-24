package com.slaviboy.drumpadmachine.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.slaviboy.composeunits.DeviceHeight
import com.slaviboy.composeunits.DeviceWidth
import com.slaviboy.composeunits.Density
import com.slaviboy.composeunits.ScaleDensity
import com.slaviboy.composeunits.dh
import com.slaviboy.composeunits.dw
import com.slaviboy.composeunits.sw
import com.slaviboy.drumpadmachine.R
import com.slaviboy.drumpadmachine.data.MenuItem
import com.slaviboy.drumpadmachine.extensions.bounceClick
import com.slaviboy.drumpadmachine.ui.RobotoFont

@Composable
fun NavigationMenu(
    boxScope: BoxScope,
    menuItems: List<MenuItem>,
    onItemClick: (MenuItem) -> Unit
) = with(boxScope) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.075.dh)
            .background(
                Color(0xFF19182C)
            )
            .align(Alignment.BottomCenter),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        menuItems.forEach {
            val color = if (it.isSelected) {
                Color(0xFFffd112)
            } else {
                Color(0xFF444350)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .bounceClick {
                        onItemClick(it)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = it.iconResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(0.09.dw),
                    colorFilter = ColorFilter.tint(color)
                )
                Text(
                    text = stringResource(id = it.titleResId).uppercase(),
                    color = color,
                    fontFamily = RobotoFont,
                    fontSize = 0.024.sw,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF232339)
@Composable
private fun NavigationMenuPreview() {
    DeviceWidth = 1080f
    DeviceHeight = 2400f
    Density = 3f
    ScaleDensity = 3f
    Box(modifier = Modifier.fillMaxSize()) {
        NavigationMenu(
            boxScope = this,
            menuItems = listOf(
                MenuItem(isSelected = true, iconResId = R.drawable.ic_home, titleResId = R.string.main),
                MenuItem(isSelected = false, iconResId = R.drawable.ic_vinyl, titleResId = R.string.my_music),
                MenuItem(isSelected = false, iconResId = R.drawable.ic_more, titleResId = R.string.more)
            ),
            onItemClick = {}
        )
    }
}