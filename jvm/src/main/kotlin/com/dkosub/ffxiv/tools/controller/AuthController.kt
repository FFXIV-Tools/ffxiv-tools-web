package com.dkosub.ffxiv.tools.controller

import com.dkosub.ffxiv.tools.config.OAuthConfig
import com.dkosub.ffxiv.tools.service.AuthService
import io.jooby.Context
import io.jooby.Cookie
import io.jooby.annotations.GET
import io.jooby.annotations.Path
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
    @Path("/auth/redirect")
    fun redirect(ctx: Context) {
        val queryString = listOf(
            "client_id" to clientId,
            "redirect_uri" to redirectUrl,
            "response_type" to "code",
            "scope" to "identify",
        ).formUrlEncode()

        ctx.sendRedirect("${authorizeUrl}?${queryString}")
    }

    @GET
    @Path("/auth")
    suspend fun authenticate(ctx: Context) {
        val tokenResponse: Map<String, String> = client.post(tokenUrl) {
            contentType(ContentType.Application.FormUrlEncoded)
            body = listOf(
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "grant_type" to "authorization_code",
                "code" to ctx.query("code").toString(),
                "redirect_uri" to redirectUrl,
            ).formUrlEncode()
        }

        val userResponse = client.get<Map<String, String>>("https://discord.com/api/users/@me") {
            header("Authorization", "${tokenResponse["token_type"]} ${tokenResponse["access_token"]}")
        }

        val jwt = authService.loginAccount(userResponse["id"]!!.toLong())
        ctx.setResponseCookie(Cookie("jwt", jwt))
        ctx.sendRedirect("/")
    }
}