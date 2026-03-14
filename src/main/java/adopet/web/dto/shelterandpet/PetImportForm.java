package adopet.web.dto.shelterandpet;

import jakarta.validation.constraints.NotBlank;

public class PetImportForm {

    @NotBlank(message = "O caminho do arquivo CSV é obrigatório.")
    private String csvFileName;

    public PetImportForm() {
    }

    public PetImportForm(String csvFileName) {
        this.csvFileName = csvFileName;
    }

    public String getCsvFileName() {
        return csvFileName;
    }

    public void setCsvFileName(String csvFileName) {
        this.csvFileName = csvFileName;
    }
}