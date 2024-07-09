package com.herbst.vortexbank.v1.controllers;

import com.herbst.vortexbank.util.MediaType;
import com.herbst.vortexbank.v1.dtos.*;
import com.herbst.vortexbank.v1.services.AccountService;
import com.herbst.vortexbank.v1.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@Tag(name = "Account Controller V1", description = "Endpoints V1 for Accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/account", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Create Account", description = "Endpoint for create Account", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = AccountDTO.class)),
                    @Content(mediaType = "application/x-yaml", schema = @Schema(implementation = AccountDTO.class))
            }),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
    })
    public ResponseEntity<AccountDTO> create(@RequestBody CreateAccountDTO accountDataDTO){
        return ResponseEntity.ok(authService.create(accountDataDTO));
    }
    @PostMapping(value = "/account/password", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Create Account Password", description = "Endpoint for create account password", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    public ResponseEntity createAccountPassword(@RequestBody CreateAccountPasswordDTO accountPasswordDTO){
        if(accountService.createAccountPassword(accountPasswordDTO)) return ResponseEntity.accepted().build();
        return ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/account/signin", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Sign in", description = "Endpoint for Sign in", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = TokenDTO.class)),
                    @Content(mediaType = "application/xml", schema = @Schema(implementation = TokenDTO.class)),
                    @Content(mediaType = "application/x-yaml", schema = @Schema(implementation = TokenDTO.class))
            }),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    public ResponseEntity<TokenDTO> signin(@RequestBody AccountSignInDTO accountSignInDTO){
        TokenDTO tokenDTO = authService.signin(accountSignInDTO);
        return ResponseEntity.ok().body(tokenDTO);
    }

    @PostMapping(value = "/account/newpassword", consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
            produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
    @Operation(summary = "Change Account Password", description = "Endpoint for change account password", responses = {
            @ApiResponse(description = "Success", responseCode = "201", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
    })
    public ResponseEntity changeAccountPassword(@RequestBody ChangeAccountPasswordDTO changeAccountPasswordDTO){
        if(accountService.changeAccountPassword(changeAccountPasswordDTO)) return ResponseEntity.accepted().build();
        return ResponseEntity.notFound().build();
    }
}
