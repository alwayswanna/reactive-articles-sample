package a.gleb.oauthsecurityserver.db.repository

import a.gleb.oauthsecurityserver.db.entity.CustomRegisteredClient
import org.springframework.data.jpa.repository.JpaRepository

interface CustomClientRegisteredRepository : JpaRepository<CustomRegisteredClient, Long> {
}