package com.orgest.bank.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;


    @Test
    void openAccountReturns201WithTheNewAccount() throws Exception {
        when(accountService.openAccount("Alice")).thenReturn(new Account("ACC-1", "Alice"));

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"owner": "Alice"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("ACC-1"))
                .andExpect(jsonPath("$.owner").value("Alice"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void getUnknownAccountReturns404() throws Exception {
        when(accountService.getAccount("nope")).thenThrow(new AccountNotFoundException("nope"));

        mockMvc.perform(get("/api/accounts/nope"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Account not found: nope"));
    }

    @Test
    void depositWithNegativeAmountIsRejected() throws Exception {
        mockMvc.perform(post("/api/accounts/ACC-1/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": -5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").exists());

        verify(accountService, never()).deposit(any(), any());
    }


}