package adopet.web.controller;

import adopet.application.shelterandpet.DeleteShelterCommandHandler;
import adopet.application.shelterandpet.ListSheltersQuery;
import adopet.application.shelterandpet.RegisterShelterCommandHandler;
import adopet.application.shelterandpet.UpdateShelterCommandHandler;
import adopet.domain.shelterandpet.Email;
import adopet.domain.shelterandpet.PhoneNumber;
import adopet.domain.shelterandpet.Shelter;
import adopet.exception.EntityNotFoundException;
import adopet.gateway.ShelterGateway;
import adopet.web.exception.WebExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShelterController.class)
@Import(WebExceptionHandler.class)
class ShelterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShelterGateway shelterGateway;

    @MockitoBean
    private ListSheltersQuery listSheltersQuery;

    @MockitoBean
    private RegisterShelterCommandHandler registerShelterCommandHandler;

    @MockitoBean
    private UpdateShelterCommandHandler updateShelterCommandHandler;

    @MockitoBean
    private DeleteShelterCommandHandler deleteShelterCommandHandler;

    @Test
    void shouldListShelters() throws Exception {
        List<Shelter> shelters = List.of(
                new Shelter(1L, "Abrigo 1", new PhoneNumber("31999999999"), new Email("abrigo1@email.com")),
                new Shelter(2L, "Abrigo 2", new PhoneNumber("31888888888"), new Email("abrigo2@email.com"))
        );

        when(listSheltersQuery.execute()).thenReturn(shelters);

        mockMvc.perform(get("/shelters"))
                .andExpect(status().isOk())
                .andExpect(view().name("shelters/list"))
                .andExpect(model().attributeExists("shelters"))
                .andExpect(model().attribute("shelters", shelters));
    }

    @Test
    void shouldShowCreateForm() throws Exception {
        mockMvc.perform(get("/shelters/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("shelters/create"))
                .andExpect(model().attributeExists("shelterForm"));
    }

    @Test
    void shouldCreateShelterSuccessfully() throws Exception {
        mockMvc.perform(post("/shelters")
                        .param("name", "Abrigo Central")
                        .param("phoneNumber", "31999999999")
                        .param("email", "abrigo@email.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shelters"))
                .andExpect(flash().attribute("successMessage", "Abrigo cadastrado com sucesso."));

        verify(registerShelterCommandHandler).execute(
                eq("Abrigo Central"),
                eq("31999999999"),
                eq("abrigo@email.com")
        );
    }

    @Test
    void shouldReturnCreateFormWhenCreateShelterValidationFails() throws Exception {
        mockMvc.perform(post("/shelters")
                        .param("name", "")
                        .param("phoneNumber", "")
                        .param("email", "email-invalido"))
                .andExpect(status().isOk())
                .andExpect(view().name("shelters/create"))
                .andExpect(model().attributeHasFieldErrors("shelterForm", "name", "phoneNumber", "email"));
    }

    @Test
    void shouldShowEditForm() throws Exception {
        Shelter shelter = new Shelter(
                1L,
                "Abrigo Central",
                new PhoneNumber("31999999999"),
                new Email("abrigo@email.com")
        );

        when(shelterGateway.findById(1L)).thenReturn(Optional.of(shelter));

        mockMvc.perform(get("/shelters/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("shelters/edit"))
                .andExpect(model().attributeExists("shelterId"))
                .andExpect(model().attributeExists("shelterForm"))
                .andExpect(model().attribute("shelterId", 1L));
    }

    @Test
    void shouldReturnErrorViewWhenEditFormShelterNotFound() throws Exception {
        when(shelterGateway.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/shelters/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void shouldUpdateShelterSuccessfully() throws Exception {
        mockMvc.perform(post("/shelters/1")
                        .param("name", "Abrigo Atualizado")
                        .param("phoneNumber", "31888888888")
                        .param("email", "novo@email.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shelters"))
                .andExpect(flash().attribute("successMessage", "Abrigo atualizado com sucesso."));

        verify(updateShelterCommandHandler).execute(
                eq(1L),
                eq("Abrigo Atualizado"),
                eq("31888888888"),
                eq("novo@email.com")
        );
    }

    @Test
    void shouldReturnEditFormWhenUpdateShelterValidationFails() throws Exception {
        mockMvc.perform(post("/shelters/1")
                        .param("name", "")
                        .param("phoneNumber", "")
                        .param("email", "email-invalido"))
                .andExpect(status().isOk())
                .andExpect(view().name("shelters/edit"))
                .andExpect(model().attribute("shelterId", 1L))
                .andExpect(model().attributeHasFieldErrors("shelterForm", "name", "phoneNumber", "email"));
    }

    @Test
    void shouldDeleteShelterSuccessfully() throws Exception {
        doNothing().when(deleteShelterCommandHandler).execute(1L);

        mockMvc.perform(post("/shelters/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shelters"))
                .andExpect(flash().attribute("successMessage", "Abrigo excluído com sucesso."));

        verify(deleteShelterCommandHandler).execute(1L);
    }

    @Test
    void shouldRenderErrorViewWhenDeleteShelterThrowsBusinessException() throws Exception {
        doThrow(new EntityNotFoundException("Abrigo não encontrado (id=1)."))
                .when(deleteShelterCommandHandler).execute(1L);

        mockMvc.perform(post("/shelters/1/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorMessage", "Abrigo não encontrado (id=1)."));
    }
}