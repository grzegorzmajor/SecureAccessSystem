package ovh.major.secure_access_system.test_controllers;

import lombok.Builder;

@Builder
public record TestRecord(
        //Long id,
        String description,
        String somethingElse
) {
}
