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
 * @description 驾驶员登录
 * @date 2022/1/18
 */
@Data
@NoArgsConstructor
public class LoginDriver implements UserDetails {

    private static final long serialVersionUID = 4636703231606879084L;
    private DriverInfo driverInfo;

    private List<String> permissions;

    public LoginDriver(DriverInfo driverInfo, List<String> permissions) {
        this.driverInfo = driverInfo;
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
        return driverInfo.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return driverInfo.getPhoneNum();
    }

    public DriverInfo getDriverInfo() {
        return driverInfo;
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
