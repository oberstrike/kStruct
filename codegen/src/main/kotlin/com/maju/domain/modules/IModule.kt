package com.maju.domain.modules

import com.maju.domain.entities.ConverterEntity
import com.maju.domain.entities.MethodEntity
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassInspector

@KotlinPoetMetadataPreview
interface IModule {
    fun process(
        repositoryKmClazz: ImmutableKmClass,
        converterEntities: List<ConverterEntity>,
        elementClassInspector: ClassInspector
    ): List<MethodEntity>
}