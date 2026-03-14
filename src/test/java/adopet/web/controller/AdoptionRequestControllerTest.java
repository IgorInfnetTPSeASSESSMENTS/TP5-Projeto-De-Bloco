package adopet.web.controller;

import adopet.application.adoption.AdoptionRequestOperationResult;
import adopet.application.adoption.AnalysisExecutionStatus;
import adopet.application.adoption.ApproveAdoptionRequestCommandHandler;
import adopet.application.adoption.CancelAdoptionRequestCommandHandler;
import adopet.application.adoption.CreateAdoptionRequestCommandHandler;
import adopet.application.adoption.DeleteAdoptionRequestCommandHandler;
import adopet.application.adoption.GetAdoptionRequestDetailsQuery;
import adopet.application.adoption.ListAdoptionRequestsQuery;
import adopet.application.adoption.RejectAdoptionRequestCommandHandler;
import adopet.application.adoption.RetryEligibilityAnalysisCommandHandler;
import adopet.application.adoption.UpdateAdoptionRequestCommandHandler;
import adopet.domain.adoption.AdoptionRequest;
import adopet.domain.adoption.AdoptionRequestStatus;
import adopet.domain.adoption.ApplicantDocument;
import adopet.domain.adoption.ApplicantEmail;
import adopet.domain.adoption.ApplicantName;
import adopet.domain.adoption.ApplicantPhone;
import adopet.domain.adoption.EligibilityAnalysis;
import adopet.domain.adoption.HousingType;
import adopet.domain.adoption.ReasonText;
import adopet.domain.shelterandpet.AgeYears;
import adopet.domain.shelterandpet.Pet;
import adopet.domain.shelterandpet.PetName;
import adopet.domain.shelterandpet.PetStatus;
import adopet.domain.shelterandpet.PetType;
import adopet.domain.shelterandpet.WeightKg;
import adopet.gateway.AdoptionRequestGateway;
import adopet.gateway.PetGateway;
import adopet.infrastructure.memory.ProgrammableEligibilityAnalysisGateway;
import adopet.infrastructure.memory.ProgrammableNotificationGateway;
import adopet.infrastructure.memory.ScenarioMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdoptionRequestControllerTest {

    private AdoptionRequestGateway adoptionRequestGateway;
    private PetGateway petGateway;
    private ListAdoptionRequestsQuery listAdoptionRequestsQuery;
    private GetAdoptionRequestDetailsQuery getAdoptionRequestDetailsQuery;
    private CreateAdoptionRequestCommandHandler createAdoptionRequestCommandHandler;
    private UpdateAdoptionRequestCommandHandler updateAdoptionRequestCommandHandler;
    private ApproveAdoptionRequestCommandHandler approveAdoptionRequestCommandHandler;
    private RejectAdoptionRequestCommandHandler rejectAdoptionRequestCommandHandler;
    private CancelAdoptionRequestCommandHandler cancelAdoptionRequestCommandHandler;
    private RetryEligibilityAnalysisCommandHandler retryEligibilityAnalysisCommandHandler;
    private ProgrammableEligibilityAnalysisGateway programmableEligibilityAnalysisGateway;
    private ProgrammableNotificationGateway programmableNotificationGateway;
    private DeleteAdoptionRequestCommandHandler deleteAdoptionRequestCommandHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        adoptionRequestGateway = mock(AdoptionRequestGateway.class);
        petGateway = mock(PetGateway.class);
        listAdoptionRequestsQuery = mock(ListAdoptionRequestsQuery.class);
        getAdoptionRequestDetailsQuery = mock(GetAdoptionRequestDetailsQuery.class);
        createAdoptionRequestCommandHandler = mock(CreateAdoptionRequestCommandHandler.class);
        updateAdoptionRequestCommandHandler = mock(UpdateAdoptionRequestCommandHandler.class);
        approveAdoptionRequestCommandHandler = mock(ApproveAdoptionRequestCommandHandler.class);
        rejectAdoptionRequestCommandHandler = mock(RejectAdoptionRequestCommandHandler.class);
        cancelAdoptionRequestCommandHandler = mock(CancelAdoptionRequestCommandHandler.class);
        retryEligibilityAnalysisCommandHandler = mock(RetryEligibilityAnalysisCommandHandler.class);
        deleteAdoptionRequestCommandHandler = mock(DeleteAdoptionRequestCommandHandler.class);
        programmableEligibilityAnalysisGateway = new ProgrammableEligibilityAnalysisGateway();
        programmableNotificationGateway = new ProgrammableNotificationGateway();

        when(petGateway.findById(1L)).thenReturn(Optional.of(samplePet()));
        when(petGateway.findShelterIdByPetId(1L)).thenReturn(Optional.of(10L));

        AdoptionRequestController controller = new AdoptionRequestController(
                adoptionRequestGateway,
                petGateway,
                listAdoptionRequestsQuery,
                getAdoptionRequestDetailsQuery,
                createAdoptionRequestCommandHandler,
                updateAdoptionRequestCommandHandler,
                approveAdoptionRequestCommandHandler,
                rejectAdoptionRequestCommandHandler,
                cancelAdoptionRequestCommandHandler,
                retryEligibilityAnalysisCommandHandler,
                programmableEligibilityAnalysisGateway,
                programmableNotificationGateway,
                deleteAdoptionRequestCommandHandler
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldListRequests() throws Exception {
        when(listAdoptionRequestsQuery.execute(null, 1L, 10L)).thenReturn(List.of());

        mockMvc.perform(get("/pets/1/adoption-requests"))
                .andExpect(status().isOk())
                .andExpect(view().name("adoption-requests/list"))
                .andExpect(model().attributeExists("requests"))
                .andExpect(model().attributeExists("analysisMode"))
                .andExpect(model().attributeExists("notificationMode"))
                .andExpect(model().attribute("contextLocked", true))
                .andExpect(model().attribute("contextPetId", 1L))
                .andExpect(model().attribute("contextShelterId", 10L));
    }

    @Test
    void shouldShowCreateForm() throws Exception {
        mockMvc.perform(get("/pets/1/adoption-requests/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("adoption-requests/create"))
                .andExpect(model().attributeExists("adoptionRequestForm"))
                .andExpect(model().attribute("contextLocked", true))
                .andExpect(model().attribute("contextPetId", 1L))
                .andExpect(model().attribute("contextShelterId", 10L));
    }

    @Test
    void shouldReturnCreateViewWhenCreateFormHasErrors() throws Exception {
        mockMvc.perform(post("/adoption-requests")
                        .param("petId", "1")
                        .param("shelterId", "10")
                        .param("applicantName", "")
                        .param("applicantEmail", "email-invalido")
                        .param("applicantPhone", "")
                        .param("applicantDocument", "")
                        .param("housingType", "")
                        .param("reason", "curto"))
                .andExpect(status().isOk())
                .andExpect(view().name("adoption-requests/create"))
                .andExpect(model().attribute("contextLocked", true))
                .andExpect(model().attribute("contextPetId", 1L))
                .andExpect(model().attribute("contextShelterId", 10L));
    }

    @Test
    void shouldCreateRequestWithEligibleAnalysisMessageAndNotificationSent() throws Exception {
        AdoptionRequest created = sampleRequest();
        AdoptionRequestOperationResult result = new AdoptionRequestOperationResult(
                created,
                true,
                AnalysisExecutionStatus.SUCCESS,
                EligibilityAnalysis.ELIGIBLE
        );

        when(createAdoptionRequestCommandHandler.execute(
                anyLong(), anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString()
        )).thenReturn(result);

        mockMvc.perform(validCreatePost())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("ELIGIBLE")))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("Notificação enviada ao solicitante.")));
    }

    @Test
    void shouldCreateRequestWithManualReviewMessage() throws Exception {
        AdoptionRequestOperationResult result = new AdoptionRequestOperationResult(
                sampleRequest(),
                true,
                AnalysisExecutionStatus.SUCCESS,
                EligibilityAnalysis.REQUIRES_MANUAL_REVIEW
        );

        when(createAdoptionRequestCommandHandler.execute(
                anyLong(), anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString()
        )).thenReturn(result);

        mockMvc.perform(validCreatePost())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("REQUIRES_MANUAL_REVIEW")));
    }

    @Test
    void shouldCreateRequestWithNotEligibleMessage() throws Exception {
        AdoptionRequestOperationResult result = new AdoptionRequestOperationResult(
                sampleRequest(),
                true,
                AnalysisExecutionStatus.SUCCESS,
                EligibilityAnalysis.NOT_ELIGIBLE
        );

        when(createAdoptionRequestCommandHandler.execute(
                anyLong(), anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString()
        )).thenReturn(result);

        mockMvc.perform(validCreatePost())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("NOT_ELIGIBLE")));
    }

    @Test
    void shouldCreateRequestWithNotRequestedMessage() throws Exception {
        AdoptionRequestOperationResult result = new AdoptionRequestOperationResult(
                sampleRequest(),
                true,
                AnalysisExecutionStatus.SUCCESS,
                EligibilityAnalysis.NOT_REQUESTED
        );

        when(createAdoptionRequestCommandHandler.execute(
                anyLong(), anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString()
        )).thenReturn(result);

        mockMvc.perform(validCreatePost())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("Análise automática não solicitada.")));
    }

    @Test
    void shouldCreateRequestWithUnavailableMessage() throws Exception {
        AdoptionRequestOperationResult result = new AdoptionRequestOperationResult(
                sampleRequest(),
                true,
                AnalysisExecutionStatus.SUCCESS,
                EligibilityAnalysis.UNAVAILABLE
        );

        when(createAdoptionRequestCommandHandler.execute(
                anyLong(), anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString()
        )).thenReturn(result);

        mockMvc.perform(validCreatePost())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("UNAVAILABLE")));
    }

    @Test
    void shouldCreateRequestWhenAnalysisFailsAndNotificationFails() throws Exception {
        AdoptionRequestOperationResult result = new AdoptionRequestOperationResult(
                sampleRequest(),
                false,
                AnalysisExecutionStatus.FAILED,
                EligibilityAnalysis.UNAVAILABLE
        );

        when(createAdoptionRequestCommandHandler.execute(
                anyLong(), anyLong(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString()
        )).thenReturn(result);

        mockMvc.perform(validCreatePost())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("A análise automática falhou")))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("A notificação não pôde ser enviada no momento.")));
    }

    @Test
    void shouldShowDetails() throws Exception {
        when(getAdoptionRequestDetailsQuery.execute(1L)).thenReturn(sampleRequest());

        mockMvc.perform(get("/adoption-requests/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("adoption-requests/details"))
                .andExpect(model().attributeExists("request"));
    }

    @Test
    void shouldShowEditForm() throws Exception {
        when(getAdoptionRequestDetailsQuery.execute(1L)).thenReturn(sampleRequest());

        mockMvc.perform(get("/adoption-requests/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("adoption-requests/edit"))
                .andExpect(model().attributeExists("requestId"))
                .andExpect(model().attributeExists("adoptionRequestForm"));
    }

    @Test
    void shouldReturnEditViewWhenUpdateFormHasErrors() throws Exception {
        when(getAdoptionRequestDetailsQuery.execute(1L)).thenReturn(sampleRequest());

        mockMvc.perform(post("/adoption-requests/1")
                        .param("petId", "1")
                        .param("shelterId", "10")
                        .param("applicantName", "")
                        .param("applicantEmail", "email-invalido")
                        .param("applicantPhone", "")
                        .param("applicantDocument", "")
                        .param("housingType", "")
                        .param("reason", "curto"))
                .andExpect(status().isOk())
                .andExpect(view().name("adoption-requests/edit"))
                .andExpect(model().attribute("requestId", 1L));
    }

    @Test
    void shouldUpdateRequest() throws Exception {
        when(getAdoptionRequestDetailsQuery.execute(1L)).thenReturn(sampleRequest());
        when(updateAdoptionRequestCommandHandler.execute(
                anyLong(), anyLong(), anyLong(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean(), anyString()
        )).thenReturn(sampleRequest());

        mockMvc.perform(post("/adoption-requests/1")
                        .param("petId", "1")
                        .param("shelterId", "10")
                        .param("applicantName", "Maria da Silva")
                        .param("applicantEmail", "maria@email.com")
                        .param("applicantPhone", "31999999999")
                        .param("applicantDocument", "12345678900")
                        .param("housingType", "HOUSE")
                        .param("hasOtherPets", "true")
                        .param("reason", "Quero adotar com responsabilidade e carinho."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage", "Solicitação atualizada com sucesso."));
    }

    @Test
    void shouldApproveWithNotificationSent() throws Exception {
        when(approveAdoptionRequestCommandHandler.execute(1L)).thenReturn(
                new AdoptionRequestOperationResult(sampleRequest(), true, AnalysisExecutionStatus.SUCCESS, EligibilityAnalysis.ELIGIBLE)
        );

        mockMvc.perform(post("/adoption-requests/1/approve"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("Notificação enviada ao solicitante.")));
    }

    @Test
    void shouldApproveWithNotificationFailure() throws Exception {
        when(approveAdoptionRequestCommandHandler.execute(1L)).thenReturn(
                new AdoptionRequestOperationResult(sampleRequest(), false, AnalysisExecutionStatus.SUCCESS, EligibilityAnalysis.ELIGIBLE)
        );

        mockMvc.perform(post("/adoption-requests/1/approve"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("A notificação não pôde ser enviada no momento.")));
    }

    @Test
    void shouldRejectWithNotificationSent() throws Exception {
        when(rejectAdoptionRequestCommandHandler.execute(1L)).thenReturn(
                new AdoptionRequestOperationResult(sampleRequest(), true, AnalysisExecutionStatus.SUCCESS, EligibilityAnalysis.ELIGIBLE)
        );

        mockMvc.perform(post("/adoption-requests/1/reject"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("Notificação enviada ao solicitante.")));
    }

    @Test
    void shouldRejectWithNotificationFailure() throws Exception {
        when(rejectAdoptionRequestCommandHandler.execute(1L)).thenReturn(
                new AdoptionRequestOperationResult(sampleRequest(), false, AnalysisExecutionStatus.SUCCESS, EligibilityAnalysis.ELIGIBLE)
        );

        mockMvc.perform(post("/adoption-requests/1/reject"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("A notificação não pôde ser enviada no momento.")));
    }

    @Test
    void shouldCancelWithNotificationSent() throws Exception {
        when(cancelAdoptionRequestCommandHandler.execute(1L)).thenReturn(
                new AdoptionRequestOperationResult(sampleRequest(), true, AnalysisExecutionStatus.SUCCESS, EligibilityAnalysis.ELIGIBLE)
        );

        mockMvc.perform(post("/adoption-requests/1/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("Notificação enviada ao solicitante.")));
    }

    @Test
    void shouldCancelWithNotificationFailure() throws Exception {
        when(cancelAdoptionRequestCommandHandler.execute(1L)).thenReturn(
                new AdoptionRequestOperationResult(sampleRequest(), false, AnalysisExecutionStatus.SUCCESS, EligibilityAnalysis.ELIGIBLE)
        );

        mockMvc.perform(post("/adoption-requests/1/cancel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage",
                        org.hamcrest.Matchers.containsString("A notificação não pôde ser enviada no momento.")));
    }

    @Test
    void shouldRetryAnalysis() throws Exception {
        when(retryEligibilityAnalysisCommandHandler.execute(1L)).thenReturn(sampleRequest());

        mockMvc.perform(post("/adoption-requests/1/retry-analysis"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/adoption-requests/1"))
                .andExpect(flash().attribute("successMessage", "Nova análise executada com sucesso. O pet voltou para disponível."));
    }

    @Test
    void shouldSetEligibilitySimulation() throws Exception {
        mockMvc.perform(post("/pets/1/adoption-requests/simulation/eligibility/TIMEOUT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage", "Simulação da análise atualizada."));

        org.junit.jupiter.api.Assertions.assertEquals(
                ScenarioMode.TIMEOUT,
                programmableEligibilityAnalysisGateway.getScenarioMode()
        );
    }

    @Test
    void shouldSetNotificationSimulation() throws Exception {
        mockMvc.perform(post("/pets/1/adoption-requests/simulation/notification/NETWORK_ERROR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute("successMessage", "Simulação da notificação atualizada."));

        org.junit.jupiter.api.Assertions.assertEquals(
                ScenarioMode.NETWORK_ERROR,
                programmableNotificationGateway.getScenarioMode()
        );
    }

    @Test
    void shouldDeleteRequest() throws Exception {
        when(getAdoptionRequestDetailsQuery.execute(1L)).thenReturn(sampleRequest());
        doNothing().when(deleteAdoptionRequestCommandHandler).execute(1L);

        mockMvc.perform(post("/adoption-requests/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/pets/1/adoption-requests"))
                .andExpect(flash().attribute(
                        "successMessage",
                        "Solicitação removida com sucesso."
                ));

        verify(deleteAdoptionRequestCommandHandler).execute(1L);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder validCreatePost() {
        return post("/adoption-requests")
                .param("petId", "1")
                .param("shelterId", "10")
                .param("applicantName", "Maria da Silva")
                .param("applicantEmail", "maria@email.com")
                .param("applicantPhone", "31999999999")
                .param("applicantDocument", "12345678900")
                .param("housingType", "HOUSE")
                .param("hasOtherPets", "true")
                .param("reason", "Quero adotar com responsabilidade e carinho.");
    }

    private AdoptionRequest sampleRequest() {
        LocalDateTime now = LocalDateTime.now();
        return new AdoptionRequest(
                1L,
                1L,
                10L,
                new ApplicantName("Maria da Silva"),
                new ApplicantEmail("maria@email.com"),
                new ApplicantPhone("31999999999"),
                new ApplicantDocument("12345678900"),
                HousingType.HOUSE,
                true,
                new ReasonText("Quero adotar com responsabilidade e carinho."),
                AdoptionRequestStatus.PENDING,
                EligibilityAnalysis.REQUIRES_MANUAL_REVIEW,
                now,
                now
        );
    }

    private Pet samplePet() {
        return new Pet(
                1L,
                PetType.CACHORRO,
                new PetName("Rex"),
                "Vira-lata",
                new AgeYears(2),
                "Caramelo",
                new WeightKg(10.5),
                PetStatus.AVAILABLE
        );
    }
}