package adopet.web.dto.shelterandpet;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ShelterForm {

    @NotBlank(message = "O nome do abrigo é obrigatório.")
    private String name;

    @NotBlank(message = "O telefone é obrigatório.")
    private String phoneNumber;

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "O email informado é inválido.")
    private String email;

    public ShelterForm() {
    }

    public ShelterForm(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}