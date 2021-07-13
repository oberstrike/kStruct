package com.maju.domain.generator

import com.maju.domain.entities.MethodEntity
import com.maju.domain.entities.ParameterEntity
import com.maju.domain.modules.IStatementCreator
import com.maju.utils.IGenerator
import com.maju.utils.CKType

class MethodEntityGenerator(
    private val name: String,
    private val parameters: List<ParameterEntity>,
    private val returnType: CKType,
    private val isSuspend: Boolean,
    private val statementCreator: IStatementCreator? = null
) : IGenerator<MethodEntity> {

    override fun generate(): MethodEntity {
        return MethodEntity(
            name = name,
            parameters = parameters,
            returnType = returnType,
            isSuspend = isSuspend,
            statementCreator = statementCreator
        )
    }
}
