package com.github.afarentino.poll;

import org.springframework.core.io.Resource;

public interface AnswersRepository {
    void save(Questions data);
    Resource answersCsv();
    void reset();
    void init();
}
