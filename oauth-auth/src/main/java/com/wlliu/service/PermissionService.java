package com.wlliu.service;

import com.wlliu.domain.SysPermission;

import java.util.List;

public interface PermissionService {
    List<SysPermission> findAllPermissionWithRoles();
}
