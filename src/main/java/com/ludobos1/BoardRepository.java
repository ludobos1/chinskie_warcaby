package com.ludobos1;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends MongoRepository<Board, String> {
}
