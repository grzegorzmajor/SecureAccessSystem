package ovh.major.secure_access_system.test_controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@AllArgsConstructor
@RequestMapping("/test")
@Tag(name = "Testing Authorities",
description = "Here You can test authorities.")
class TestController {

    @GetMapping("/opened")
    @Operation(description = "This endpoint does not require authorization, anyone can use it.")
    public ResponseEntity<TestRecord> getSomethingOpened() {
        String response =  "That everybody can see..";
        return ResponseEntity.ok(TestRecord.builder()
                .description(response)
                .somethingElse("You welcome!")
                .build());
    }
    @GetMapping(path ="/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "AccessToken")
    @PreAuthorize("hasAuthority('BASIC_USER')")
    @Operation(description = """
    <strong>This endpoint does require authorization.<br><br>
    Requirements:<br>
    &emsp;AccessToken in Authorization Bearer header,<br>
    &emsp;Logged in User with 'BASIC_USER' authorities.<br></strong>
    """)
    public ResponseEntity<TestRecord> getSomething() {
        String response =  "If you can see this text, You logged in success for rest api.";
        return ResponseEntity.ok(TestRecord.builder()
                .description(response)
                .somethingElse("You welcome!")
                .build());
    }


    @GetMapping(path = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    @SecurityRequirement(name = "AccessToken")
    @Operation(description = """
    <strong>This endpoint does require authorization.<br><br>
    Requirements:<br>
    &emsp;AccessToken in Authorization Bearer header,<br>
    &emsp;Logged in User with 'ADMIN' authorities.<br></strong>
    """)
    public ResponseEntity<TestRecord> getSomethingAsAdmin() {
        String response =  "If you can see this text, You logged in success for rest api as admin.";
        return ResponseEntity.ok(TestRecord.builder()
                .description(response)
                .somethingElse("You are Admin?")
                .build());
    }
}
