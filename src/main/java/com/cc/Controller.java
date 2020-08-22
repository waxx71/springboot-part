package com.cc;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class Controller {

    @Autowired
    private Repo repo;

    @RequestMapping("/insert")
    public int insert() {
        return repo.insert();
    }

    @RequestMapping("/find")
    public Object find(String id) throws IOException{
        return repo.find(id);
    }

    @RequestMapping("/getAll")
    public List<Config> getAll() throws IOException{
        List<Config> list = new ArrayList<>();
        Object json = repo.find(String.valueOf(1));
        list.add(JSONObject.parseObject(json.toString(), Config.class));
        return list;
    }

    @RequestMapping("/del")
    public boolean delete(String id) throws IOException{
        return repo.delete(id);
    }

    @RequestMapping("/edit")
    public boolean edit(Config config,String id) throws IOException{
        return repo.edit(config, id);
    }

    @RequestMapping("/select")
    public List<Config> select(String key,String value,int type) throws IOException {
        return repo.select(key, value, type);
    }

    @RequestMapping("/update")
    public void update() throws IOException
    {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "123456"));  //es账号密码（默认用户名为elastic）
        RestHighLevelClient client = new RestHighLevelClient
                (
                RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")).setHttpClientConfigCallback
                        (new RestClientBuilder.HttpClientConfigCallback()
                {
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder)
                    {
                        httpClientBuilder.disableAuthCaching();
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                })
        );
    }
}