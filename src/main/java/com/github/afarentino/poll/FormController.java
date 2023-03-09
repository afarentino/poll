package com.github.afarentino.poll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class FormController {
    private static final Logger logger = LoggerFactory.getLogger(FormController.class);

    private AnswersRepository storage;
    @Autowired
    public FormController(AnswersRepository storage) { this.storage = storage; }

    @GetMapping("/survey")
    public String showForm(Model model) {
        model.addAttribute("questions", new Questions());
        return "survey";
    }

    @PostMapping("/survey")
    public String answersSubmit(@ModelAttribute Questions answers, Model model) {

        storage.save(answers);

        model.addAttribute("user", answers.getFirstName());
        model.addAttribute("questions", answers);

        return "thanks";
    }

    @GetMapping(value= "/survey/results")
    @ResponseBody
    public ResponseEntity<Resource> csvDownload() throws IOException {
        Resource file = storage.answersCsv();
        return ResponseEntity.ok().
                header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=" + file.getFilename()).
                contentType(MediaType.parseMediaType("text/csv")).
                contentLength(file.contentLength()).
                body(file);
    }
}
