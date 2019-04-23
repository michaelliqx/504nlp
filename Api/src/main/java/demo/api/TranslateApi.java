package demo.api;

import demo.checker.SpellCorrect;
import demo.test.NLP_test;
import org.springframework.web.bind.annotation.*;

@RestController
public class TranslateApi {

    @PostMapping("/get/correction")
    @ResponseBody
    @CrossOrigin
    public CorrectionResponse postResponseController(
            @RequestBody CorrectionRequest request
    ) throws Exception {
        String output = SpellCorrect.Corrector_string(request.getText());
        String standard = NLP_test.NLPteststring(request.getText());
        return new CorrectionResponse(output, standard);
    }
}
