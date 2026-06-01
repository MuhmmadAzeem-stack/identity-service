package com.omnicore.identity.permission;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.omnicore.identity.permission.error.PermissionBusinessException;
import com.omnicore.identity.permission.error.PermissionErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermissionDependencyValidator {

  private final PermissionRepository permissionRepository;
  private final PermissionDependencyRepository permissionDependencyRepository;

  public List<Permission> resolveAndValidateDependencies(
      Long permissionId, List<Long> dependencyIds) {
    if (dependencyIds == null || dependencyIds.isEmpty()) {
      return List.of();
    }

    if (dependencyIds.size() != new HashSet<>(dependencyIds).size()) {
      throw new PermissionBusinessException(PermissionErrorCode.DUPLICATE_DEPENDENCY_PERMISSION);
    }

    List<Permission> dependencies = new ArrayList<>();
    for (Long dependencyId : dependencyIds) {
      if (permissionId != null && Objects.equals(permissionId, dependencyId)) {
        throw new PermissionBusinessException(
            PermissionErrorCode.PERMISSION_SELF_DEPENDENCY_NOT_ALLOWED);
      }

      Permission dependency =
          permissionRepository
              .findById(dependencyId)
              .orElseThrow(
                  () ->
                      new PermissionBusinessException(
                          PermissionErrorCode.DEPENDENCY_PERMISSION_NOT_FOUND));

      if (!dependency.isActive() || dependency.isDeleted()) {
        throw new PermissionBusinessException(PermissionErrorCode.DEPENDENCY_PERMISSION_INACTIVE);
      }

      dependencies.add(dependency);
    }

    validateNoCircularDependency(permissionId, dependencyIds);
    return dependencies;
  }

  private void validateNoCircularDependency(Long permissionId, List<Long> dependencyIds) {
    if (permissionId == null) {
      return;
    }

    Map<Long, Set<Long>> graph = loadDependencyGraph();
    for (Long dependencyId : dependencyIds) {
      if (wouldCreateCycle(graph, permissionId, dependencyId)) {
        throw new PermissionBusinessException(PermissionErrorCode.PERMISSION_CIRCULAR_DEPENDENCY);
      }
      graph.computeIfAbsent(permissionId, ignored -> new LinkedHashSet<>()).add(dependencyId);
    }
  }

  private Map<Long, Set<Long>> loadDependencyGraph() {
    Map<Long, Set<Long>> graph = new HashMap<>();
    for (Object[] pair : permissionDependencyRepository.findAllDependencyPairs()) {
      Long permissionId = ((Number) pair[0]).longValue();
      Long dependencyId = ((Number) pair[1]).longValue();
      graph.computeIfAbsent(permissionId, ignored -> new LinkedHashSet<>()).add(dependencyId);
    }
    return graph;
  }

  private boolean wouldCreateCycle(
      Map<Long, Set<Long>> graph, Long permissionId, Long dependsOnId) {
    Deque<Long> pending = new ArrayDeque<>();
    Set<Long> visited = new HashSet<>();
    pending.add(dependsOnId);

    while (!pending.isEmpty()) {
      Long current = pending.removeFirst();
      if (Objects.equals(permissionId, current)) {
        return true;
      }
      if (!visited.add(current)) {
        continue;
      }
      for (Long next : graph.getOrDefault(current, Set.of())) {
        pending.addLast(next);
      }
    }

    return false;
  }
}
