package com.mdv.corefinance.beans;


import com.mdv.corefinance.engine.LoanType;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.lang.reflect.Array;
import java.util.ArrayList;


@Document(collection = "product")
public class Product {

    @Id
    public ObjectId id;

    @Field("type")
    public String type;

    @Field("fee")
    public Double fee;

    @Field("name")
    public String name;

    @Field("shortDescription")
    public String shortDescription;

    @Field("latePaymentFee")
    public String latePaymentFee;

    @Field("period")
    public ArrayList<String> period;


    public Product(){

    }


}
