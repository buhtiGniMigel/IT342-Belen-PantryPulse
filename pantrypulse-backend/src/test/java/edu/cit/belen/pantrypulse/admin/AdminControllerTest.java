package edu.cit.belen.pantrypulse.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers_AsAdmin_Returns200() throws Exception {
        mockMvc.perform(get("/api/admin/users")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllUsers_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/api/admin/users")).andExpect(status().isForbidden());
    }
}