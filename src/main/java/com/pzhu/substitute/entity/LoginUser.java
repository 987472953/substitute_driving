package com.pzhu.substitute.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author dengyiqing
 * @description spring security 的用户登录类
 * @date 2022/1/11
 */
public class LoginUser implements UserDetails {

    private UserInfo userInfo;

    public LoginUser(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return userInfo.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return userInfo.getPhoneNum() ;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
