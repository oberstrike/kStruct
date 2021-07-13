package com.maju.domain.proxy.statement

import com.maju.domain.modules.IStatementCreator
import com.maju.utils.CKType

class SimpleStatementGenerator : IStatementCreator {

    override fun createStatement(
        converterName: String,
        convertExpression: String,
        targetType: CKType,
        paramName: String
    ): String {
        return "${converterName}.${convertExpression}($paramName)"
    }


    override fun isSupported(type: CKType): Boolean {
        return true
    }

}

