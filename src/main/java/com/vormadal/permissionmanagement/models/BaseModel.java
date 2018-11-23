package com.vormadal.permissionmanagement.models;

import lombok.Data;

import java.util.UUID;

/**
 * Created: 20-04-2018
 * Owner: Runi
 */

@Data
public abstract class BaseModel {

    private String id;

    public BaseModel(){
        this(UUID.randomUUID().toString());
    }

    protected BaseModel(String id){
        this.id = (id == null ? UUID.randomUUID().toString() : id);
    }

}
