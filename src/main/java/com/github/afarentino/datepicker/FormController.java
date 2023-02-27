package com.github.afarentino.datepicker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class FormController {
    private static final Logger logger = LoggerFactory.getLogger(FormController.class);

    @Autowired
    public FormController() {}

    @GetMapping("/")
    public String showForm(Model model) {

        return "survey";
    }

}
