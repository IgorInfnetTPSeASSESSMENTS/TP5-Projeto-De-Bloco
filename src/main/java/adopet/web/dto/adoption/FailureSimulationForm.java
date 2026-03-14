package adopet.web.dto.adoption;

import jakarta.validation.constraints.NotBlank;

public class FailureSimulationForm {

    @NotBlank(message = "O modo de simulação é obrigatório.")
    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}