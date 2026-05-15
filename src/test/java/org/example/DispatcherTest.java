package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.*;

class DispatcherTest {

    @Test
    void dispatcherAnnotation_shouldExist() {
        SpringBootApplication ann = Dispatcher.class.getAnnotation(SpringBootApplication.class);
        assertNotNull(ann);
    }
}
