package com.v2ray.ang.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R
import com.v2ray.ang.ui.compose.AppDivider
import com.v2ray.ang.ui.compose.colorFabInactiveDark
import com.v2ray.ang.ui.compose.colorFabInactiveLight
import com.v2ray.ang.ui.compose.colorPing

@Composable
fun MainBottomBar(
    displayText: String,
    isRunning: Boolean,
    isDarkTheme: Boolean,
    onAction: (MainAction) -> Unit
) {
    val idleColor = if (isDarkTheme) colorFabInactiveDark else colorFabInactiveLight

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        AppDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(onClick = { onAction(MainAction.TestCurrentServer) })
                    .padding(vertical = 6.dp)
                    .semantics { contentDescription = displayText },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isRunning) colorPing else idleColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Button(
                onClick = { onAction(MainAction.ToggleService) },
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    contentColor = if (isRunning) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                )
            ) {
                Text(
                    text = stringResource(if (isRunning) R.string.cheng_disconnect else R.string.cheng_connect),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
