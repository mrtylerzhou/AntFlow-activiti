package org.openoa.base.interf;

import org.openoa.base.vo.EsQueryParam;
import org.openoa.base.vo.EsQueryResult;

/**
 * Elasticsearch client adaptor interface.
 * Abstracts ES operations so implementations can be swapped (e.g. RestHighLevelClient, new Java client).
 * Users can provide their own implementation as a Spring bean.
 *
 * @Author tylerzhou
 */
public interface EsClientAdaptor {

    /**
     * Query data from ES index.
     *
     * @param param     query parameters
     * @param indexName index name
     * @return query result with total count and JSON list
     */
    EsQueryResult queryData(EsQueryParam param, String indexName);

    /**
     * Save (index) a document. Creates or replaces.
     *
     * @param indexName index name
     * @param id        document id
     * @param json      document JSON
     * @return true if successful
     */
    boolean saveData(String indexName, String id, String json);

    /**
     * Update a document (partial update, upsert if not exists).
     *
     * @param indexName index name
     * @param id        document id
     * @param json      document JSON
     * @return true if successful
     */
    boolean updateData(String indexName, String id, String json);

    /**
     * Delete a document by id.
     *
     * @param indexName index name
     * @param id        document id
     * @return true if successful
     */
    boolean deleteData(String indexName, String id);

    /**
     * Get document source as JSON string by id.
     *
     * @param indexName index name
     * @param id        document id
     * @return JSON string, or empty string if not found
     */
    String getDataById(String indexName, String id);

    /**
     * Check if an index exists.
     *
     * @param indexName index name
     * @return true if exists
     */
    boolean isIndexExists(String indexName);

    /**
     * Create an index with auto-generated mapping from the given class.
     *
     * @param indexName index name
     * @param clazz     entity class for mapping generation
     * @return true if created successfully
     */
    boolean createIndex(String indexName, Class<?> clazz);
}
