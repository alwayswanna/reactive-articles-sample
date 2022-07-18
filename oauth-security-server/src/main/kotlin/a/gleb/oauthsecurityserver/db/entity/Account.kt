package a.gleb.oauthsecurityserver.db.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.AUTO
import javax.persistence.Id

@Entity(name = "accounts")
data class Account(

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", nullable = false)
    val id: UUID,
    @Column(name = "username")
    val username: String,
    @Column(name = "password")
    val password: String,
    @Column(name = "first_name")
    val firstName: String? = null,
    @Column(name = "last_name")
    val lastName: String? = null,
    @Column(name = "role")
    val roles: String,
    @Column(name = "email")
    val email: String,
    @Column(name = "enabled")
    val enabled: Boolean = false
)
