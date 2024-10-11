@file:Suppress("SqlSourceToSinkFlow")

package com.linecorp.kotlinjdsl.support.spring.data.jpa.javax

import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQuery
import com.linecorp.kotlinjdsl.render.RenderContext
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.query.QueryEnhancerFactoryAdaptor
import javax.persistence.EntityManager
import javax.persistence.Query
import javax.persistence.TypedQuery
import kotlin.reflect.KClass

internal object JpqlEntityManagerUtils {
    fun <T : Any> createQuery(
        entityManager: EntityManager,
        query: JpqlQuery<*>,
        returnType: KClass<T>,
        context: RenderContext,
    ): TypedQuery<T> {
        val rendered = JpqlRendererHolder.get().render(query, context)

        return createQuery(entityManager, rendered.query, rendered.params, returnType.java)
    }

    fun <T : Any> createQuery(
        entityManager: EntityManager,
        query: JpqlQuery<*>,
        queryParams: Map<String, Any?>,
        returnType: KClass<T>,
        context: RenderContext,
    ): TypedQuery<T> {
        val rendered = JpqlRendererHolder.get().render(query, queryParams, context)

        return createQuery(entityManager, rendered.query, rendered.params, returnType.java)
    }

    fun createQuery(
        entityManager: EntityManager,
        query: JpqlQuery<*>,
        context: RenderContext,
    ): Query {
        val rendered = JpqlRendererHolder.get().render(query, context)

        return createQuery(entityManager, rendered.query, rendered.params)
    }

    fun createQuery(
        entityManager: EntityManager,
        query: JpqlQuery<*>,
        queryParams: Map<String, Any?>,
        context: RenderContext,
    ): Query {
        val rendered = JpqlRendererHolder.get().render(query, queryParams, context)

        return createQuery(entityManager, rendered.query, rendered.params)
    }

    fun <T : Any> createEnhancedQuery(
        entityManager: EntityManager,
        query: JpqlQuery<*>,
        returnType: KClass<T>,
        sort: Sort,
        context: RenderContext,
    ): EnhancedTypedQuery<T> {
        val rendered = JpqlRendererHolder.get().render(query, context)

        val queryEnhancer = QueryEnhancerFactoryAdaptor.forQuery(rendered.query)

        return EnhancedTypedQuery(
            createQuery(entityManager, queryEnhancer.applySorting(sort), rendered.params, returnType.java),
        ) {
            // Lazy
            createCountQuery(entityManager, queryEnhancer.createCountQueryFor(), rendered.params)
        }
    }

    private fun createCountQuery(
        entityManager: EntityManager,
        query: String,
        queryParams: Map<String, Any?>,
    ): TypedQuery<Long> {
        return entityManager.createQuery(query, Long::class.javaObjectType).apply {
            setCountQueryParams(this, queryParams)
        }
    }

    private fun <T : Any> createQuery(
        entityManager: EntityManager,
        query: String,
        queryParams: Map<String, Any?>,
        returnType: Class<T>,
    ): TypedQuery<T> {
        return entityManager.createQuery(query, returnType).apply {
            setParams(this, queryParams)
        }
    }

    private fun createQuery(
        entityManager: EntityManager,
        query: String,
        queryParams: Map<String, Any?>,
    ): Query {
        return entityManager.createQuery(query).apply {
            setParams(this, queryParams)
        }
    }

    private fun setCountQueryParams(query: Query, params: Map<String, Any?>) {
        val parameterList = query.parameters.associateBy { it.name }

        params.forEach { (name, value) ->
            if (parameterList.contains(name)) {
                query.setParameter(name, value)
            }
        }
    }

    private fun setParams(query: Query, params: Map<String, Any?>) {
        params.forEach { (name, value) ->
            query.setParameter(name, value)
        }
    }
}
