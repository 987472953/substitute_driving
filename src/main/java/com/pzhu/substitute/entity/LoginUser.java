package com.pzhu.substitute.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dengyiqing
 * @description spring security 的用户登录类
 * @date 2022/1/11
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {

    private UserInfo userInfo;

    private List<String> permissions;

    public LoginUser(UserInfo userInfo, List<String> permissions) {
        this.userInfo = userInfo;
        this.permissions = permissions;
    }

    @JsonIgnore
    private List<SimpleGrantedAuthority> authorities;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将 permissions 中的信息封装为SimpleGrantedAuthority对象
        if (authorities != null) {
            return authorities;
        }
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return userInfo.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return userInfo.getPhoneNum();
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
