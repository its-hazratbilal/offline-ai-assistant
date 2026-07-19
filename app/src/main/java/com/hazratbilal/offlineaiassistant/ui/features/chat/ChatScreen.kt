package com.hazratbilal.offlineaiassistant.ui.features.chat

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hazratbilal.offlineaiassistant.R
import com.hazratbilal.offlineaiassistant.domain.model.ChatMessage
import com.hazratbilal.offlineaiassistant.domain.model.ChatSession
import com.hazratbilal.offlineaiassistant.ui.common.fadeTopEdge
import com.hazratbilal.offlineaiassistant.utils.FileDownloader
import com.hazratbilal.offlineaiassistant.utils.MarkdownStripper
import com.mikepenz.markdown.m3.Markdown
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun ChatScreen(
    onSettingsClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val clipboard = LocalClipboard.current

    var showOptionsMenu by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var playingMessageId by remember { mutableStateOf<Long?>(null) }

    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    BackHandler {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastBackPressTime < 2000) {
            (context as? Activity)?.finish()
        } else {
            lastBackPressTime = currentTime

            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    message = "Press back again to exit",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    DisposableEffect(Unit) {
        val textToSpeech = TextToSpeech(context) { }
        textToSpeech.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {

                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    playingMessageId = null
                }

                override fun onError(utteranceId: String?) {
                    playingMessageId = null
                }
            }
        )

        tts = textToSpeech

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    fun speak(messageId: Long, text: String) {

        if (playingMessageId == messageId) {
            tts?.stop()
            playingMessageId = null
            return
        }

        tts?.stop()

        playingMessageId = messageId

        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            messageId.toString()
        )
    }

    val downloader = remember {
        FileDownloader(context)
    }

    fun shareMessage(message: ChatMessage) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, MarkdownStripper.toPlainText(message.response))
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "Share message")
        )
    }

    fun shareChat(messages: List<ChatMessage>) {
        val text = buildString {
            messages.forEach { message ->
                appendLine("You:")
                appendLine(message.request)
                appendLine()
                appendLine("AI:")
                appendLine(MarkdownStripper.toPlainText(message.response))
                appendLine()
                appendLine("----------------------------------------")
                appendLine()
            }
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        context.startActivity(
            Intent.createChooser(shareIntent, "Share chat")
        )
    }

    LaunchedEffect(state.error) {
        state.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorShown()
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val text = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
        if (!text.isNullOrBlank()) {
            viewModel.onMessageChanged(text)
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
            }
            speechLauncher.launch(intent)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        DismissibleNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DismissibleDrawerSheet {
                    ChatDrawerContent(
                        sessions = state.sessions,
                        currentSessionId = state.currentSessionId,
                        onNewChatClick = {
                            viewModel.onNewChatClick()
                            scope.launch { drawerState.close() }
                        },
                        onPrivateChatClick = {
                            viewModel.onPrivateChatClick()
                            scope.launch { drawerState.close() }
                        },
                        onSessionClick = { session ->
                            viewModel.onSessionSelected(session)
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }

                    Text(
                        text = state.activeModelName ?: "Offline AI Assistant",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }

                    if (state.messages.isNotEmpty()) {
                        Box {
                            IconButton(onClick = { showOptionsMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Options")
                            }
                            DropdownMenu(
                                expanded = showOptionsMenu,
                                onDismissRequest = { showOptionsMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Delete Chat") },
                                    onClick = {
                                        showOptionsMenu = false
                                        viewModel.onDeleteChatClick()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Download Chat") },
                                    onClick = {
                                        showOptionsMenu = false

                                        viewModel.downloadChat { file ->
                                            scope.launch {
                                                if (downloader.saveToDownloads(file)) {
                                                    snackbarHostState.showSnackbar("Chat saved to Downloads")
                                                } else {
                                                    snackbarHostState.showSnackbar("Failed to save chat")
                                                }
                                            }
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Share Chat") },
                                    onClick = {
                                        showOptionsMenu = false
                                        shareChat(state.messages)
                                    }
                                )
                            }
                        }
                    }
                }

                if (state.isModelLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier.size(42.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.fillMaxSize(),
                                    strokeWidth = 2.dp
                                )

                                Image(
                                    painter = painterResource(R.drawable.logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Text(
                                text = "Loading model...",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                } else {
                    val showEmptyState = state.messages.isEmpty() &&
                            state.pendingUserMessage == null &&
                            state.assistantGreeting == null &&
                            state.selectedLabel == ChatLabel.GENERAL

                    if (showEmptyState) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Ask me anything",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                state.activeModelName?.let { modelName ->
                                    Text(
                                        text = buildAnnotatedString {
                                            append("Chatting with ")
                                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(modelName)
                                            }
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(4.dp))
                                }
                                Text(
                                    text = "Your conversation stays fully offline on this device.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                                .fadeTopEdge(16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            state.assistantGreeting?.let { greeting ->
                                item {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Image(
                                            painter = painterResource(id = R.drawable.logo),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = greeting,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            items(state.messages) { chatMessage ->
                                ChatMessageBubble(
                                    message = chatMessage,
                                    isPlaying = playingMessageId == chatMessage.id,
                                    onCopy = { text ->
                                        scope.launch {
                                            clipboard.setClipEntry(
                                                ClipEntry(
                                                    ClipData.newPlainText(
                                                        "message",
                                                        MarkdownStripper.toPlainText(text)
                                                    )
                                                )
                                            )
                                        }
                                    },
                                    onPlay = {
                                        speak(chatMessage.id, chatMessage.response)
                                    },
                                    onDownload = { msg ->
                                        viewModel.downloadMessage(msg) { file ->
                                            scope.launch {
                                                if (downloader.saveToDownloads(file)) {
                                                    snackbarHostState.showSnackbar("Message saved to Downloads")
                                                } else {
                                                    snackbarHostState.showSnackbar("Failed to save message")
                                                }
                                            }
                                        }
                                    },
                                    onShare = { msg -> shareMessage(msg) },
                                )
                            }

                            state.pendingUserMessage?.let { pendingText ->
                                item {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        BoxWithConstraints(Modifier.fillMaxWidth()) {
                                            Card(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .widthIn(max = maxWidth * 0.9f),
                                                shape = MaterialTheme.shapes.medium,
                                            ) {
                                                Text(
                                                    text = pendingText,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    modifier = Modifier.padding(
                                                        horizontal = 14.dp,
                                                        vertical = 10.dp
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(8.dp))

                                        when {
                                            state.isThinking -> {
                                                var dotCount by remember { mutableIntStateOf(1) }

                                                LaunchedEffect(Unit) {
                                                    while (true) {
                                                        delay(500)
                                                        dotCount = (dotCount % 3) + 1
                                                    }
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Image(
                                                        painter = painterResource(id = R.drawable.logo),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(
                                                        text = "Thinking" + ".".repeat(dotCount),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            state.pendingAiText != null -> {
                                                Text(
                                                    text = state.pendingAiText!!,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)
                            ),
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 14.dp,
                                    top = 18.dp
                                )
                            ) {
                                BasicTextField(
                                    value = state.message,
                                    onValueChange = viewModel::onMessageChanged,
                                    enabled = !state.isLoading,
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.Sentences,
                                        autoCorrectEnabled = true,
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Default
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 24.dp, max = 140.dp),
                                    decorationBox = { innerTextField ->
                                        if (state.message.isEmpty()) {
                                            Text(
                                                text = "Write a message...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        innerTextField()
                                    }
                                )

                                Spacer(Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AssistChip(
                                        onClick = viewModel::onShowLabelDialog,
                                        label = {
                                            Text(
                                                text = state.selectedLabel.displayName,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Outlined.AutoAwesome,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    )

                                    Spacer(Modifier.weight(1f))

                                    when {
                                        state.isLoading -> {
                                            FilledIconButton(onClick = viewModel::stopGeneration) {
                                                Icon(
                                                    Icons.Default.Stop,
                                                    contentDescription = "Stop generating"
                                                )
                                            }
                                        }

                                        state.message.isBlank() -> {
                                            FilledTonalIconButton(
                                                onClick = {
                                                    micPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Mic,
                                                    contentDescription = "Voice input"
                                                )
                                            }
                                        }

                                        else -> {
                                            FilledIconButton(onClick = viewModel::sendMessage) {
                                                Icon(
                                                    Icons.Default.ArrowUpward,
                                                    contentDescription = "Send"
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    if (state.showLabelDialog) {
        LabelSelectionDialog(
            selectedLabel = state.selectedLabel,
            isActiveModelLightweight = state.isActiveModelLightweight,
            onLabelSelected = viewModel::onLabelSelected,
            onDismiss = viewModel::onDismissLabelDialog
        )
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessage,
    isPlaying: Boolean,
    onCopy: (String) -> Unit,
    onPlay: () -> Unit,
    onDownload: (ChatMessage) -> Unit,
    onShare: (ChatMessage) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        BoxWithConstraints(Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .widthIn(max = maxWidth * 0.9f),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    text = message.request,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (message.response.isNotBlank()) {
            Markdown(
                content = message.response
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                MessageActionIcon(Icons.Outlined.ContentCopy, "Copy") { onCopy(message.response) }
                MessageActionIcon(
                    icon = if (isPlaying) Icons.Default.Stop else Icons.AutoMirrored.Outlined.VolumeUp,
                    description = if (isPlaying) "Stop" else "Play"
                ) {
                    onPlay()
                }
                MessageActionIcon(Icons.Outlined.Download, "Download") { onDownload(message) }
                MessageActionIcon(Icons.Outlined.Share, "Share") { onShare(message) }
            }
        }
    }
}

@Composable
private fun MessageActionIcon(icon: ImageVector, description: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ChatDrawerContent(
    sessions: List<ChatSession>,
    currentSessionId: Long?,
    onNewChatClick: () -> Unit,
    onPrivateChatClick: () -> Unit,
    onSessionClick: (ChatSession) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
    ) {

        Spacer(Modifier.height(16.dp))
        Text("Offline AI Assistant", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)
                .clickable { onNewChatClick() }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text("New Chat", style = MaterialTheme.typography.bodyLarge)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = MaterialTheme.shapes.medium)
                .clickable { onPrivateChatClick() }
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Lock, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text("Private Chat", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            thickness = 0.8.dp
        )
        Spacer(Modifier.height(16.dp))

        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Once you start chatting, your conversations will appear here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            Text(
                text = "Recent Chats",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            val grouped = groupSessionsByRecency(sessions)

            LazyColumn(modifier = Modifier.weight(1f)) {
                grouped.forEach { (groupLabel, groupSessions) ->
                    item {
                        Text(
                            text = groupLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    items(groupSessions) { session ->
                        val isSelected = session.id == currentSessionId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape = MaterialTheme.shapes.medium)
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.08f
                                    )
                                    else MaterialTheme.colorScheme.surface,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .clickable { onSessionClick(session) }
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                        ) {
                            Text(
                                text = session.title,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelSelectionDialog(
    selectedLabel: ChatLabel,
    isActiveModelLightweight: Boolean,
    onLabelSelected: (ChatLabel) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Choose Mode", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                ChatLabel.entries.forEach { label ->
                    val showHint = isActiveModelLightweight &&
                            (label == ChatLabel.CODE || label == ChatLabel.RESUME)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = MaterialTheme.shapes.medium)
                            .clickable { onLabelSelected(label) }
                            .background(
                                color = if (label == selectedLabel) MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.08f
                                )
                                else Color.Transparent
                            )
                            .padding(vertical = 10.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = label.icon,
                            contentDescription = null,
                            tint = if (label == selectedLabel) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = label.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (label == selectedLabel) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                            if (showHint) {
                                Text(
                                    text = "Works best with Gemma 3 or larger",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

private fun groupSessionsByRecency(sessions: List<ChatSession>): Map<String, List<ChatSession>> {
    val now = Calendar.getInstance()

    fun isSameDay(a: Calendar, b: Calendar) =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) && a.get(Calendar.DAY_OF_YEAR) == b.get(
            Calendar.DAY_OF_YEAR
        )

    fun labelFor(timestamp: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = timestamp }
        val daysDiff = ((now.timeInMillis - cal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
        return when {
            isSameDay(now, cal) -> "Today"
            daysDiff <= 1 -> "Yesterday"
            daysDiff <= 7 -> "This Week"
            else -> "Older"
        }
    }

    val order = listOf("Today", "Yesterday", "This Week", "Older")
    return sessions.groupBy { labelFor(it.updatedAt) }.toSortedMap(compareBy { order.indexOf(it) })
}