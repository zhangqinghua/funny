package com.funny.service.auth;

import com.funny.entity.OAuth;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public abstract class OAuthServiceDeractor implements OAuthService {

    private final OAuthService oAuthService;
    private final OAuth.Type type;
    private final String authorizationUrl;

    public OAuthServiceDeractor(OAuthService oAuthService, OAuth.Type type) {
        super();
        this.oAuthService = oAuthService;
        this.type = type;
        this.authorizationUrl = oAuthService.getAuthorizationUrl(null);
    }

    @Override
    public Token getRequestToken() {
        return oAuthService.getRequestToken();
    }

    @Override
    public Token getAccessToken(Token requestToken, Verifier verifier) {
        return oAuthService.getAccessToken(requestToken, verifier);
    }

    @Override
    public void signRequest(Token accessToken, OAuthRequest request) {
        oAuthService.signRequest(accessToken, request);
    }

    @Override
    public String getVersion() {
        return oAuthService.getVersion();
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return oAuthService.getAuthorizationUrl(requestToken);
    }

    public OAuth.Type getType() {
        return type;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public abstract OAuth getOAuthUser(Token accessToken);
}
