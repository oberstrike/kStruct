package com.maju.domain.modules

import com.maju.utils.CKType

interface IStatementCreator {
    fun isSupported(type: CKType): Boolean

    fun createStatement(
        converterName: String,
        convertExpression: String,
        targetType: CKType,
        paramName: String
    ): String
}