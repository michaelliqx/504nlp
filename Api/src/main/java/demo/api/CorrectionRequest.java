package demo.api;

public class CorrectionRequest {

    private String text;

    public CorrectionRequest() {
        text = null;
    }

    public CorrectionRequest(String text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
