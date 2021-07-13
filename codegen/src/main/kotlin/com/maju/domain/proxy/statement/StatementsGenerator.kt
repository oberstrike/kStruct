package com.maju.domain.proxy.statement

import com.maju.domain.entities.ConverterEntity
import com.maju.domain.entities.MethodEntity
import com.maju.domain.entities.ParameterEntity
import com.maju.utils.IGenerator
import com.maju.utils.CKType
import org.jetbrains.kotlin.builtins.StandardNames.FqNames.target

class StatementsGenerator(
    private val methodEntity: MethodEntity,
    private val converterEntities: List<ConverterEntity>
) : IGenerator<List<String>> {

    private val statements = mutableListOf<String>()

    private val resultVariableName = "result"


    override fun generate(): List<String> {
        val returnType = methodEntity.returnType

        val allParams = methodEntity.parameters.joinToString(",·") { param -> analyseParam(param) }

        var resultStatement = "val $resultVariableName = repository.·${methodEntity.name}·(·$allParams·)"

        if (returnType.isNullable) {
            resultStatement += "?: return null"
        }

        statements.add(resultStatement)
        statements.add(analyseReturnType(returnType))
        return statements
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

    private fun analyseParam(param: ParameterEntity): String {
        val paramType = param.type
        val converterEntity = getConverterByTargetType(paramType)

        var targetName = param.name
        val originName = param.name

        val targetType = converterEntity?.targetType ?: return targetName

        targetName = "${param.name}Model"
        val converterName = converterEntity.getName()

        val generator = methodEntity.statementCreator


        val converters = listOfNotNull(
            CollectionStatementGenerator(),
            generator,
            SimpleStatementGenerator()
        )

        val targetGenerator = converters.first { it.isSupported(paramType) }

        val createStatement = targetGenerator.createStatement(
            converterName,
            converterEntity.targetToOriginFunctionName,
            targetType,
            originName
        )



        statements.add("val $targetName = $createStatement")

        return targetName
    }

    private fun analyseReturnType(returnType: CKType): String {
        val converterEntity = getConverterByTargetType(returnType)
        val targetName = "result"
        val targetType = converterEntity?.targetType ?: return "return $targetName"
        val converterName = converterEntity.getName()


        val generator = methodEntity.statementCreator

        if(generator != null){
            println("test")
        }

        val converters = listOfNotNull(
            CollectionStatementGenerator(),
            generator,
            SimpleStatementGenerator()
        )

        val targetGenerator = converters.first { it.isSupported(returnType) }

        val createStatement = targetGenerator.createStatement(
            converterName,
            converterEntity.originToTargetFunctionName,
            targetType,
            targetName
        )

        return "return $createStatement"
    }

}