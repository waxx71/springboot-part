package com.cc;

import org.springframework.data.elasticsearch.annotations.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Document(indexName = "config", shards = 1, replicas = 0)
public class Config {

    private int id;

    private String code;

    private String name;

    public static List<Config> getAll() {
        List<Config> list = new ArrayList<Config>();
        try {
            Config config = new Config(1,
                    " ",
                    "nobody");
                list.add(config);
        } catch (Exception e) {
        }
        return list;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Config(int id, String code, String name) {
        super();
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Config() {
        super();
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", code=" + code + ", name=" + name +  "]";
    }
}