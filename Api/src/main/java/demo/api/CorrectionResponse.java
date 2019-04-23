package demo.api;

public class CorrectionResponse {

    private String output;
    private String standard;

    public CorrectionResponse() {
        output = null;
        standard = null;
    }

    public CorrectionResponse(String output, String standard) {
        this.output = output;
        this.standard = standard;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getOutput() {
        return output;
    }

    public String getStandard() {
        return standard;
    }
}
