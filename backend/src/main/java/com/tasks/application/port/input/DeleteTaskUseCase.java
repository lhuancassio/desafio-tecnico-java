package com.tasks.application.port.input;

import java.util.UUID;

public interface DeleteTaskUseCase {

    void delete(UUID id);
}
