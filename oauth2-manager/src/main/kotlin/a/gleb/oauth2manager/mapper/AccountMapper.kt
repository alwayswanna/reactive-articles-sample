package a.gleb.oauth2manager.mapper

import a.gleb.articlecommon.models.rest.AccountEditModel
import a.gleb.articlecommon.models.rest.AccountModel
import a.gleb.oauth2manager.db.entity.Account
import a.gleb.oauth2manager.db.entity.AccountRoles
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountMapper(
    var passwordEncoder: PasswordEncoder
) {

    fun toAccount(accountModel: AccountModel): Account {
        return Account(
            id = UUID.randomUUID(),
            username = accountModel.username,
            password = passwordEncoder.encode(accountModel.password),
            email = accountModel.email,
            firstName = accountModel.firstName,
            lastName = accountModel.lastName,
            roles = AccountRoles.USER.name,
            enabled = true
        )
    }

    fun updateAccount(account: Account, accountEditModel: AccountEditModel): Account{
        if (accountEditModel.username.isNotEmpty()){
            account.username = accountEditModel.username
        }
        if (accountEditModel.password.isNotEmpty()){
            account.password = passwordEncoder.encode(accountEditModel.password)
        }
        if (accountEditModel.firstName.isNotEmpty()){
            account.firstName = accountEditModel.firstName
        }
        if (accountEditModel.lastName.isNotEmpty()){
            account.lastName = accountEditModel.lastName
        }
        if (accountEditModel.email.isNotEmpty()){
            account.email = accountEditModel.email
        }
        return account
    }

    fun toAccountModel(account: Account): AccountModel {
        return AccountModel.builder()
            .username(account.username)
            .password(passwordEncoder.encode(account.password))
            .email(account.email)
            .firstName(account.firstName)
            .lastName(account.lastName)
            .build();
    }
}