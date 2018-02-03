package com.funny.service.auth;

import com.funny.entity.OAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OAuthServices {

    @Autowired
    private List<OAuthServiceDeractor> oAuthServiceDeractors;

    public OAuthServiceDeractor getOAuthService(OAuth.Type type) {
        Optional<OAuthServiceDeractor> oAuthService = oAuthServiceDeractors.stream().filter(o -> o.getType().equals(type))
                .findFirst();
        if (oAuthService.isPresent()) {
            return oAuthService.get();
        }
        return null;
    }

    public List<OAuthServiceDeractor> getAllOAuthServices() {
        return oAuthServiceDeractors;
    }

}
