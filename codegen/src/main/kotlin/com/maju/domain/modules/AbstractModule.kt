package com.maju.domain.modules

import com.maju.domain.entities.ConverterEntity
import com.maju.domain.entities.MethodEntity
import com.maju.domain.proxy.statement.utils.CollectionStatementGenerator
import com.maju.domain.proxy.statement.utils.SimpleStatementGenerator
import com.maju.utils.className
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.ImmutableKmType
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassInspector
import kotlin.reflect.KClass

@KotlinPoetMetadataPreview
abstract class AbstractModule : IModule {

    open val supportedTypes: List<KClass<*>> = listOf()

    protected val statementCreators = listOf(
        CollectionStatementGenerator(),
        SimpleStatementGenerator()
    )

    private lateinit var repositoryKmClazz: ImmutableKmClass

    protected lateinit var converterEntities: List<ConverterEntity>

    protected lateinit var elementClassInspector: ClassInspector


    private fun getModuleClasses(): List<ImmutableKmType> {
        val classes = mutableListOf<ImmutableKmType>()
        val supportedTypesNames = supportedTypes.map { it.qualifiedName }

        for (supportedType in supportedTypes) {
            val supertypes = repositoryKmClazz.supertypes
            val type = supertypes.findLast { supportedTypesNames.contains(it.className().canonicalName) }
            if (type != null) classes.add(type)
        }

        return classes
    }

    abstract fun createMethodEntities(kmType: ImmutableKmType): List<MethodEntity>


    override fun process(
        repositoryKmClazz: ImmutableKmClass,
        converterEntities: List<ConverterEntity>,
        elementClassInspector: ClassInspector
    ): List<MethodEntity> {
        this.repositoryKmClazz = repositoryKmClazz
        this.converterEntities = converterEntities
        this.elementClassInspector = elementClassInspector


        val moduleTypes = getModuleClasses()
        val methodEntities = mutableListOf<MethodEntity>()

        for (moduleClass in moduleTypes) {
            methodEntities.addAll(createMethodEntities(moduleClass))

        }
        return methodEntities
    }


}