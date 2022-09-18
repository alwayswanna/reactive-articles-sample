package a.gleb.oauth2manager.service

import a.gleb.articlecommon.models.mq.EventType.DELETE
import a.gleb.articlecommon.models.mq.EventType.EDIT
import a.gleb.articlecommon.models.rest.AccountEditModel
import a.gleb.articlecommon.models.rest.AccountModel
import a.gleb.oauth2manager.db.entity.Account
import a.gleb.oauth2manager.db.repository.AccountRepository
import a.gleb.oauth2manager.exception.AccountExistingException
import a.gleb.oauth2manager.exception.DataAccountAccessException
import a.gleb.oauth2manager.mapper.AccountMapper
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

val logger = KotlinLogging.logger { }

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val accountMapper: AccountMapper,
    private val securityContextHelperService: SecurityContextHelperService,
    private val notifierService: NotifierService
) {

    /**
     * Create new account for OAuth2-server.
     */
    fun create(accountModel: AccountModel): Account {
        if (accountRepository.findAccountByUsername(accountModel.username).isPresent) {
            throw AccountExistingException("Account with username ${accountModel.username} already exists")
        }

        try {
            return accountRepository.save(accountMapper.toAccount(accountModel))
        } catch (e: Exception) {
            logger.warn { "Something goes wrong while save new account ${accountModel.username}" }
            throw DataAccountAccessException("Can`t create new account. Let`s try late.")
        }
    }

    /**
     * Edit account for OAuth2-server.
     */
    @Transactional
    fun edit(accountEditModel: AccountEditModel): Account {
        val accountId = securityContextHelperService.getIdFromToken()
        val accountOptionalFromDatabase = accountRepository.findAccountById(accountId)
        if (!accountOptionalFromDatabase.isPresent) {
            throw AccountExistingException("Account with ID: $accountId, does not exist.")
        }

        if (accountRepository.findAccountByUsername(accountEditModel.username).isPresent &&
            accountOptionalFromDatabase.get().username != accountEditModel.username
        ) {
            throw AccountExistingException("Account with username ${accountEditModel.username} already exists")
        }

        val accountToSave = accountMapper.updateAccount(
            accountOptionalFromDatabase.get(),
            accountEditModel
        )
        try {
            val updatedAccount = accountRepository.save(accountToSave)
            notifierService.notify(updatedAccount, EDIT)
            return updatedAccount
        } catch (e: Exception) {
            logger.warn { "Something goes wrong while edit existing account ${accountToSave.username}" }
            throw DataAccountAccessException("Can`t edit existing account. Let`s try late.")
        }
    }

    /**
     * Remove existing account for OAuth2-server.
     */
    @Transactional
    fun remove(): Account {
        val accountId = securityContextHelperService.getIdFromToken()
        val accountOptionalFromDatabase = accountRepository.findAccountById(accountId)
        if (!accountOptionalFromDatabase.isPresent) {
            throw AccountExistingException("Account with ID: $accountId, does not exist.")
        }
        try {
            accountRepository.deleteAccountById(accountId)
            notifierService.notify(accountOptionalFromDatabase.get(), DELETE)
            return accountOptionalFromDatabase.get()
        } catch (e: Exception) {
            logger.warn {
                "Something goes wrong while edit existing account ${accountOptionalFromDatabase.get().username}, " +
                        "message: ${e.message}"
            }
            throw DataAccountAccessException("Can`t remove existing account. Let`s try late.")
        }
    }
}