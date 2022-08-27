package a.gleb.oauthsecurityserver.controller

import a.gleb.articlecommon.models.rest.RegisterClientRequest
import a.gleb.oauthsecurityserver.service.OauthRegisterClientService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@Controller
@RequestMapping("/admin/api/oauth-client")
class RegisterClientController(val registerClientService: OauthRegisterClientService) {

    @PostMapping("/create")
    fun create(@RequestBody @Valid registerClientRequest: RegisterClientRequest, model: Model): String {
        val response = registerClientService.createNewRegisteredClient(registerClientRequest)
        model.addAttribute("response", response)
        return "index";
    }

    @GetMapping("/")
    fun main(model: Model): String {
        val clientModels = registerClientService.findAllClients()
        model.addAttribute("clientModels", clientModels)
        return "index"
    }


}