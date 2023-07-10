package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDbServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDbService userDbService;

    private static User newUser = new User();
    private static User existUser = new User();
    private static User updatedUser = new User();

    @BeforeAll
    static void setUp() {
        newUser = User.builder()
                .name("newUser")
                .email("new.user@user.com")
                .build();

        existUser = User.builder()
                .id(1)
                .name("existUser")
                .email("exist.user@user.com")
                .build();

        updatedUser = User.builder()
                .id(1)
                .name("updatedUser")
                .email("updated.user@user.com")
                .build();
    }

    @Test
    void findAllWhenRequestedAllPages() {
        Pageable pageable1 = PageRequest.of(0, 5);
        Pageable pageable2 = PageRequest.of(1, 5);

        List<User> list1 = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            list1.add(User.builder()
                    .id(i)
                    .name("user" + i)
                    .email("user" + i + "@user.com")
                    .build());
        }

        Page<User> page1 = new PageImpl<>(list1, pageable1, 0);
        List<User> list2 = new ArrayList<>();

        for (int j = 6; j < 9; j++) {
            list1.add(User.builder()
                    .id(j)
                    .name("user" + j)
                    .email("user" + j + "@user.com")
                    .build());
        }

        Page<User> page2 = new PageImpl<>(list2, pageable1, 0);

        when(userRepository.findAll(pageable1)).thenReturn(page1);
        List<User> qwe = page1.toList();
        when(userRepository.findAll(pageable2)).thenReturn(page2);

        List<UserDto> retrievedPage1 = userDbService.findAll(0, 5);
        List<UserDto> retrievedPage2 = userDbService.findAll(5, 5);

        assertEquals(retrievedPage1, UserMapper.mapListToUserDto(page1.toList()));
        assertEquals(retrievedPage2,  UserMapper.mapListToUserDto(page2.toList()));
        verify(userRepository, times(1))
                .findAll(pageable1);
        verify(userRepository, times(1))
                .findAll(pageable2);
    }

    @Test
    void findByIdWhenUserFoundThenReturnedUser() {
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(existUser));

        UserDto retrivedUserDto = userDbService.findById(userId);

        assertEquals(UserMapper.mapToUserDto(existUser), retrivedUserDto);

        verify(userRepository, times(1))
                .findById(userId);
    }

    @Test
    void findByIdWhenUserNotFoundThenExceptionThrown() {
        int userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class,
                () -> userDbService.findById(userId));
        assertEquals("There is no User with ID: " + userId,
                idNotFoundException.getMessage());

        verify(userRepository, times(1))
                .findById(userId);
    }

    @Test
    void updateWhenUserFoundAndUpdatedThenReturnedUpdatedUser() {

        when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.of(existUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        UserDto retrivedUserDto = userDbService.update(updatedUser);

        assertEquals(UserMapper.mapToUserDto(updatedUser), retrivedUserDto);

        InOrder inOrder = inOrder(userRepository, userRepository);
        inOrder.verify(userRepository, times(1))
                .findById(updatedUser.getId());
        inOrder.verify(userRepository, times(1))
                .save(updatedUser);
    }

    @Test
    void updateWhenUserNotFoundNotUpdatedThenExceptionThrown() {

        when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.empty());

        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class,
                () -> userDbService.update(updatedUser));
        assertEquals("There is no User with ID: " + updatedUser.getId(),
                idNotFoundException.getMessage());

        verify(userRepository, times(1))
                .findById(updatedUser.getId());
        verify(userRepository, times(0))
                .save(updatedUser);
    }

    @Test
    void updateByPatchWhenUserFoundAndUpdatedThenReturnedUpdatedUser() {

        when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.of(existUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        UserDto updatedUserDto = UserMapper.mapToUserDto(updatedUser);

        UserDto retrivedUserDto = userDbService.update(updatedUser);

        assertEquals(updatedUserDto, retrivedUserDto);

        InOrder inOrder = inOrder(userRepository, userRepository);
        inOrder.verify(userRepository, times(1))
                .findById(updatedUser.getId());
        inOrder.verify(userRepository, times(1))
                .save(updatedUser);
    }

    @Test
    void updateByPatchWhenUserNotFoundNotUpdatedThenExceptionThrown() {
        UserDto updatedUserDto = UserMapper.mapToUserDto(updatedUser);
        when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.empty());

        IdNotFoundException idNotFoundException = assertThrows(IdNotFoundException.class,
                () -> userDbService.updateByPatch(updatedUserDto, updatedUser.getId()));
        assertEquals("There is no User with ID: " + updatedUser.getId(),
                idNotFoundException.getMessage());

        verify(userRepository, times(1))
                .findById(updatedUser.getId());
        verify(userRepository, times(0))
                .save(updatedUser);
    }
}