package com.cc;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;

@Service
public class Repo {

    // 索引名称 (可以理解成数据库的表名)
    private static final String index_name = "config";

    // es对象
    @Autowired
    private RestHighLevelClient client;

    public int insert() {
        int count = 0;
        try {
            // 从数据库拿到数据
            List<Config> list = Config.getAll();
            for(Config u : list) {
                addIndex(u, String.valueOf(u.getId()));
                count += 1;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return count;
    }
    /**
     * 添加索引
     */
    public void addIndex(Config config, String id) throws IOException{
        // 创建一个索引，指定文档id，source是文档内容
        IndexRequest request = new IndexRequest(index_name).id(id).source(beanToMap(config));
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }
    /**
     * 查询
     */
    public Object find(String id) throws IOException{
        GetRequest getRequest = new GetRequest(index_name,id);
        GetResponse response = client.get(getRequest,RequestOptions.DEFAULT);

        Map<String, Object> source = response.getSource();
        System.out.println(response);
        return JSONObject.toJSON(source);
    }
    /**
     * 删除
     */
    public boolean delete(String id) throws IOException{
        DeleteRequest request = new DeleteRequest(index_name,id);
        DeleteResponse res = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(res);
        return true;
    }
    /**
     * 改
     */
    public boolean edit(Config config,String id) throws IOException{
        UpdateRequest request = new UpdateRequest(index_name,id).doc(beanToMap(config));
        UpdateResponse name = client.update(request,RequestOptions.DEFAULT);
        System.out.println(name);
        return true;
    }
    /**
     * 条件搜索
     */
    public List<Config> select(String key,String value,int type) throws IOException {
        // 指定索引，类似于数据库的表
        SearchRequest searchRequest = new SearchRequest(index_name);
        // 创建查询对象，相当于写查询sql
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if(type==1) {
            // matchQuery是模糊查询，会对key进行分词
            searchSourceBuilder.query(QueryBuilders.matchQuery(key,value));
        }else if(type==2) {
            // termQuery是精准查询
            searchSourceBuilder.query(QueryBuilders.termQuery(key,value));
        }

        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest,RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSON(response));

        SearchHit[] hits = response.getHits().getHits();
        List<Config> list = new LinkedList<>();
        for(SearchHit hit: hits){
            Config config = JSONObject.parseObject(hit.getSourceAsString(),Config.class);
            list.add(config);
        }
        return list;
    }

    /**
     * 对象转map
     */
    public <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                if(beanMap.get(key) != null){
                    map.put(key + "", beanMap.get(key));
                }
            }
        }
        return map;
    }

}