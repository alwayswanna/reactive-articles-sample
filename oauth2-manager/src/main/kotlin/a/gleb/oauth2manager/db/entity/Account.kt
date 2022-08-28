package a.gleb.oauth2manager.db.entity

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
    var id: UUID,
    @Column(name = "username")
    var username: String,
    @Column(name = "password")
    var password: String,
    @Column(name = "first_name")
    var firstName: String? = null,
    @Column(name = "last_name")
    var lastName: String? = null,
    @Column(name = "role")
    var roles: String,
    @Column(name = "email")
    var email: String,
    @Column(name = "enabled")
    var enabled: Boolean = false
)
