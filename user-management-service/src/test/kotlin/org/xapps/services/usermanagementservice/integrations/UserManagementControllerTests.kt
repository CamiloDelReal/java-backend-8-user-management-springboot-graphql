package org.xapps.services.usermanagementservice.integrations

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testng.annotations.Test


@SpringBootTest
@AutoConfigureGraphQlTester
@Testcontainers
class UserManagementControllerTests : AbstractTestNGSpringContextTests() {

    @Autowired
    private lateinit var graphQlTester: GraphQlTester

    @Test
    fun loginRoot_success() {
        val response = graphQlTester
            .documentName("login")
            .variable("email", "root@gmail.com")
            .variable("password", "123456")
            .execute()

        response.path("login.token").hasValue()
        response.path("login.expiration").hasValue()
        response.path("login")
            .matchesJson(
                """
                    {
                        "type": "Bearer"
                    }
                """
            )
    }

    @Test
    fun loginRoot_failByInvalidEmail() {
        graphQlTester
            .documentName("login")
            .variable("email", "invalid@gmail.com")
            .variable("password", "123456")
            .execute()
            .errors()
            .expect {
                it.message == "Bad credentials"
            }
    }

    @Test
    fun loginRoot_failByInvalidPassword() {
        graphQlTester
            .documentName("login")
            .variable("email", "root@gmail.com")
            .variable("password", "invalid")
            .execute()
            .errors()
            .expect {
                it.message == "Bad credentials"
            }
    }

    companion object {

        @JvmStatic
        @Container
        val mySQLContainer = MySQLContainer("mysql:8.0")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true)
            .apply {
                start()
            }

        @JvmStatic
        @DynamicPropertySource
        fun setDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { mySQLContainer.getJdbcUrl() }
            registry.add("spring.datasource.username") { mySQLContainer.username }
            registry.add("spring.datasource.password") { mySQLContainer.password }
        }

    }


}
