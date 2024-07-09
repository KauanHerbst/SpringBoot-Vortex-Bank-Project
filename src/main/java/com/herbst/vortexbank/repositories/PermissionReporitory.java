package com.herbst.vortexbank.repositories;

import com.herbst.vortexbank.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionReporitory extends JpaRepository<Permission, Long> {
}
