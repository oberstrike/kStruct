package com.maju.domain.proxy.statement

import com.maju.domain.entities.ConverterEntity
import com.maju.domain.entities.MethodEntity
import com.maju.domain.entities.ParameterEntity
import com.maju.domain.modules.IStatementCreator
import com.maju.domain.proxy.statement.utils.CollectionStatementGenerator
import com.maju.domain.proxy.statement.utils.SimpleStatementGenerator
import com.maju.utils.CKType

class StatementsGenerator(
    methodEntity: MethodEntity,
    converterEntities: List<ConverterEntity>
) : AbstractStatementsGenerator(
    methodEntity, converterEntities
) {

    private val resultVariableName = "result"


    override fun generate(): List<String> = createStatement {
        val returnType = methodEntity.returnType

        val allParamNames = methodEntity.parameters
            .map { it.copy(name = getVarNameOfParam(it)) }

        val allParams = allParamNames.joinToString(",·") { it.name }


        val allParamsVars = allParamNames
            .filter { it.name.contains("Model") }
            .map { createParam(it) }

        addStatements(allParamsVars)

        var resultStatement = "val $resultVariableName = repository.·${methodEntity.name}·(·$allParams·)"

        if (returnType.isNullable) {
            resultStatement += "?: return null"
        }

        addStatement(resultStatement)
        addStatement(analyseReturnType(returnType))
    }

    private fun getConverterByTargetType(target: CKType): ConverterEntity? {
        val arguments = target.arguments
        if (arguments.isNotEmpty()) {
            return getConverterByTargetType(arguments.first())
        }

        return converterEntities.find { converter ->
            converter.targetType.className == target.className
        }

    }

    private fun getVarNameOfParam(param: ParameterEntity): String {
        val paramType = param.type
        val converterEntity = getConverterByTargetType(paramType)
        var targetName = param.name
        converterEntity?.targetType ?: return targetName
        targetName = "${param.name}Model"
        return targetName
    }


    private fun createParam(param: ParameterEntity): String {
        val paramType = param.type
        val converterEntity = getConverterByTargetType(paramType)
        val originName = param.name.removeSuffix("Model")
        val targetName = param.name
        val targetType = converterEntity?.targetType!!

        val createStatement = getStatementCreator(paramType).createStatement(
            converterEntity.getName(),
            converterEntity.targetToOriginFunctionName,
            targetType,
            originName
        )

        return "val $targetName = $createStatement"
    }

    private fun analyseReturnType(returnType: CKType): String {
        val converterEntity = getConverterByTargetType(returnType)

        val targetType = converterEntity?.targetType ?: return "return $resultVariableName"

        val createStatement = getStatementCreator(returnType).createStatement(
            converterEntity.getName(),
            converterEntity.originToTargetFunctionName,
            targetType,
            resultVariableName
        )

        return "return $createStatement"
    }

    private fun getStatementCreator(returnType: CKType): IStatementCreator {
        val statementCreators = listOfNotNull(
            CollectionStatementGenerator(),
            methodEntity.statementCreator,
            SimpleStatementGenerator()
        )

        return statementCreators.first { it.isSupported(returnType) }
    }

}