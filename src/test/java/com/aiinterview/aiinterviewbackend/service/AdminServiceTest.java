package com.aiinterview.aiinterviewbackend.service;

import com.aiinterview.aiinterviewbackend.dto.AdminDashboardStatsResponse;
import com.aiinterview.aiinterviewbackend.dto.AdminLoginRequest;
import com.aiinterview.aiinterviewbackend.dto.AdminLoginResponse;
import com.aiinterview.aiinterviewbackend.dto.AdminRegisterRequest;

import com.aiinterview.aiinterviewbackend.entity.Admin;
import com.aiinterview.aiinterviewbackend.entity.Interview;
import com.aiinterview.aiinterviewbackend.entity.User;

import com.aiinterview.aiinterviewbackend.repository.AdminRepository;
import com.aiinterview.aiinterviewbackend.repository.InterviewAnswerRepository;
import com.aiinterview.aiinterviewbackend.repository.InterviewRepository;
import com.aiinterview.aiinterviewbackend.repository.UserRepository;

import com.aiinterview.aiinterviewbackend.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private InterviewAnswerRepository interviewAnswerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    // =========================================================
    // JWT SERVICE MOCK
    // =========================================================

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AdminService adminService;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
    }


    // =========================================================
    // ADMIN REGISTER TEST
    // =========================================================

    @Test
    void shouldRegisterAdminSuccessfully() {

        AdminRegisterRequest request =
                new AdminRegisterRequest();

        request.setName("Test Admin");
        request.setEmail("ADMIN@TEST.COM");
        request.setPassword("password123");


        when(
                adminRepository.existsByEmail(
                        "admin@test.com"
                )
        )
        .thenReturn(false);


        when(
                passwordEncoder.encode(
                        "password123"
                )
        )
        .thenReturn("encoded-password");


        Admin savedAdmin =
                new Admin();

        savedAdmin.setId(1L);
        savedAdmin.setName("Test Admin");
        savedAdmin.setEmail("admin@test.com");
        savedAdmin.setPassword(
                "encoded-password"
        );


        when(
                adminRepository.save(
                        any(Admin.class)
                )
        )
        .thenReturn(savedAdmin);


        Admin result =
                adminService.register(request);


        assertNotNull(result);

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                "Test Admin",
                result.getName()
        );

        assertEquals(
                "admin@test.com",
                result.getEmail()
        );


        verify(
                adminRepository,
                times(1)
        ).save(any(Admin.class));


        verify(
                passwordEncoder,
                times(1)
        ).encode("password123");
    }


    // =========================================================
    // DUPLICATE ADMIN TEST
    // =========================================================

    @Test
    void shouldRejectDuplicateAdmin() {

        AdminRegisterRequest request =
                new AdminRegisterRequest();

        request.setName("Test Admin");
        request.setEmail("admin@test.com");
        request.setPassword("password123");


        when(
                adminRepository.existsByEmail(
                        "admin@test.com"
                )
        )
        .thenReturn(true);


        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                adminService.register(
                                        request
                                )
                );


        assertEquals(
                "Admin with this email already exists",
                exception.getMessage()
        );


        verify(
                adminRepository,
                never()
        ).save(any(Admin.class));
    }


    // =========================================================
    // ADMIN LOGIN SUCCESS TEST
    // =========================================================

    @Test
    void shouldLoginAdminSuccessfully() {

        AdminLoginRequest request =
                new AdminLoginRequest();

        request.setEmail(
                "ADMIN@TEST.COM"
        );

        request.setPassword(
                "password123"
        );


        Admin admin =
                new Admin();

        admin.setId(1L);
        admin.setName("Test Admin");
        admin.setEmail("admin@test.com");
        admin.setPassword(
                "encoded-password"
        );


        when(
                adminRepository.findByEmail(
                        "admin@test.com"
                )
        )
        .thenReturn(
                Optional.of(admin)
        );


        when(
                passwordEncoder.matches(
                        "password123",
                        "encoded-password"
                )
        )
        .thenReturn(true);


        // =====================================================
        // JWT TOKEN MOCK
        // =====================================================

        when(
                jwtService.generateToken(
                        "admin@test.com"
                )
        )
        .thenReturn(
                "test-jwt-token"
        );


        // =====================================================
        // LOGIN
        // =====================================================

        AdminLoginResponse result =
                adminService.login(request);


        // =====================================================
        // ASSERTIONS
        // =====================================================

        assertNotNull(result);

        assertEquals(
                1L,
                result.getId()
        );

        assertEquals(
                "Test Admin",
                result.getName()
        );

        assertEquals(
                "admin@test.com",
                result.getEmail()
        );

        assertEquals(
                "test-jwt-token",
                result.getToken()
        );


        // =====================================================
        // VERIFY PASSWORD
        // =====================================================

        verify(
                passwordEncoder,
                times(1)
        ).matches(
                "password123",
                "encoded-password"
        );


        // =====================================================
        // VERIFY JWT
        // =====================================================

        verify(
                jwtService,
                times(1)
        ).generateToken(
                "admin@test.com"
        );
    }


    // =========================================================
    // ADMIN LOGIN WRONG PASSWORD
    // =========================================================

    @Test
    void shouldRejectWrongPassword() {

        AdminLoginRequest request =
                new AdminLoginRequest();

        request.setEmail(
                "admin@test.com"
        );

        request.setPassword(
                "wrong-password"
        );


        Admin admin =
                new Admin();

        admin.setEmail(
                "admin@test.com"
        );

        admin.setPassword(
                "encoded-password"
        );


        when(
                adminRepository.findByEmail(
                        "admin@test.com"
                )
        )
        .thenReturn(
                Optional.of(admin)
        );


        when(
                passwordEncoder.matches(
                        "wrong-password",
                        "encoded-password"
                )
        )
        .thenReturn(false);


        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                adminService.login(
                                        request
                                )
                );


        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );


        // JWT should NOT be generated
        verify(
                jwtService,
                never()
        ).generateToken(anyString());
    }


    // =========================================================
    // ADMIN LOGIN UNKNOWN EMAIL
    // =========================================================

    @Test
    void shouldRejectUnknownEmail() {

        AdminLoginRequest request =
                new AdminLoginRequest();

        request.setEmail(
                "unknown@test.com"
        );

        request.setPassword(
                "password123"
        );


        when(
                adminRepository.findByEmail(
                        "unknown@test.com"
                )
        )
        .thenReturn(
                Optional.empty()
        );


        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                adminService.login(
                                        request
                                )
                );


        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );


        // JWT should NOT be generated
        verify(
                jwtService,
                never()
        ).generateToken(anyString());
    }


    // =========================================================
    // DASHBOARD STATS TEST
    // =========================================================

    @Test
    void shouldCalculateDashboardStats() {

        when(
                userRepository.count()
        )
        .thenReturn(5L);


        when(
                interviewRepository.count()
        )
        .thenReturn(3L);


        User user =
                new User(
                        "Test User",
                        "user@test.com",
                        "9876543210",
                        "password"
                );


        Interview interview1 =
                new Interview(
                        user,
                        "Java",
                        "easy",
                        80,
                        10,
                        LocalDateTime.now()
                );


        Interview interview2 =
                new Interview(
                        user,
                        "Spring Boot",
                        "medium",
                        60,
                        10,
                        LocalDateTime.now()
                );


        Interview interview3 =
                new Interview(
                        user,
                        "React",
                        "hard",
                        90,
                        10,
                        LocalDateTime.now()
                );


        when(
                interviewRepository.findAll()
        )
        .thenReturn(
                List.of(
                        interview1,
                        interview2,
                        interview3
                )
        );


        AdminDashboardStatsResponse result =
                adminService.getDashboardStats();


        assertNotNull(result);


        assertEquals(
                5L,
                result.getTotalUsers()
        );


        assertEquals(
                3L,
                result.getTotalInterviews()
        );


        assertEquals(
                76.67,
                result.getAverageScore()
        );


        assertEquals(
                90,
                result.getHighestScore()
        );
    }


    // =========================================================
    // EMPTY DASHBOARD TEST
    // =========================================================

    @Test
    void shouldReturnZeroStatsWhenNoInterviews() {

        when(
                userRepository.count()
        )
        .thenReturn(0L);


        when(
                interviewRepository.count()
        )
        .thenReturn(0L);


        when(
                interviewRepository.findAll()
        )
        .thenReturn(
                List.of()
        );


        AdminDashboardStatsResponse result =
                adminService.getDashboardStats();


        assertNotNull(result);


        assertEquals(
                0L,
                result.getTotalUsers()
        );


        assertEquals(
                0L,
                result.getTotalInterviews()
        );


        assertEquals(
                0.0,
                result.getAverageScore()
        );


        assertEquals(
                0,
                result.getHighestScore()
        );
    }


    // =========================================================
    // TOPIC ANALYTICS TEST
    // =========================================================

    @Test
    void shouldCalculateAverageScoreByTopic() {

        User user =
                new User(
                        "Test User",
                        "user@test.com",
                        "9876543210",
                        "password"
                );


        Interview java1 =
                new Interview(
                        user,
                        "Java",
                        "easy",
                        80,
                        10,
                        LocalDateTime.now()
                );


        Interview java2 =
                new Interview(
                        user,
                        "Java",
                        "medium",
                        60,
                        10,
                        LocalDateTime.now()
                );


        Interview react =
                new Interview(
                        user,
                        "React",
                        "easy",
                        90,
                        10,
                        LocalDateTime.now()
                );


        when(
                interviewRepository.findAll()
        )
        .thenReturn(
                List.of(
                        java1,
                        java2,
                        react
                )
        );


        Map<String, Double> result =
                adminService
                        .getAverageScoreByTopic();


        assertNotNull(result);


        assertEquals(
                70.0,
                result.get("Java")
        );


        assertEquals(
                90.0,
                result.get("React")
        );
    }


    // =========================================================
    // DIFFICULTY ANALYTICS TEST
    // =========================================================

    @Test
    void shouldCalculateAverageScoreByDifficulty() {

        User user =
                new User(
                        "Test User",
                        "user@test.com",
                        "9876543210",
                        "password"
                );


        Interview easy1 =
                new Interview(
                        user,
                        "Java",
                        "easy",
                        80,
                        10,
                        LocalDateTime.now()
                );


        Interview easy2 =
                new Interview(
                        user,
                        "React",
                        "easy",
                        60,
                        10,
                        LocalDateTime.now()
                );


        Interview hard =
                new Interview(
                        user,
                        "Spring",
                        "hard",
                        90,
                        10,
                        LocalDateTime.now()
                );


        when(
                interviewRepository.findAll()
        )
        .thenReturn(
                List.of(
                        easy1,
                        easy2,
                        hard
                )
        );


        Map<String, Double> result =
                adminService
                        .getAverageScoreByDifficulty();


        assertNotNull(result);


        assertEquals(
                70.0,
                result.get("easy")
        );


        assertEquals(
                90.0,
                result.get("hard")
        );
    }
}