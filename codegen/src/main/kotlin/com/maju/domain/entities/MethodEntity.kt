package com.maju.domain.entities

import com.maju.domain.modules.IStatementCreator
import com.maju.utils.CKType

data class MethodEntity(
    val name: String,
    val parameters: List<ParameterEntity>,
    val returnType: CKType,
    val isSuspend: Boolean,
    val statementCreator: IStatementCreator? = null
)