package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.config.OAuthConfig
import com.dkosub.ffxiv.tools.service.AuthService
import io.jooby.Context
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Path("/auth")
class AuthController @Inject constructor(
    oAuthConfig: OAuthConfig,
    private val authService: AuthService,
    private val client: HttpClient,
) {
    private val clientId = oAuthConfig.clientId()
    private val clientSecret = oAuthConfig.clientSecret()
    private val authorizeUrl = oAuthConfig.authorizeUrl()
    private val redirectUrl = oAuthConfig.redirectUrl()
    private val tokenUrl = oAuthConfig.tokenUrl()

    @GET
    suspend fun authenticate(ctx: Context) {
        val tokenResponse = client.post(tokenUrl) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "client_id" to clientId,
                    "client_secret" to clientSecret,
                    "grant_type" to "authorization_code",
                    "code" to ctx.query("code").toString(),
                    "redirect_uri" to redirectUrl,
                ).formUrlEncode()
            )
        }.body<Map<String, String>>()

        val userResponse = client.get("https://discord.com/api/users/@me") {
            header("Authorization", "${tokenResponse["token_type"]} ${tokenResponse["access_token"]}")
        }.body<Map<String, String>>()

        val id = userResponse["id"]!!.toLong()
        authService.loginAccount(id)
        ctx.session().put("id", id)
        ctx.sendRedirect("/")
    }

    @GET("/logout")
    fun logout(ctx: Context) {
        ctx.session().destroy()
        ctx.sendRedirect("/")
    }

    @GET("/redirect")
    fun redirect(ctx: Context) {
        val queryString = listOf(
            "client_id" to clientId,
            "redirect_uri" to redirectUrl,
            "response_type" to "code",
            "scope" to "identify",
        ).formUrlEncode()

        ctx.sendRedirect("${authorizeUrl}?${queryString}")
    }
}
