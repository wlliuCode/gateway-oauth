package com.wlliu.authorization;

import cn.hutool.core.convert.Convert;
import com.wlliu.constant.AuthConstant;
import com.wlliu.constant.RedisConstant;
import com.wlliu.domain.SysPermission;
import com.wlliu.domain.SysRole;
import com.wlliu.mapper.PermissionMapper;
import com.wlliu.mapper.RoleMapper;
import com.wlliu.service.PermissionService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 鉴权管理器，用于判断是否有资源的访问权限
 * Created by macro on 2020/6/19.
 */
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    private Map<String, List<String>> resourceRolesMap;


    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {


        /*List<SysPermission> allPermissionWithRoleNames = permissionMapper.findAllPermissionWithRoleNames();
        resourceRolesMap = new TreeMap<>();

        System.out.println(allPermissionWithRoleNames);

        for (SysPermission sysPermission : allPermissionWithRoleNames) {
            resourceRolesMap.put(sysPermission.getPermissionUrl(),sysPermission.getRoleNames());
        }

        redisTemplate.opsForHash().putAll(RedisConstant.RESOURCE_ROLES_MAP, resourceRolesMap);*/

        //从Redis中获取当前路径可访问角色列表

        URI uri = authorizationContext.getExchange().getRequest().getURI();



        System.out.println(uri.getPath());
        /*Object obj = redisTemplate.opsForHash().get(RedisConstant.RESOURCE_ROLES_MAP, uri.getPath());
        System.out.println("obj  " +obj);
        List<String> authorities = Convert.toList(String.class,obj);*/

        List<String> authorities = roleMapper.findByPermissionUrl(uri.getPath());

        System.out.println("authorities  "+authorities);
        authorities = authorities.stream().map(i -> i = AuthConstant.AUTHORITY_PREFIX + i).collect(Collectors.toList());
        System.out.println(authorities);
        //认证通过且角色匹配的用户可访问当前路径
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

}
