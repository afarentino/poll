package com.github.afarentino.datepicker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FormController {
    private static final Logger logger = LoggerFactory.getLogger(FormController.class);

    @Autowired
    public FormController() {}

    @GetMapping("/survey")
    public String showForm(Model model) {
        model.addAttribute("questions", new Questions());
        return "survey";
    }

    @PostMapping("/survey")
    public String answersSubmit(@ModelAttribute Questions answers, Model model) {
        model.addAttribute("questions", answers);
        return "thanks";
    }

}
