package com.funny.repository;

import com.funny.entity.OAuth;

public interface OAuthRepository extends BaseRepository<OAuth> {

    OAuth findByTypeAndOAuthId(OAuth.Type type, String oAuthId);

}
