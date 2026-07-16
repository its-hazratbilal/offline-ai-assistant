package com.hazratbilal.offlineaiassistant.ui.features.model_selection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.hazratbilal.offlineaiassistant.data.download.DownloadState
import com.hazratbilal.offlineaiassistant.data.model.LlmModel
import com.hazratbilal.offlineaiassistant.ui.common.PrimaryButtonWithLoading
import com.hazratbilal.offlineaiassistant.ui.common.SecondaryButton
import com.hazratbilal.offlineaiassistant.ui.common.SecondaryIconButton

@Composable
fun ModelItem(
    model: LlmModel,
    cardState: ModelCardState,
    isSwitching: Boolean,
    isSelected: Boolean,
    isAnyDownloadActive: Boolean,
    onDownloadClick: () -> Unit,
    onRetryClick: () -> Unit,
    onUseModelClick: () -> Unit,
    onHelp: () -> Unit
) {
    val downloadState = cardState.downloadState
    val isDownloading = downloadState is DownloadState.InProgress
    val isError = downloadState is DownloadState.Error

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            0.6.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (model.recommended) {
                        InfoBadge(
                            text = "Recommended",
                            icon = Icons.Default.Star
                        )
                    }
                }

                SecondaryIconButton(
                    Icons.Default.Info,
                    "Model info",
                    onClick = onHelp,
                    modifier = Modifier.size(22.dp),
                    iconSize = 14.dp
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = model.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoBadge(
                    text = model.downloadSize,
                    icon = Icons.Default.FileDownload
                )

                InfoBadge(
                    text = "${model.ramRequirement} RAM",
                    icon = Icons.Default.Memory,
                )

                if (cardState.isDownloaded) {
                    InfoBadge(
                        text = "Downloaded",
                        icon = Icons.Default.CheckCircle,
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    InfoBadge(
                        text = "Not Downloaded",
                        icon = Icons.Default.Clear
                    )
                }

            }

            Spacer(Modifier.height(12.dp))

            if (isError) {
                Text(
                    text = downloadState.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(8.dp))
            }

            if (isDownloading) {
                Column {
                    LinearProgressIndicator(
                        progress = { downloadState.progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        drawStopIndicator = {}
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "%d%% • %.1f MB / %.1f MB".format(
                            downloadState.progressPercent,
                            downloadState.downloadedMB,
                            downloadState.totalMB
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                when {
                    cardState.isDownloaded -> {
                        if (isSelected) {
                            Button(
                                onClick = { },
                                enabled = false,
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Selected", style = MaterialTheme.typography.labelMedium)
                            }
                        } else {
                            PrimaryButtonWithLoading(
                                text = "Select",
                                onClick = onUseModelClick,
                                isLoading = isSwitching,
                            )
                        }
                    }

                    isError -> {
                        SecondaryButton("Retry Download", onClick = onRetryClick, isEnabled = !isAnyDownloadActive)
                    }

                    else -> {
                        SecondaryButton("Download", onClick = onDownloadClick, isEnabled = !isAnyDownloadActive)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBadge(
    text: String,
    icon: ImageVector? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = containerColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(3.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor
            )
        }
    }
}