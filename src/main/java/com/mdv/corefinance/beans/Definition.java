package com.mdv.corefinance.beans;


import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Definition {
    public int interest;
    public int durationMonths;

}
