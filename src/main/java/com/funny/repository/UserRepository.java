package com.funny.repository;

import com.funny.entity.User;

public interface UserRepository extends BaseRepository<User> {

    User findByMobileOrEmail(String mobile, String email);

    User findByOAuthTypeAndOAuthId(User.OAuthType oAuthType, String oAuthId);
}
