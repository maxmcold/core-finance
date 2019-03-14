package com.mdv.corefinance.repos;


import com.mdv.corefinance.beans.Definition;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DefinitionRepository extends MongoRepository<Definition, ObjectId> {


    @Override
    List<Definition> findAll();


    List<Definition> findAllByInterest();


}
