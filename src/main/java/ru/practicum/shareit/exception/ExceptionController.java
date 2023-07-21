package ru.practicum.shareit.exception;


import org.hibernate.HibernateException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/exception")
public class ExceptionController {

    @GetMapping("/bad_request")
    Exception getMyValidationException() {
        return new MyValidationException("bad_request");
    }

    @GetMapping("/not_found/{e}")
    Exception getIdNotFoundException(@PathVariable Integer e) {
        if (e == 1) {
            return new IdNotFoundException("not_found");
        } else if (e == 2) {
            return new AccessDeniedException("not_found");
        } else {
            return new ApprovingException("not_found");
        }
    }

    @GetMapping("/conflict/{e}")
    Exception getDataIntegrityViolationException(@PathVariable Integer e) {
        if (e == 1) {
            return new DataIntegrityViolationException("conflict");
        } else {
            return new HibernateException("conflict");
        }
    }
}
