package adopet.web.controller;


import adopet.application.shelterandpet.*;
import adopet.domain.shelterandpet.*;
import adopet.exception.EntityNotFoundException;
import adopet.gateway.PetGateway;
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

@WebMvcTest(PetController.class)
@Import(WebExceptionHandler.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShelterGateway shelterGateway;

    @MockitoBean
    private PetGateway petGateway;

    @MockitoBean
    private ListShelterPetsQuery listShelterPetsQuery;

    @MockitoBean
    private RegisterPetCommandHandler registerPetCommandHandler;

    @MockitoBean
    private UpdatePetCommandHandler updatePetCommandHandler;

    @MockitoBean
    private DeletePetCommandHandler deletePetCommandHandler;

    @MockitoBean
    private ImportShelterPetsCommandHandler importShelterPetsCommandHandler;

    @MockitoBean
    private ExportShelterPetsCommandHandler exportShelterPetsCommandHandler;

    @Test
    void shouldListPets() throws Exception {
        Shelter shelter = new Shelter(
                1L,
                "Abrigo Central",
                new PhoneNumber("31999999999"),
                new Email("abrigo@email.com")
        );

        List<Pet> pets = List.of(
                new Pet(1L, PetType.CACHORRO, new PetName("Rex"), "Vira-lata", new AgeYears(2), "Caramelo", new WeightKg(10.5), PetStatus.AVAILABLE),
                new Pet(2L, PetType.GATO, new PetName("Mimi"), "Siamês", new AgeYears(3), "Branco", new WeightKg(4.2), PetStatus.AVAILABLE)
        );

        when(shelterGateway.findById(1L)).thenReturn(Optional.of(shelter));
        when(listShelterPetsQuery.execute("1")).thenReturn(pets);

        mockMvc.perform(get("/shelters/1/pets"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/list"))
                .andExpect(model().attributeExists("shelter"))
                .andExpect(model().attributeExists("pets"))
                .andExpect(model().attributeExists("petImportForm"))
                .andExpect(model().attributeExists("petExportForm"));
    }

    @Test
    void shouldShowCreatePetForm() throws Exception {
        Shelter shelter = new Shelter(
                1L,
                "Abrigo Central",
                new PhoneNumber("31999999999"),
                new Email("abrigo@email.com")
        );

        when(shelterGateway.findById(1L)).thenReturn(Optional.of(shelter));

        mockMvc.perform(get("/shelters/1/pets/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/create"))
                .andExpect(model().attributeExists("shelter"))
                .andExpect(model().attributeExists("petForm"));
    }

    @Test
    void shouldCreatePetSuccessfully() throws Exception {
        Shelter shelter = new Shelter(
                1L,
                "Abrigo Central",
                new PhoneNumber("31999999999"),
                new Email("abrigo@email.com")
        );

        when(shelterGateway.findById(1L)).thenReturn(Optional.of(shelter));

        mockMvc.perform(post("/shelters/1/pets")
                        .param("type", "CACHORRO")
                        .param("name", "Rex")
                        .param("breed", "Vira-lata")
                        .param("age", "2")
                        .param("color", "Caramelo")
                        .param("weight", "10.5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shelters/1/pets"))
                .andExpect(flash().attribute("successMessage", "Pet cadastrado com sucesso."));

        verify(registerPetCommandHandler).execute(
                eq("1"),
                eq("CACHORRO"),
                eq("Rex"),
                eq("Vira-lata"),
                eq(2),
                eq("Caramelo"),
                eq(10.5)
        );
    }

    @Test
    void shouldReturnCreatePetFormWhenValidationFails() throws Exception {
        Shelter shelter = new Shelter(
                1L,
                "Abrigo Central",
                new PhoneNumber("31999999999"),
                new Email("abrigo@email.com")
        );

        when(shelterGateway.findById(1L)).thenReturn(Optional.of(shelter));

        mockMvc.perform(post("/shelters/1/pets")
                        .param("type", "")
                        .param("name", "")
                        .param("breed", "")
                        .param("age", "-1")
                        .param("color", "")
                        .param("weight", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/create"))
                .andExpect(model().attributeExists("shelter"))
                .andExpect(model().attributeHasFieldErrors(
                        "petForm",
                        "type",
                        "name",
                        "breed",
                        "age",
                        "color",
                        "weight"
                ));
    }

    @Test
    void shouldShowEditPetForm() throws Exception {
        Pet pet = new Pet(
                1L,
                PetType.CACHORRO,
                new PetName("Rex"),
                "Vira-lata",
                new AgeYears(2),
                "Caramelo",
                new WeightKg(10.5),
                PetStatus.AVAILABLE
        );

        when(petGateway.findById(1L)).thenReturn(Optional.of(pet));
        when(petGateway.findShelterIdByPetId(1L)).thenReturn(Optional.of(10L));

        mockMvc.perform(get("/pets/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/edit"))
                .andExpect(model().attributeExists("petId"))
                .andExpect(model().attributeExists("shelterId"))
                .andExpect(model().attributeExists("petForm"))
                .andExpect(model().attribute("petId", 1L))
                .andExpect(model().attribute("shelterId", 10L));
    }

    @Test
    void shouldUpdatePetSuccessfully() throws Exception {
        Pet pet = new Pet(
                1L,
                PetType.CACHORRO,
                new PetName("Rex"),
                "Vira-lata",
                new AgeYears(2),
                "Caramelo",
                new WeightKg(10.5),
                PetStatus.AVAILABLE
        );

        when(petGateway.findById(1L)).thenReturn(Optional.of(pet));
        when(petGateway.findShelterIdByPetId(1L)).thenReturn(Optional.of(10L));

        mockMvc.perform(post("/pets/1")
                        .param("type", "GATO")
                        .param("name", "Mimi")
                        .param("breed", "Siamês")
                        .param("age", "3")
                        .param("color", "Branco")
                        .param("weight", "4.2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shelters/10/pets"))
                .andExpect(flash().attribute("successMessage", "Pet atualizado com sucesso."));

        verify(updatePetCommandHandler).execute(
                eq(1L),
                eq("GATO"),
                eq("Mimi"),
                eq("Siamês"),
                eq(3),
                eq("Branco"),
                eq(4.2)
        );
    }

    @Test
    void shouldReturnEditPetFormWhenUpdateValidationFails() throws Exception {
        Pet pet = new Pet(
                1L,
                PetType.CACHORRO,
                new PetName("Rex"),
                "Vira-lata",
                new AgeYears(2),
                "Caramelo",
                new WeightKg(10.5),
                PetStatus.AVAILABLE
        );

        when(petGateway.findById(1L)).thenReturn(Optional.of(pet));
        when(petGateway.findShelterIdByPetId(1L)).thenReturn(Optional.of(10L));

        mockMvc.perform(post("/pets/1")
                        .param("type", "")
                        .param("name", "")
                        .param("breed", "")
                        .param("age", "-1")
                        .param("color", "")
                        .param("weight", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/edit"))
                .andExpect(model().attribute("petId", 1L))
                .andExpect(model().attribute("shelterId", 10L))
                .andExpect(model().attributeHasFieldErrors(
                        "petForm",
                        "type",
                        "name",
                        "breed",
                        "age",
                        "color",
                        "weight"
                ));
    }

    @Test
    void shouldDeletePetSuccessfully() throws Exception {
        Pet pet = new Pet(
                1L,
                PetType.CACHORRO,
                new PetName("Rex"),
                "Vira-lata",
                new AgeYears(2),
                "Caramelo",
                new WeightKg(10.5),
                PetStatus.AVAILABLE
        );

        when(petGateway.findById(1L)).thenReturn(Optional.of(pet));
        when(petGateway.findShelterIdByPetId(1L)).thenReturn(Optional.of(10L));
        doNothing().when(deletePetCommandHandler).execute(1L);

        mockMvc.perform(post("/pets/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shelters/10/pets"))
                .andExpect(flash().attribute("successMessage", "Pet excluído com sucesso."));

        verify(deletePetCommandHandler).execute(1L);
    }

    @Test
    void shouldImportPetsSuccessfully() throws Exception {
        Shelter shelter = new Shelter(
                1L,
                "Abrigo Central",
                new PhoneNumber("31999999999"),
                new Email("abrigo@email.com")
        );

        when(shelterGateway.findById(1L)).thenReturn(Optional.of(shelter));
        when(importShelterPetsCommandHandler.execute("1", "/tmp/pets.csv")).thenReturn(2);

        mockMvc.perform(post("/shelters/1/pets/import")
                        .param("csvFileName", "/tmp/pets.csv"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shelters/1/pets"))
                .andExpect(flash().attribute("successMessage", "2 pet(s) importado(s) com sucesso."));

        verify(importShelterPetsCommandHandler).execute("1", "/tmp/pets.csv");
    }

    @Test
    void shouldReturnPetListWhenImportValidationFails() throws Exception {
        Shelter shelter = new Shelter(
                1L,
                "Abrigo Central",
                new PhoneNumber("31999999999"),
                new Email("abrigo@email.com")
        );

        List<Pet> pets = List.of();

        when(shelterGateway.findById(1L)).thenReturn(Optional.of(shelter));
        when(listShelterPetsQuery.execute("1")).thenReturn(pets);

        mockMvc.perform(post("/shelters/1/pets/import")
                        .param("csvFileName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/list"))
                .andExpect(model().attributeExists("shelter"))
                .andExpect(model().attributeExists("pets"))
                .andExpect(model().attributeExists("petExportForm"))
                .andExpect(model().attributeHasFieldErrors("petImportForm", "csvFileName"));
    }

    @Test
    void shouldExportPetsSuccessfully() throws Exception {
        Shelter shelter = new Shelter(
                1L,
                "Abrigo Central",
                new PhoneNumber("31999999999"),
                new Email("abrigo@email.com")
        );

        when(shelterGateway.findById(1L)).thenReturn(Optional.of(shelter));
        when(exportShelterPetsCommandHandler.execute("1", "/tmp/export.csv")).thenReturn(2);

        mockMvc.perform(post("/shelters/1/pets/export")
                        .param("csvFileName", "/tmp/export.csv"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shelters/1/pets"))
                .andExpect(flash().attribute("successMessage", "2 pet(s) exportado(s) com sucesso."));

        verify(exportShelterPetsCommandHandler).execute("1", "/tmp/export.csv");
    }

    @Test
    void shouldReturnPetListWhenExportValidationFails() throws Exception {
        Shelter shelter = new Shelter(
                1L,
                "Abrigo Central",
                new PhoneNumber("31999999999"),
                new Email("abrigo@email.com")
        );

        List<Pet> pets = List.of();

        when(shelterGateway.findById(1L)).thenReturn(Optional.of(shelter));
        when(listShelterPetsQuery.execute("1")).thenReturn(pets);

        mockMvc.perform(post("/shelters/1/pets/export")
                        .param("csvFileName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("pets/list"))
                .andExpect(model().attributeExists("shelter"))
                .andExpect(model().attributeExists("pets"))
                .andExpect(model().attributeExists("petImportForm"))
                .andExpect(model().attributeHasFieldErrors("petExportForm", "csvFileName"));
    }

    @Test
    void shouldRenderErrorViewWhenListPetsShelterNotFound() throws Exception {
        when(shelterGateway.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/shelters/1/pets"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void shouldRenderErrorViewWhenDeletePetThrowsBusinessException() throws Exception {
        Pet pet = new Pet(
                1L,
                PetType.CACHORRO,
                new PetName("Rex"),
                "Vira-lata",
                new AgeYears(2),
                "Caramelo",
                new WeightKg(10.5),
                PetStatus.AVAILABLE
        );

        when(petGateway.findById(1L)).thenReturn(Optional.of(pet));
        when(petGateway.findShelterIdByPetId(1L)).thenReturn(Optional.of(10L));
        doThrow(new EntityNotFoundException("Pet não encontrado (id=1)."))
                .when(deletePetCommandHandler).execute(1L);

        mockMvc.perform(post("/pets/1/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}