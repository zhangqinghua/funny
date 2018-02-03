package com.funny.service.auth;

import com.funny.entity.OAuth;
import com.funny.entity.User;
import com.funny.vo.JSON;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

public class GithubOAuthService extends OAuthServiceDeractor {

    private static final String PROTECTED_RESOURCE_URL = "https://api.github.com/user";

    public GithubOAuthService(OAuthService oAuthService) {
        super(oAuthService, OAuth.Type.GITHUB);
    }

    @Override
    public OAuth getOAuthUser(Token accessToken) {
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        this.signRequest(accessToken, request);
        Response response = request.send();
        OAuth oAuthUser = new OAuth();

        oAuthUser.setType(getType());

        JSON result = new JSON(response.getBody());

        oAuthUser.setOAuthId(result.getStr("$.id"));
        oAuthUser.setUser(new User());
//        oAuthUser.getUser().setUsername(result.getStr("$.login"));
        return oAuthUser;
    }
}
