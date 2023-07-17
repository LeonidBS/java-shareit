package ru.practicum.shareit.exception;

import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {
    private MockMvc mvc;

    @Mock
    private ExceptionController exceptionController;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(exceptionController)
                .setControllerAdvice((new ErrorHandler()))
                .build();
    }

    @Test
    void handleMyValidationException() throws Exception {
        when(exceptionController.getMyValidationException())
                .thenThrow(new MyValidationException("bad_request"));

        mvc.perform(MockMvcRequestBuilders.get("/exception/bad_request")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof MyValidationException));

    }

    @Test
    void handleRequestOfNotExistUserException() throws Exception {
        when(exceptionController.getIdNotFoundException(1))
                .thenThrow(new IdNotFoundException("not_found"));

        mvc.perform(MockMvcRequestBuilders.get("/exception/not_found/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof IdNotFoundException));
    }

    @Test
    void handleAccessDeniedException() throws Exception {
        when(exceptionController.getIdNotFoundException(2))
                .thenThrow(new AccessDeniedException("not_found"));

        mvc.perform(MockMvcRequestBuilders.get("/exception/not_found/" + 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof AccessDeniedException));
    }

    @Test
    void handleApprovingException() throws Exception {
        when(exceptionController.getIdNotFoundException(3))
                .thenThrow(new ApprovingException("not_found"));

        mvc.perform(MockMvcRequestBuilders.get("/exception/not_found/" + 3)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof ApprovingException));
    }

    @Test
    void getDataIntegrityViolationException() throws Exception {
        when(exceptionController.getDataIntegrityViolationException(1))
                .thenThrow(new HibernateException("conflict"));

        mvc.perform(MockMvcRequestBuilders.get("/exception/conflict/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof HibernateException));
    }

    @Test
    void handleHibernateException() throws Exception {
        when(exceptionController.getDataIntegrityViolationException(2))
                .thenThrow(new HibernateException("conflict"));

        mvc.perform(MockMvcRequestBuilders.get("/exception/conflict/" + 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result
                        .getResolvedException() instanceof HibernateException));
    }

    @Test
    void getMyValidationException() {
    }

    @Test
    void getIdNotFoundException() {
    }

    @Test
    void testGetDataIntegrityViolationException() {
    }
}