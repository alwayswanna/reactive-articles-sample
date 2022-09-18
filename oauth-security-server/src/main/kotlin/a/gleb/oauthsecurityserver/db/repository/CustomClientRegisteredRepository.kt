package a.gleb.oauthsecurityserver.db.repository

import a.gleb.oauthsecurityserver.db.entity.CustomRegisteredClient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomClientRegisteredRepository : JpaRepository<CustomRegisteredClient, Long> {
}