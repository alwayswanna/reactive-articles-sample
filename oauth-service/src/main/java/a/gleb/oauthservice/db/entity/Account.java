package a.gleb.oauthservice.db.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.UUID;

import static javax.persistence.GenerationType.AUTO;

@Entity(name = "accounts")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private AccountRoles role;

    @Column(name = "email")
    private String email;

    @Column(name = "enabled")
    private boolean enabled = false;

}
