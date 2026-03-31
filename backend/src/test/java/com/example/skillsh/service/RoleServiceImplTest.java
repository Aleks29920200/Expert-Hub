package com.example.skillsh.service;

import com.example.skillsh.domain.entity.Role;
import com.example.skillsh.repository.RoleRepo;
import com.example.skillsh.services.role.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepo roleRepo;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void testSeedRoles_WhenRolesDoNotExist_ShouldSaveThem() {
        // Указваме, че ролите липсват (връщаме празен списък - List.of())
        when(roleRepo.getRoleByName("ADMIN")).thenReturn(List.of());
        when(roleRepo.getRoleByName("EXPERT")).thenReturn(List.of());
        when(roleRepo.getRoleByName("CLIENT")).thenReturn(List.of());

        // Act
        roleService.seedRoles();

        // Assert
        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepo, times(3)).save(roleCaptor.capture());

        List<Role> savedRoles = roleCaptor.getAllValues();
        assertEquals("ADMIN", savedRoles.get(0).getName());
        assertEquals("EXPERT", savedRoles.get(1).getName());
        assertEquals("CLIENT", savedRoles.get(2).getName());
    }

    @Test
    void testSeedRoles_WhenRolesAlreadyExist_ShouldNotSaveAnything() {
        // Указваме, че ролите са намерени (връщаме списък с една роля вътре)
        when(roleRepo.getRoleByName("ADMIN")).thenReturn(List.of(new Role("ADMIN")));
        when(roleRepo.getRoleByName("EXPERT")).thenReturn(List.of(new Role("EXPERT")));
        when(roleRepo.getRoleByName("CLIENT")).thenReturn(List.of(new Role("CLIENT")));

        // Act
        roleService.seedRoles();

        // Assert
        // Уверяваме се, че save() никога не е извикан, за да не се дублират записи
        verify(roleRepo, never()).save(any(Role.class));
    }
    @Test
    void testRoles_ReturnsAllRoles() {
        // Arrange
        List<Role> mockRoles = List.of(new Role("ADMIN"), new Role("CLIENT"));
        when(roleRepo.findAll()).thenReturn(mockRoles);

        // Act
        List<Role> result = roleService.roles();

        // Assert
        assertEquals(2, result.size());
        assertEquals("ADMIN", result.get(0).getName());
        verify(roleRepo, times(1)).findAll();
    }
}
