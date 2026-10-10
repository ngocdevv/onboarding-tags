package com.ngocdevv.onboardingtags.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ngocdevv.onboardingtags.R
import com.ngocdevv.onboardingtags.ui.theme.BottomTabInset
import com.ngocdevv.onboardingtags.ui.theme.MaxContentWidth
import com.ngocdevv.onboardingtags.ui.theme.Palette
import com.ngocdevv.onboardingtags.ui.theme.Spacing
import com.ngocdevv.onboardingtags.ui.theme.palette

/** Port of `src/app/explore.tsx`. */
@Composable
fun ExploreScreen() {
    val theme = palette()
    val uriHandler = LocalUriHandler.current

    Box(
        Modifier
            .fillMaxSize()
            .background(theme.background)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            Modifier
                .widthIn(max = MaxContentWidth)
                .fillMaxWidth()
                .statusBarsPadding(),
        ) {
            // Title
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.four, vertical = Spacing.six),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.three),
            ) {
                Text("Explore", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = theme.text)
                Text(
                    "This starter app includes example\ncode to help you get started.",
                    textAlign = TextAlign.Center,
                    color = theme.textSecondary,
                )
                Row(
                    Modifier
                        .clip(RoundedCornerShape(Spacing.five))
                        .background(theme.backgroundElement)
                        .clickable { uriHandler.openUri("https://docs.expo.dev") }
                        .padding(horizontal = Spacing.four, vertical = Spacing.two),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.one),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Expo documentation", fontWeight = FontWeight.SemiBold, color = theme.text)
                    Icon(Icons.Outlined.Link, contentDescription = null, tint = theme.text, modifier = Modifier.size(12.dp))
                }
            }

            // Sections
            Column(
                Modifier
                    .padding(horizontal = Spacing.four)
                    .padding(top = Spacing.three, bottom = BottomTabInset + Spacing.three),
                verticalArrangement = Arrangement.spacedBy(Spacing.five),
            ) {
                Collapsible("File-based routing", theme) {
                    SmallText(richText("This app has two screens: ", Code("src/app/index.tsx"), " and ", Code("src/app/explore.tsx")), theme)
                    SmallText(richText("The layout file in ", Code("src/app/_layout.tsx"), " sets up the tab navigator."), theme)
                    LearnMore("https://docs.expo.dev/router/introduction")
                }

                Collapsible("Android, iOS, and web support", theme) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Spacing.three))
                            .background(theme.backgroundElement)
                            .padding(Spacing.three),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.two),
                    ) {
                        SmallText(
                            richText(
                                "You can open this project on Android, iOS, and the web. To open the web version, press ",
                                Bold("w"), " in the terminal running this project.",
                            ),
                            theme,
                        )
                        Image(
                            painter = painterResource(R.drawable.tutorial_web),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(296f / 171f)
                                .clip(RoundedCornerShape(Spacing.three)),
                        )
                    }
                }

                Collapsible("Images", theme) {
                    SmallText(
                        richText(
                            "For static images, you can use the ", Code("@2x"), " and ", Code("@3x"),
                            " suffixes to provide files for different screen densities.",
                        ),
                        theme,
                    )
                    Image(
                        painter = painterResource(R.drawable.react_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally),
                    )
                    LearnMore("https://reactnative.dev/docs/images")
                }

                Collapsible("Light and dark mode components", theme) {
                    SmallText(
                        richText(
                            "This template has light and dark mode support. The ", Code("useColorScheme()"),
                            " hook lets you inspect what the user's current color scheme is, and so you can adjust UI colors accordingly.",
                        ),
                        theme,
                    )
                    LearnMore("https://docs.expo.dev/develop/user-interface/color-themes/")
                }

                Collapsible("Animations", theme) {
                    SmallText(
                        richText(
                            "This template includes an example of an animated component. The ",
                            Code("src/components/ui/collapsible.tsx"), " component uses the powerful ",
                            Code("react-native-reanimated"), " library to animate opening this hint.",
                        ),
                        theme,
                    )
                }
            }
        }
    }
}

/** Port of `src/components/ui/collapsible.tsx`. */
@Composable
private fun Collapsible(title: String, theme: Palette, content: @Composable ColumnScope.() -> Unit) {
    var isOpen by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isOpen) 90f else 0f, label = "chevron")

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.two)) {
        Row(
            Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { isOpen = !isOpen },
            horizontalArrangement = Arrangement.spacedBy(Spacing.two),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = theme.text,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(rotation),
            )
            Text(title, fontWeight = FontWeight.SemiBold, color = theme.text)
        }
        AnimatedVisibility(visible = isOpen) {
            Column(
                Modifier.padding(start = Spacing.four),
                verticalArrangement = Arrangement.spacedBy(Spacing.two),
                content = content,
            )
        }
    }
}

@Composable
private fun SmallText(text: AnnotatedString, theme: Palette) {
    Text(text, fontSize = 14.sp, lineHeight = 20.sp, color = theme.text)
}

@Composable
private fun LearnMore(url: String) {
    val uriHandler = LocalUriHandler.current
    Text(
        "Learn more",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF208AEF),
        modifier = Modifier.clickable { uriHandler.openUri(url) },
    )
}

private class Code(val text: String)
private class Bold(val text: String)

private fun richText(vararg parts: Any): AnnotatedString = buildAnnotatedString {
    for (part in parts) {
        when (part) {
            is Code -> withStyle(SpanStyle(fontFamily = FontFamily.Monospace)) { append(part.text) }
            is Bold -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(part.text) }
            else -> append(part.toString())
        }
    }
}
