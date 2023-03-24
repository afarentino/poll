package com.github.afarentino.poll;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EntryRepository extends MongoRepository<Entry, String> {
    // Additional custom finder methods would go here...
    //long count();
}
