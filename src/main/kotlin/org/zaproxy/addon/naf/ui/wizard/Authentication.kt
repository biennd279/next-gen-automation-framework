package org.zaproxy.addon.naf.ui.wizard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.zaproxy.addon.naf.model.NafAuthMethodType
import org.zaproxy.addon.naf.model.NafAuthenticationMethod
import org.zaproxy.addon.naf.model.emptyFormBased

@Composable
fun Authentication(
    nafAuthenticationMethod: MutableState<NafAuthenticationMethod>
) {
    val currentMethod = derivedStateOf {
        when (nafAuthenticationMethod.value) {
            NafAuthenticationMethod.None -> NafAuthMethodType.NONE
            is NafAuthenticationMethod.FormBased -> NafAuthMethodType.FORM_BASED
        }
    }

    Column {
        Row {
            val expanded = remember { mutableStateOf(false) }

            OutlinedTextField(
                value = currentMethod.value.name,
                onValueChange = {},
                trailingIcon = {
                    IconButton(
                        onClick = {
                            expanded.value = true
                        }
                    ) {
                        Icon(Icons.Default.ArrowDropDown, "show method")
                    }
                }
            )


            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = {
                    expanded.value = false
                }
            ) {
                NafAuthMethodType.values().forEach { nafAuthMethod ->
                    DropdownMenuItem(
                        onClick = {
                            expanded.value = false
                            when (nafAuthMethod) {
                                NafAuthMethodType.NONE -> {
                                    nafAuthenticationMethod.value = NafAuthenticationMethod.None
                                }

                                NafAuthMethodType.FORM_BASED -> {
                                    nafAuthenticationMethod.value = emptyFormBased()
                                }
                            }
                        }
                    ) {
                        Text(
                            text = nafAuthMethod.name
                        )
                    }
                }
            }
        }
    }


    when (nafAuthenticationMethod.value) {
        NafAuthenticationMethod.None -> {}
        is NafAuthenticationMethod.FormBased -> {
            @Suppress("unchecked_cast")
            FormBasedMethod(
                nafAuthenticationMethod as MutableState<NafAuthenticationMethod.FormBased>
            )
        }
    }

}

@Composable
fun FormBasedMethod(
    nafAuthenticationMethod: MutableState<NafAuthenticationMethod.FormBased>,
) {
    val scrollState = rememberScrollState(0)
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        with(nafAuthenticationMethod) {
            OutlinedTextField(
                value = value.loginPage,
                onValueChange = {
                    value = value.copy(loginPage = it)
                },
                label = {
                    Text("Login page")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = value.loginUrl,
                onValueChange = {
                    value = value.copy(loginUrl = it)
                },
                label = {
                    Text("Login Url")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = value.username,
                onValueChange = {
                    value = value.copy(username = it)
                },
                label = {
                    Text("Username")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = value.password,
                onValueChange = {
                    value = value.copy(password = it)
                },
                label = {
                    Text("Password")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = value.loginField.first,
                    onValueChange = {
                        value = value.copy(loginField = value.loginField.copy(first = it))
                    },
                    label = {
                        Text("Username Field")
                    }
                )

                OutlinedTextField(
                    value = value.loginField.second,
                    onValueChange = {
                        value = value.copy(loginField = value.loginField.copy(second = it))
                    },
                    label = {
                        Text("Password Field")
                    },
                )
            }
            OutlinedTextField(
                value = value.loginPattern,
                onValueChange = {
                    value = value.copy(loginPattern = it)
                },
                label = {
                    Text("Login Pattern")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = value.logoutPattern,
                onValueChange = {
                    value = value.copy(logoutPattern = it)
                },
                label = {
                    Text("Logout Pattern")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
