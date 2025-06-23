package de.laboranowitsch.poc.enversfilterdemo.repo

import de.laboranowitsch.poc.enversfilterdemo.entity.ParentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ParentRepository : JpaRepository<ParentEntity, UUID>
