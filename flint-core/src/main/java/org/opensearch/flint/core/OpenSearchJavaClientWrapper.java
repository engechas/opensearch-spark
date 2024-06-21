//package org.opensearch.flint.core;
//
//import org.opensearch.action.DocWriteResponse;
//import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
//import org.opensearch.action.bulk.BulkRequest;
//import org.opensearch.action.bulk.BulkResponse;
//import org.opensearch.action.delete.DeleteRequest;
//import org.opensearch.action.delete.DeleteResponse;
//import org.opensearch.action.get.GetRequest;
//import org.opensearch.action.get.GetResponse;
//import org.opensearch.action.index.IndexRequest;
//import org.opensearch.action.index.IndexResponse;
//import org.opensearch.action.search.ClearScrollRequest;
//import org.opensearch.action.search.ClearScrollResponse;
//import org.opensearch.action.search.SearchRequest;
//import org.opensearch.action.search.SearchResponse;
//import org.opensearch.action.search.SearchScrollRequest;
//import org.opensearch.action.update.UpdateRequest;
//import org.opensearch.client.opensearch.OpenSearchClient;
//import org.opensearch.client.RequestOptions;
//import org.opensearch.client.indices.CreateIndexRequest;
//import org.opensearch.client.indices.CreateIndexResponse;
//import org.opensearch.client.indices.GetIndexRequest;
//import org.opensearch.client.indices.GetIndexResponse;
//import org.opensearch.client.indices.PutMappingRequest;
//
//import java.io.IOException;
//
//public class OpenSearchJavaClientWrapper implements IRestHighLevelClient {
//    private final OpenSearchClient client;
//
//    public OpenSearchJavaClientWrapper(OpenSearchClient client) {
//        this.client = client;
//    }
//
//    @Override
//    public BulkResponse bulk(BulkRequest bulkRequest, RequestOptions options) throws IOException {
//        return null;
//    }
//
//    @Override
//    public ClearScrollResponse clearScroll(ClearScrollRequest clearScrollRequest, RequestOptions options) throws IOException {
//        return null;
//    }
//
//    @Override
//    public CreateIndexResponse createIndex(CreateIndexRequest createIndexRequest, RequestOptions options) throws IOException {
//        return null;
////        client.indices().create()
////        final CreateIndexRequest convertedRequest = new org.opensearch.client.opensearch.indices.CreateIndexRequest.Builder()
////                .index()
//    }
//
//    @Override
//    public void updateIndexMapping(PutMappingRequest putMappingRequest, RequestOptions options) throws IOException {
//
//    }
//
//    @Override
//    public void deleteIndex(DeleteIndexRequest deleteIndexRequest, RequestOptions options) throws IOException {
//
//    }
//
//    @Override
//    public DeleteResponse delete(DeleteRequest deleteRequest, RequestOptions options) throws IOException {
//        return null;
//    }
//
//    @Override
//    public GetResponse get(GetRequest getRequest, RequestOptions options) throws IOException {
//        return null;
//    }
//
//    @Override
//    public GetIndexResponse getIndex(GetIndexRequest getIndexRequest, RequestOptions options) throws IOException {
//        return null;
//    }
//
//    @Override
//    public IndexResponse index(IndexRequest indexRequest, RequestOptions options) throws IOException {
//        return null;
//    }
//
//    @Override
//    public Boolean doesIndexExist(GetIndexRequest getIndexRequest, RequestOptions options) throws IOException {
//        return null;
//    }
//
//    @Override
//    public SearchResponse search(SearchRequest searchRequest, RequestOptions options) throws IOException {
//        return null;
//    }
//
//    @Override
//    public SearchResponse scroll(SearchScrollRequest searchScrollRequest, RequestOptions options) throws IOException {
//        return null;
//    }
//
//    @Override
//    public DocWriteResponse update(UpdateRequest updateRequest, RequestOptions options) throws IOException {
//        return null;
//    }
//
//    @Override
//    public void close() throws IOException {
//
//    }
//}
