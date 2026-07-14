package com.example.oshu_android.feature.auth.login

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oshu_android.R
import com.example.oshu_android.data.auth.GoogleOAuthCallbackResult
import com.example.oshu_android.data.auth.LocalGoogleOAuthCallbackIntake

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val googleOAuthCallbackIntake = LocalGoogleOAuthCallbackIntake.current
    val googleOAuthCallback by googleOAuthCallbackIntake.callbacks.collectAsState()
    val context = LocalContext.current
    val googleAuthorizationUrl = stringResource(
        R.string.google_authorization_url,
    )
    var googleAuthorizationError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(googleOAuthCallback) {
        when (val callback = googleOAuthCallback) {
            is GoogleOAuthCallbackResult.Success -> {
                googleOAuthCallbackIntake.consume()
                viewModel.loginWithGoogleTicket(callback.code)
            }

            GoogleOAuthCallbackResult.Failure.AuthorizationDenied -> {
                googleOAuthCallbackIntake.consume()
                googleAuthorizationError = "구글 로그인이 취소되었거나 실패했습니다. 다시 시도해주세요."
            }

            GoogleOAuthCallbackResult.Failure.InvalidCallback,
            GoogleOAuthCallbackResult.Failure.InvalidState -> {
                googleOAuthCallbackIntake.consume()
                googleAuthorizationError = "구글 로그인 정보를 확인할 수 없습니다. 다시 시도해주세요."
            }

            null -> Unit
        }
    }

    LaunchedEffect(
        uiState.isLoginSuccessful
    ) {
        if (uiState.isLoginSuccessful) {
            onLoginSuccess()
            viewModel.onLoginSuccessHandled()
        }
    }

    LoginScreen(
        uiState = uiState,
        onLoginIdChanged =
            viewModel::onLoginIdChanged,
        onPasswordChanged =
            viewModel::onPasswordChanged,
        onPasswordVisibilityClick =
            viewModel::onPasswordVisibilityChanged,
        onKeepLoggedInChanged =
            viewModel::onKeepLoggedInChanged,
        onLoginClick = viewModel::login,
        onGoogleLoginClick = {
            if (!uiState.isLoading) {
                googleAuthorizationError = when (
                    launchGoogleAuthorization(
                        authorizationUrl = googleAuthorizationUrl,
                        launch = { request ->
                            context.startActivity(
                                Intent(
                                    request.action,
                                    Uri.parse(request.uri),
                                ),
                            )
                        },
                    )
                ) {
                    GoogleAuthorizationLaunchResult.Launched -> null
                    GoogleAuthorizationLaunchResult.InvalidConfiguration ->
                        "Google 로그인을 준비할 수 없습니다. 잠시 후 다시 시도해주세요."
                }
            }
        },
        googleAuthorizationError = googleAuthorizationError,
        onSignUpClick = onSignUpClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onLoginIdChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordVisibilityClick: () -> Unit,
    onKeepLoggedInChanged: (Boolean) -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    googleAuthorizationError: String?,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val colorScheme = MaterialTheme.colorScheme

    val loginIdError =
        uiState.loginIdError

    val passwordError =
        uiState.passwordError
            ?: uiState.loginError

    val fieldColors =
        OutlinedTextFieldDefaults.colors(
            focusedTextColor =
                colorScheme.onSurface,
            unfocusedTextColor =
                colorScheme.onSurface,
            disabledTextColor =
                colorScheme.onSurface.copy(
                    alpha = 0.5f
                ),
            focusedContainerColor =
                colorScheme.surface,
            unfocusedContainerColor =
                colorScheme.surface,
            disabledContainerColor =
                colorScheme.surface,
            errorContainerColor =
                colorScheme.surface,
            focusedBorderColor =
                colorScheme.primary.copy(
                    alpha = 0.85f
                ),
            unfocusedBorderColor =
                colorScheme.outline.copy(
                    alpha = 0.42f
                ),
            disabledBorderColor =
                colorScheme.outline.copy(
                    alpha = 0.24f
                ),
            errorBorderColor =
                colorScheme.error.copy(
                    alpha = 0.85f
                ),
            cursorColor =
                colorScheme.primary,
            errorCursorColor =
                colorScheme.error,
            focusedPlaceholderColor =
                colorScheme.onSurfaceVariant.copy(
                    alpha = 0.8f
                ),
            unfocusedPlaceholderColor =
                colorScheme.onSurfaceVariant.copy(
                    alpha = 0.72f
                ),
        )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 28.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally,
        ) {
            Spacer(
                modifier = Modifier.height(110.dp)
            )

            Image(
                painter = painterResource(
                    R.drawable.img_logo_oshu
                ),
                contentDescription = "OSHU",
                modifier = Modifier
                    .width(240.dp)
                    .height(100.dp)
                    .graphicsLayer(
                        scaleX = 3.3f,
                        scaleY = 3.3f,
                        translationX = -40f,
                        translationY = 120f,
                    ),
                contentScale = ContentScale.Fit,
            )

            Spacer(
                modifier = Modifier.height(82.dp)
            )

            OutlinedTextField(
                value = uiState.loginId,
                onValueChange = onLoginIdChanged,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                singleLine = true,
                placeholder = {
                    Text(
                        text =
                            "아이디를 입력해주세요",
                        fontSize = 14.sp,
                    )
                },
                isError = loginIdError != null,
                supportingText =
                    loginIdError?.let { message ->
                        {
                            ErrorText(
                                message = message
                            )
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    keyboardType =
                        KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                shape = RoundedCornerShape(6.dp),
                colors = fieldColors,
            )

            Spacer(
                modifier = Modifier.height(
                    if (loginIdError == null) {
                        10.dp
                    } else {
                        2.dp
                    }
                )
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                singleLine = true,
                placeholder = {
                    Text(
                        text =
                            "비밀번호를 입력해주세요",
                        fontSize = 14.sp,
                    )
                },
                visualTransformation =
                    if (uiState.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                trailingIcon = {
                    IconButton(
                        onClick =
                            onPasswordVisibilityClick,
                        modifier =
                            Modifier.size(40.dp),
                        enabled = !uiState.isLoading,
                    ) {
                        Icon(
                            painter = painterResource(
                                if (
                                    uiState
                                        .isPasswordVisible
                                ) {
                                    R.drawable
                                        .ic_visibility_off
                                } else {
                                    R.drawable
                                        .ic_visibility
                                },
                            ),
                            contentDescription =
                                if (
                                    uiState
                                        .isPasswordVisible
                                ) {
                                    "비밀번호 숨기기"
                                } else {
                                    "비밀번호 보기"
                                },
                            modifier =
                                Modifier.size(21.dp),
                            tint =
                                colorScheme.primary,
                        )
                    }
                },
                isError = passwordError != null,
                supportingText =
                    passwordError?.let { message ->
                        {
                            ErrorText(
                                message = message
                            )
                        }
                    },
                keyboardOptions = KeyboardOptions(
                    keyboardType =
                        KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            onLoginClick()
                        },
                    ),
                shape = RoundedCornerShape(6.dp),
                colors = fieldColors,
            )

            Spacer(
                modifier = Modifier.height(
                    if (passwordError == null) {
                        4.dp
                    } else {
                        0.dp
                    }
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .toggleable(
                        value =
                            uiState.keepLoggedIn,
                        enabled =
                            !uiState.isLoading,
                        role = Role.Checkbox,
                        onValueChange =
                            onKeepLoggedInChanged,
                    ),
                horizontalArrangement =
                    Arrangement.End,
                verticalAlignment =
                    Alignment.CenterVertically,
            ) {
                CircularCheckIndicator(
                    checked =
                        uiState.keepLoggedIn,
                )

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = "로그인 상태 유지",
                    color = colorScheme.onSurface,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onLoginClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(10.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            colorScheme.primary,
                        contentColor =
                            colorScheme.onPrimary,
                        disabledContainerColor =
                            colorScheme.primary.copy(
                                alpha = 0.55f
                            ),
                        disabledContentColor =
                            colorScheme.onPrimary
                                .copy(alpha = 0.75f),
                    ),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(21.dp),
                        color =
                            colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = "로그인",
                        fontSize = 17.sp,
                        fontWeight =
                            FontWeight.SemiBold,
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedButton(
                onClick = onGoogleLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .semantics {
                        contentDescription = "Google로 계속하기"
                    },
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = colorScheme.outline.copy(
                        alpha = 0.72f,
                    ),
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFF1F3F4),
                    contentColor = colorScheme.onSurface,
                    disabledContainerColor = Color(0xFFF1F3F4).copy(
                        alpha = 0.58f,
                    ),
                    disabledContentColor = colorScheme.onSurface.copy(
                        alpha = 0.5f,
                    ),
                ),
            ) {
                GoogleMark()

                Spacer(
                    modifier = Modifier.width(8.dp),
                )

                Text(
                    text = "Google로 계속하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            googleAuthorizationError?.let { message ->
                ErrorText(
                    message = message,
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.Center,
                verticalAlignment =
                    Alignment.CenterVertically,
            ) {
                Text(
                    text = "계정이 없으신가요?",
                    color = colorScheme.onSurface,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Text(
                    text = "회원가입",
                    color = colorScheme.primary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight =
                        FontWeight.Medium,
                    modifier = Modifier.clickable(
                        enabled =
                            !uiState.isLoading,
                        onClick = onSignUpClick,
                    ),
                )
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )
        }
    }
}

@Composable
private fun GoogleMark(
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier.size(20.dp),
    ) {
        val strokeWidth = 3.3.dp.toPx()
        val inset = strokeWidth / 2f
        val arcSize = size.minDimension - strokeWidth

        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -42f,
            sweepAngle = 95f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
        )
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = -137f,
            sweepAngle = 95f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
        )
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 128f,
            sweepAngle = 94f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
        )
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 222f,
            sweepAngle = 54f,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
        )
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(size.width * 0.55f, size.height * 0.5f),
            end = Offset(size.width * 0.98f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Butt,
        )
    }
}

@Composable
private fun CircularCheckIndicator(
    checked: Boolean,
) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .size(18.dp)
            .background(
                color =
                    if (checked) {
                        colorScheme.primary
                    } else {
                        colorScheme.surface
                    },
                shape = CircleShape,
            )
            .border(
                width = 1.dp,
                color =
                    if (checked) {
                        colorScheme.primary
                    } else {
                        colorScheme.outline.copy(
                            alpha = 0.55f
                        )
                    },
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Canvas(
                modifier = Modifier.size(10.dp)
            ) {
                val strokeWidth =
                    1.7.dp.toPx()

                drawLine(
                    color =
                        colorScheme.onPrimary,
                    start = Offset(
                        x = size.width * 0.16f,
                        y = size.height * 0.52f,
                    ),
                    end = Offset(
                        x = size.width * 0.42f,
                        y = size.height * 0.76f,
                    ),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )

                drawLine(
                    color =
                        colorScheme.onPrimary,
                    start = Offset(
                        x = size.width * 0.42f,
                        y = size.height * 0.76f,
                    ),
                    end = Offset(
                        x = size.width * 0.84f,
                        y = size.height * 0.24f,
                    ),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun ErrorText(
    message: String,
) {
    Text(
        text = message,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.error,
        fontSize = 12.sp,
        lineHeight = 15.sp,
        textAlign = TextAlign.Start,
        fontWeight = FontWeight.Normal,
    )
}
