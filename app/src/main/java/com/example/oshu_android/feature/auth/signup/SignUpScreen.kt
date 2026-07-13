package com.example.oshu_android.feature.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.oshu_android.R

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel,
    onBackClick: () -> Unit,
    onSignUpSuccess: () -> Unit,
) {
    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignUpSuccess()
            viewModel.onSuccessHandled()
        }
    }

    SignUpScreen(
        uiState = uiState,
        onLoginIdChanged =
            viewModel::onLoginIdChanged,
        onPasswordChanged =
            viewModel::onPasswordChanged,
        onPasswordConfirmChanged =
            viewModel::onPasswordConfirmChanged,
        onCheckLoginId =
            viewModel::checkLoginId,
        onAllTermsChanged =
            viewModel::onAllTermsChanged,
        onServiceTermsChanged =
            viewModel::onServiceTermsChanged,
        onPrivacyTermsChanged =
            viewModel::onPrivacyTermsChanged,
        onMarketingTermsChanged =
            viewModel::onMarketingTermsChanged,
        onSignUpClick =
            viewModel::signUp,
        onBackClick = onBackClick,
    )
}

@Composable
fun SignUpScreen(
    uiState: SignUpUiState,
    onLoginIdChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordConfirmChanged: (String) -> Unit,
    onCheckLoginId: () -> Unit,
    onAllTermsChanged: (Boolean) -> Unit,
    onServiceTermsChanged: (Boolean) -> Unit,
    onPrivacyTermsChanged: (Boolean) -> Unit,
    onMarketingTermsChanged: (Boolean) -> Unit,
    onSignUpClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current

    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var passwordConfirmVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    horizontal = 24.dp,
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clickable(
                            enabled = !uiState.isLoading,
                            onClick = onBackClick,
                        ),
                    contentAlignment =
                        Alignment.CenterStart,
                ) {
                    Text(
                        text = "‹",
                        fontSize = 35.sp,
                        lineHeight = 35.sp,
                        color = colorScheme.onBackground,
                    )
                }

                Text(
                    text = "OSHU",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                )
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "회원가입",
                fontSize = 30.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            InputLabel(
                text = "아이디"
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),
                verticalAlignment =
                    Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    SignUpTextField(
                        value = uiState.loginId,
                        onValueChange =
                            onLoginIdChanged,
                        placeholder =
                            "영문, 숫자 조합 6~20자",
                        isError =
                            uiState.loginIdError != null,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Ascii,
                                imeAction =
                                    ImeAction.Next,
                            ),
                        keyboardActions =
                            KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(
                                        FocusDirection.Down
                                    )
                                }
                            ),
                    )

                    uiState.loginIdError?.let {
                        SignUpErrorText(
                            text = it
                        )
                    }
                }

                Button(
                    onClick = onCheckLoginId,
                    modifier = Modifier
                        .width(96.dp)
                        .height(52.dp),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(7.dp),
                    contentPadding = PaddingValues(
                        horizontal = 10.dp,
                        vertical = 0.dp,
                    ),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                colorScheme.primary.copy(
                                    alpha = 0.18f
                                ),
                            contentColor =
                                colorScheme.primary,
                            disabledContainerColor =
                                colorScheme.primary.copy(
                                    alpha = 0.10f
                                ),
                            disabledContentColor =
                                colorScheme.primary.copy(
                                    alpha = 0.55f
                                ),
                        ),
                ) {
                    Text(
                        text =
                            if (
                                uiState.isLoginIdChecked
                            ) {
                                "확인완료"
                            } else {
                                "중복확인"
                            },
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(
                    if (
                        uiState.loginIdError == null
                    ) {
                        22.dp
                    } else {
                        7.dp
                    }
                )
            )

            InputLabel(
                text = "비밀번호"
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            SignUpTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                placeholder =
                    "영문, 숫자, 특수문자 포함 8자 이상",
                isError =
                    uiState.passwordError != null,
                visualTransformation =
                    if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Password,
                        imeAction = ImeAction.Next,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Down
                            )
                        }
                    ),
                trailingIcon = {
                    PasswordVisibilityButton(
                        visible = passwordVisible,
                        onClick = {
                            passwordVisible =
                                !passwordVisible
                        },
                    )
                },
            )

            uiState.passwordError?.let {
                SignUpErrorText(
                    text = it
                )
            }

            Spacer(
                modifier = Modifier.height(
                    if (
                        uiState.passwordError == null
                    ) {
                        22.dp
                    } else {
                        7.dp
                    }
                )
            )

            InputLabel(
                text = "비밀번호 확인"
            )

            Spacer(
                modifier = Modifier.height(9.dp)
            )

            SignUpTextField(
                value = uiState.passwordConfirm,
                onValueChange =
                    onPasswordConfirmChanged,
                placeholder =
                    "비밀번호를 다시 입력하세요",
                isError =
                    uiState.passwordConfirmError != null,
                visualTransformation =
                    if (passwordConfirmVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType =
                            KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                trailingIcon = {
                    PasswordVisibilityButton(
                        visible =
                            passwordConfirmVisible,
                        onClick = {
                            passwordConfirmVisible =
                                !passwordConfirmVisible
                        },
                    )
                },
            )

            uiState.passwordConfirmError?.let {
                SignUpErrorText(
                    text = it
                )
            }

            Spacer(
                modifier = Modifier.height(
                    if (
                        uiState.passwordConfirmError ==
                        null
                    ) {
                        38.dp
                    } else {
                        23.dp
                    }
                )
            )

            AgreementSection(
                uiState = uiState,
                onAllTermsChanged =
                    onAllTermsChanged,
                onServiceTermsChanged =
                    onServiceTermsChanged,
                onPrivacyTermsChanged =
                    onPrivacyTermsChanged,
                onMarketingTermsChanged =
                    onMarketingTermsChanged,
            )

            uiState.generalError?.let {
                SignUpErrorText(
                    text = it,
                    modifier = Modifier.padding(
                        top = 2.dp
                    ),
                )
            }

            Spacer(
                modifier = Modifier.height(
                    if (
                        uiState.generalError == null
                    ) {
                        44.dp
                    } else {
                        29.dp
                    }
                )
            )

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSignUpClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = uiState.canSubmit,
                shape = RoundedCornerShape(10.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            colorScheme.primary,
                        contentColor =
                            colorScheme.onPrimary,
                        disabledContainerColor =
                            colorScheme.primary.copy(
                                alpha = 0.52f
                            ),
                        disabledContentColor =
                            colorScheme.onPrimary,
                    ),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(22.dp),
                        color =
                            colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = "회원 가입",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }
    }
}

@Composable
private fun SignUpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    visualTransformation:
    VisualTransformation =
        VisualTransformation.None,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions.Default,
    keyboardActions: KeyboardActions =
        KeyboardActions.Default,
    trailingIcon:
    (@Composable () -> Unit)? = null,
) {
    val colorScheme = MaterialTheme.colorScheme

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        textStyle = TextStyle(
            fontSize = 14.sp,
            color = colorScheme.onSurface,
        ),
        placeholder = {
            Text(
                text = placeholder,
                maxLines = 1,
                fontSize = 13.sp,
            )
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        isError = isError,
        visualTransformation =
            visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(7.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedContainerColor =
                    Color.White,
                unfocusedContainerColor =
                    Color.White,
                errorContainerColor =
                    Color.White,
                disabledContainerColor =
                    Color.White,
                focusedBorderColor =
                    colorScheme.primary,
                unfocusedBorderColor =
                    colorScheme.outline.copy(
                        alpha = 0.32f
                    ),
                errorBorderColor =
                    colorScheme.error,
                focusedPlaceholderColor =
                    colorScheme.onSurfaceVariant
                        .copy(alpha = 0.50f),
                unfocusedPlaceholderColor =
                    colorScheme.onSurfaceVariant
                        .copy(alpha = 0.50f),
            ),
    )
}

@Composable
private fun PasswordVisibilityButton(
    visible: Boolean,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    IconButton(
        onClick = onClick,
        modifier = Modifier.size(36.dp),
    ) {
        Icon(
            painter = painterResource(
                if (visible) {
                    R.drawable.ic_visibility_off
                } else {
                    R.drawable.ic_visibility
                }
            ),
            contentDescription =
                if (visible) {
                    "비밀번호 숨기기"
                } else {
                    "비밀번호 보기"
                },
            modifier = Modifier.size(21.dp),
            tint = colorScheme.primary,
        )
    }
}

@Composable
private fun InputLabel(
    text: String,
) {
    Text(
        text = text,
        fontSize = 14.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.SemiBold,
        color =
            MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun AgreementSection(
    uiState: SignUpUiState,
    onAllTermsChanged: (Boolean) -> Unit,
    onServiceTermsChanged: (Boolean) -> Unit,
    onPrivacyTermsChanged: (Boolean) -> Unit,
    onMarketingTermsChanged: (Boolean) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(
                    color = colorScheme.surface.copy(
                        alpha = 0.82f
                    ),
                    shape = RoundedCornerShape(8.dp),
                )
                .clickable {
                    onAllTermsChanged(
                        !uiState.allTermsAgreed
                    )
                }
                .padding(
                    horizontal = 14.dp,
                ),
            verticalAlignment =
                Alignment.CenterVertically,
        ) {
            SquareCheck(
                checked =
                    uiState.allTermsAgreed,
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = "전체 동의",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface,
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        AgreementRow(
            checked =
                uiState.serviceTermsAgreed,
            text = "[필수] 서비스 이용약관 동의",
            onClick = {
                onServiceTermsChanged(
                    !uiState.serviceTermsAgreed
                )
            },
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        AgreementRow(
            checked =
                uiState.privacyTermsAgreed,
            text = "[필수] 개인정보 수집·이용 동의",
            onClick = {
                onPrivacyTermsChanged(
                    !uiState.privacyTermsAgreed
                )
            },
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        AgreementRow(
            checked =
                uiState.marketingTermsAgreed,
            text = "[선택] 마케팅 정보 수신 동의",
            onClick = {
                onMarketingTermsChanged(
                    !uiState.marketingTermsAgreed
                )
            },
        )
    }
}

@Composable
private fun AgreementRow(
    checked: Boolean,
    text: String,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(29.dp)
            .clickable(onClick = onClick),
        verticalAlignment =
            Alignment.CenterVertically,
    ) {
        SquareCheck(
            checked = checked,
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            fontSize = 12.sp,
            color = colorScheme.onSurface,
        )

        Text(
            text = "보기  ›",
            maxLines = 1,
            fontSize = 11.sp,
            color =
                colorScheme.onSurfaceVariant.copy(
                    alpha = 0.58f
                ),
        )
    }
}

@Composable
private fun SquareCheck(
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
                        Color.Transparent
                    },
                shape = RoundedCornerShape(1.dp),
            )
            .border(
                width = 1.dp,
                color =
                    if (checked) {
                        colorScheme.primary
                    } else {
                        colorScheme.outline.copy(
                            alpha = 0.78f
                        )
                    },
                shape = RoundedCornerShape(1.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Text(
                text = "✓",
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun SignUpErrorText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.padding(
            start = 3.dp,
            top = 2.dp,
        ),
        maxLines = 1,
        color = MaterialTheme.colorScheme.error,
        fontSize = 10.sp,
        lineHeight = 12.sp,
    )
}