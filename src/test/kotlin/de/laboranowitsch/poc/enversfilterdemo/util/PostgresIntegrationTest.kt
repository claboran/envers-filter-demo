package de.laboranowitsch.poc.enversfilterdemo.util

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostgresContainerConfiguration::class)
@ActiveProfiles("test")
annotation class PostgresIntegrationTest
