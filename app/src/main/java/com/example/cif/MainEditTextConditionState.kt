package com.example.cif

sealed class MainEditTextConditionState(val errorMessage: String? = null) {
    class IsError(val message: String): MainEditTextConditionState(message)
    object IsPassed : MainEditTextConditionState()
    object Nothing : MainEditTextConditionState()
}