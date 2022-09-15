package a.gleb.oauth2manager.controller

import a.gleb.articlecommon.models.rest.AccountEditModel
import a.gleb.articlecommon.models.rest.AccountModel
import a.gleb.oauth2manager.configuration.NAME_SECURITY_SCHEME
import a.gleb.oauth2manager.db.entity.Account
import a.gleb.oauth2manager.service.AccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

const val ACCOUNT_CONTROLLER_TAG = "Account manage API."

@Controller
@RequestMapping("/api/v1")
@Tag(name = ACCOUNT_CONTROLLER_TAG)
class AccountController(
    private val accountService: AccountService
) {

    @Operation(
        summary = "Create new user account.",
        tags = [ACCOUNT_CONTROLLER_TAG]
    )
    @PostMapping("/account")
    fun createAccount(@RequestBody @Valid account: AccountModel): ResponseEntity<Account> {
        return ResponseEntity.ok().body(accountService.create(account))
    }

    @Operation(
        summary = "Update existing account.",
        security = [SecurityRequirement(name = NAME_SECURITY_SCHEME)],
        tags = [ACCOUNT_CONTROLLER_TAG]
    )
    @PutMapping("/account")
    fun editAccount(@RequestBody @Valid account: AccountEditModel): ResponseEntity<Account> {
        return ResponseEntity.ok().body(accountService.edit(account))
    }

    @Operation(
        summary = "Delete existing account.",
        security = [SecurityRequirement(name = NAME_SECURITY_SCHEME)],
        tags = [ACCOUNT_CONTROLLER_TAG]
    )
    @DeleteMapping("/account")
    fun remove():ResponseEntity<Account>{
        return ResponseEntity.ok().body(accountService.remove())
    }
}