package com.saimanikantha.securefilevault.repository;

import com.saimanikantha.securefilevault.entity.Role;
import com.saimanikantha.securefilevault.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "app.security.jwt.secret=test_secret_that_must_be_at_least_32_characters_long_for_hmac_sha_256"
})
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindUser() {
        // Arrange
        User user = User.builder()
                .username("testuser")
                .email("testuser@example.com")
                .passwordHash("hashedpassword")
                .firstName("Test")
                .lastName("User")
                .role(Role.USER)
                .build();

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();

        // Find by Username
        Optional<User> foundByUsername = userRepository.findByUsername("testuser");
        assertThat(foundByUsername).isPresent();
        assertThat(foundByUsername.get().getEmail()).isEqualTo("testuser@example.com");

        // Find by Email
        Optional<User> foundByEmail = userRepository.findByEmail("testuser@example.com");
        assertThat(foundByEmail).isPresent();
        assertThat(foundByEmail.get().getUsername()).isEqualTo("testuser");

        // Check existsByEmail
        boolean emailExists = userRepository.existsByEmail("testuser@example.com");
        assertThat(emailExists).isTrue();

        boolean nonExistentEmailExists = userRepository.existsByEmail("nonexistent@example.com");
        assertThat(nonExistentEmailExists).isFalse();
    }

}
