package com.mdv.corefinance.repos;


import com.mdv.corefinance.beans.Product;
import com.mongodb.internal.connection.ConcurrentPool;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, ObjectId>{

    @Override
    List<Product> findAll();

    Product findProductById(ObjectId pid);

    Product findProductByName(String name);

    Product findProductByType(String type);







}
