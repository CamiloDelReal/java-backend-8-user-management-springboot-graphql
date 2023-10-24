package org.xapps.services.usermanagementservice.configurations

import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.graphql.execution.ErrorType
import org.springframework.stereotype.Component
import org.xapps.services.usermanagementservice.services.exceptions.*

@Component
class ExceptionHandlerConfiguration : DataFetcherExceptionResolverAdapter() {

    override fun resolveToSingleError(ex: Throwable, env: DataFetchingEnvironment): GraphQLError? {
        val errorType = when (ex) {
            is DuplicityException -> ErrorType.BAD_REQUEST
            is ForbiddenException -> ErrorType.FORBIDDEN
            is InvalidCredentialException -> ErrorType.UNAUTHORIZED
            is RoleNotFoundException -> ErrorType.NOT_FOUND
            is UsernameNotFoundException -> ErrorType.NOT_FOUND
            is UserNotFoundException -> ErrorType.NOT_FOUND
            is AccessDeniedException -> ErrorType.UNAUTHORIZED
            else -> ErrorType.INTERNAL_ERROR
        }
        return GraphqlErrorBuilder.newError()
            .errorType(errorType)
            .message(ex.message)
            .path(env.executionStepInfo.path)
            .location(env.field.sourceLocation)
            .build()
    }

}