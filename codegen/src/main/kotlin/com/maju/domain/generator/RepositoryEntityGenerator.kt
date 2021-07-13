package com.maju.domain.generator

import com.maju.domain.entities.ConverterEntity
import com.maju.domain.entities.MethodEntity
import com.maju.domain.entities.RepositoryEntity
import com.maju.utils.IGenerator
import com.maju.utils.CKType
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview

@KotlinPoetMetadataPreview
class RepositoryEntityGenerator(
    private val name: String,
    private val type: CKType,
    private val converters: List<ConverterEntity>,
    private val methods: List<MethodEntity>
) : IGenerator<RepositoryEntity> {

    override fun generate(): RepositoryEntity {
        return RepositoryEntity(
            name = name,
            type = type,
            converters = converters,
            methods = methods
        )
    }
}
