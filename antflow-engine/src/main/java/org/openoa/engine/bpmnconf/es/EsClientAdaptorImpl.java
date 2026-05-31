package org.openoa.engine.bpmnconf.es;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
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
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.openoa.base.interf.EsClientAdaptor;
import org.openoa.base.vo.EsQueryParam;
import org.openoa.base.vo.EsQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Default ES client implementation using RestHighLevelClient (ES 7.x).
 * Users can replace this by providing their own EsClientAdaptor bean.
 *
 * @Author tylerzhou
 */
@Slf4j
@Component
public class EsClientAdaptorImpl implements EsClientAdaptor {

    @Value("${antflow.es.enabled:false}")
    private boolean enabled;

    @Autowired(required = false)
    private RestHighLevelClient restHighLevelClient;

    private boolean isReady() {
        if (!enabled || restHighLevelClient == null) {
            log.debug("ES not enabled or client not configured, skipping operation");
            return false;
        }
        return true;
    }

    @Override
    public EsQueryResult queryData(EsQueryParam param, String indexName) {
        EsQueryResult result = new EsQueryResult();
        if (!isReady()) {
            return result;
        }
        try {
            // build bool query
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // equals conditions
            if (param.getEqualsConditions() != null) {
                for (Map.Entry<String, Object> entry : param.getEqualsConditions().entrySet()) {
                    if (!ObjectUtils.isEmpty(entry.getValue())) {
                        boolQuery.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                    }
                }
            }

            // match phrase conditions
            if (param.getMatchPhraseConditions() != null) {
                for (Map.Entry<String, String> entry : param.getMatchPhraseConditions().entrySet()) {
                    if (StringUtils.hasText(entry.getValue())) {
                        boolQuery.must(QueryBuilders.matchPhraseQuery(entry.getKey(), entry.getValue()));
                    }
                }
            }

            // no-equals conditions (must_not)
            if (param.getNoEqualsConditions() != null) {
                for (Map.Entry<String, Object> entry : param.getNoEqualsConditions().entrySet()) {
                    if (!ObjectUtils.isEmpty(entry.getValue())) {
                        boolQuery.mustNot(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                    }
                }
            }

            // or-like conditions (should with minimumShouldMatch)
            if (param.getOrLikeConditions() != null && !param.getOrLikeConditions().isEmpty()) {
                BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
                for (Map.Entry<String, Object> entry : param.getOrLikeConditions().entrySet()) {
                    if (!ObjectUtils.isEmpty(entry.getValue())) {
                        shouldQuery.should(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
                    }
                }
                if (shouldQuery.hasClauses()) {
                    boolQuery.must(shouldQuery);
                }
            }

            // or-equals conditions (should with term queries)
            if (param.getOrConditions() != null && !param.getOrConditions().isEmpty()) {
                BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
                for (Map.Entry<String, Object> entry : param.getOrConditions().entrySet()) {
                    if (!ObjectUtils.isEmpty(entry.getValue())) {
                        orQuery.should(QueryBuilders.termQuery(entry.getKey(), entry.getValue()));
                    }
                }
                if (orQuery.hasClauses()) {
                    orQuery.minimumShouldMatch(1);
                    boolQuery.must(orQuery);
                }
            }

            // terms conditions
            if (param.getTermsConditions() != null) {
                for (Map.Entry<String, List> entry : param.getTermsConditions().entrySet()) {
                    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                        boolQuery.must(QueryBuilders.termsQuery(entry.getKey(), entry.getValue()));
                    }
                }
            }

            // build source
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(boolQuery);
            sourceBuilder.from((param.getPageNum() - 1) * param.getPageSize());
            sourceBuilder.size(param.getPageSize());
            sourceBuilder.timeout(new TimeValue(30, java.util.concurrent.TimeUnit.SECONDS));

            // sort fields
            if (param.getSortFields() != null) {
                for (Map.Entry<String, String> entry : param.getSortFields().entrySet()) {
                    SortOrder order = "asc".equalsIgnoreCase(entry.getValue()) ? SortOrder.ASC : SortOrder.DESC;
                    if (entry.getKey().contains(".")) {
                        // nested sort
                        EsQueryParam.NestedSortConfig nsConfig = param.getNestedSortConfigs() != null
                                ? param.getNestedSortConfigs().get(entry.getKey()) : null;
                        if (nsConfig != null) {
                            NestedSortBuilder nestedSort = new NestedSortBuilder(nsConfig.getNestedPath());
                            if (nsConfig.getFilterField() != null && nsConfig.getFilterValue() != null) {
                                nestedSort.setFilter(QueryBuilders.termQuery(nsConfig.getFilterField(), nsConfig.getFilterValue()));
                            }
                            sourceBuilder.sort(SortBuilders.fieldSort(entry.getKey()).order(order).setNestedSort(nestedSort));
                        } else {
                            sourceBuilder.sort(entry.getKey(), order);
                        }
                    } else {
                        sourceBuilder.sort(entry.getKey(), order);
                    }
                }
            }

            // count total
            CountRequest countRequest = new CountRequest(indexName);
            countRequest.query(boolQuery);
            CountResponse countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            result.setTotal(countResponse.getCount());

            // search
            SearchRequest searchRequest = new SearchRequest(indexName);
            searchRequest.source(sourceBuilder);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            java.util.ArrayList<String> jsonList = new java.util.ArrayList<>();
            for (SearchHit hit : hits.getHits()) {
                jsonList.add(hit.getSourceAsString());
            }
            result.setJsonResults(jsonList);

            log.info("ES query index[{}], total={}, returned={}", indexName, result.getTotal(), jsonList.size());
        } catch (IOException e) {
            log.error("ES query failed, index[{}]: {}", indexName, e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean saveData(String indexName, String id, String json) {
        if (!isReady()) {
            return false;
        }
        try {
            IndexRequest indexRequest = new IndexRequest(indexName);
            indexRequest.id(id);
            indexRequest.source(json, XContentType.JSON);
            IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            log.info("ES index[{}], id[{}] result: {}", indexName, id, response.getResult());
            return true;
        } catch (IOException e) {
            log.error("ES save failed, index[{}], id[{}]: {}", indexName, id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateData(String indexName, String id, String json) {
        if (!isReady()) {
            return false;
        }
        try {
            UpdateRequest updateRequest = new UpdateRequest(indexName, id);
            updateRequest.docAsUpsert(true);
            updateRequest.doc(json, XContentType.JSON);
            UpdateResponse response = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info("ES update index[{}], id[{}] result: {}", indexName, id, response.getResult());
            return true;
        } catch (IOException e) {
            log.error("ES update failed, index[{}], id[{}]: {}", indexName, id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteData(String indexName, String id) {
        if (!isReady()) {
            return false;
        }
        try {
            DeleteRequest deleteRequest = new DeleteRequest(indexName);
            deleteRequest.id(id);
            DeleteResponse response = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("ES delete index[{}], id[{}] result: {}", indexName, id, response.getResult());
            return response.getResult() != DocWriteResponse.Result.NOT_FOUND;
        } catch (IOException e) {
            log.error("ES delete failed, index[{}], id[{}]: {}", indexName, id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getDataById(String indexName, String id) {
        if (!isReady()) {
            return "";
        }
        try {
            GetRequest getRequest = new GetRequest(indexName, id);
            GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            return response.getSourceAsString();
        } catch (IOException e) {
            log.error("ES get failed, index[{}], id[{}]: {}", indexName, id, e.getMessage(), e);
            return "";
        }
    }

    @Override
    public boolean isIndexExists(String indexName) {
        if (!isReady()) {
            return false;
        }
        try {
            GetIndexRequest request = new GetIndexRequest(indexName);
            return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("ES index exists check failed, index[{}]: {}", indexName, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean createIndex(String indexName, Class<?> clazz) {
        if (!isReady()) {
            return false;
        }
        try {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            CreateIndexResponse response = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("ES create index[{}] result: {}", indexName, response.isAcknowledged());
            return response.isAcknowledged();
        } catch (IOException e) {
            log.error("ES create index failed, index[{}]: {}", indexName, e.getMessage(), e);
            return false;
        }
    }
}
