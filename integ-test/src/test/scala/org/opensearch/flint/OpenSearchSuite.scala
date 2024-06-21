/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.flint

import com.amazonaws.auth.AWS4Signer
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import org.apache.http.HttpHost
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest
import org.opensearch.action.bulk.BulkRequest
import org.opensearch.action.index.IndexRequest
import org.opensearch.action.support.WriteRequest.RefreshPolicy
import org.opensearch.client.{Request, RequestOptions, RestClient, RestHighLevelClient}
import org.opensearch.client.indices.{CreateIndexRequest, GetIndexRequest}
import org.opensearch.common.xcontent.XContentType
import org.opensearch.flint.core.auth.AWSRequestSigningApacheInterceptor
import org.scalatest.{BeforeAndAfterAll, Suite}

import org.apache.spark.sql.flint.config.FlintSparkConf.{AUTH, HOST_ENDPOINT, HOST_PORT, IGNORE_DOC_ID_COLUMN, REFRESH_POLICY, SCHEME, SIGV4_SERVICE}

/**
 * Test required OpenSearch domain should extend OpenSearchSuite.
 */
trait OpenSearchSuite extends BeforeAndAfterAll {
  self: Suite =>

  // protected lazy val container = new OpenSearchContainer()

  protected lazy val openSearchPort: Int = 443

  protected lazy val openSearchHost: String =
    "https://9hagv0jong5e14ltfy6l.us-west-2.aoss.amazonaws.com"

  protected lazy val openSearchClient = new RestHighLevelClient(
    RestClient
      .builder(HttpHost.create(openSearchHost))
      .setHttpClientConfigCallback((builder: HttpAsyncClientBuilder) => {
        val awsCredentialsProvider = new DefaultAWSCredentialsProviderChain
        val awsSigner = new AWS4Signer()
        awsSigner.setRegionName("us-west-2")
        awsSigner.setServiceName("aoss")
        builder.addInterceptorLast(
          new AWSRequestSigningApacheInterceptor("aoss", awsSigner, awsCredentialsProvider))
      }))

  protected lazy val metadataOpenSearchClient = new RestHighLevelClient(
    RestClient
      .builder(HttpHost.create(
        "https://search-big-domain-x5eqbzoy735ktw4p7piiblzqei.us-west-2.es.amazonaws.com"))
      .setHttpClientConfigCallback((builder: HttpAsyncClientBuilder) => {
        val awsCredentialsProvider = new DefaultAWSCredentialsProviderChain
        val awsSigner = new AWS4Signer()
        awsSigner.setRegionName("us-west-2")
        awsSigner.setServiceName("es")
        builder.addInterceptorLast(
          new AWSRequestSigningApacheInterceptor("es", awsSigner, awsCredentialsProvider))
      }))

  protected lazy val openSearchOptions =
    Map(
      s"${HOST_ENDPOINT.optionKey}" -> openSearchHost,
      s"${HOST_PORT.optionKey}" -> s"$openSearchPort",
      s"${SCHEME.optionKey}" -> "https",
      s"${AUTH.optionKey}" -> "sigv4",
      s"${SIGV4_SERVICE.optionKey}" -> "aoss",
      s"${REFRESH_POLICY.optionKey}" -> "false",
      s"${IGNORE_DOC_ID_COLUMN.optionKey}" -> "false")

  override def beforeAll(): Unit = {
    // container.start()
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    // container.close()
    super.afterAll()
  }

  /**
   * Delete index `indexNames` after calling `f`.
   */
  protected def withIndexName(indexNames: String*)(f: => Unit): Unit = {
    try {
      f
    } finally {
      indexNames.foreach { indexName =>
        val deleteRequest = new Request("DELETE", "/" + indexName)
        openSearchClient.getLowLevelClient.performRequest(deleteRequest)
      }
    }
  }

  val oneNodeSetting = """{
                         |  "number_of_shards": "1",
                         |  "number_of_replicas": "0"
                         |}""".stripMargin

  def simpleIndex(indexName: String): Unit = {
    val mappings = """{
                     |  "properties": {
                     |    "accountId": {
                     |      "type": "keyword"
                     |    },
                     |    "eventName": {
                     |      "type": "keyword"
                     |    },
                     |    "eventSource": {
                     |      "type": "keyword"
                     |    }
                     |  }
                     |}""".stripMargin
    val docs = Seq("""{
                     |  "accountId": "123",
                     |  "eventName": "event",
                     |  "eventSource": "source"
                     |}""".stripMargin)
    index(indexName, oneNodeSetting, mappings, docs)
  }

  def multipleDocIndex(indexName: String, N: Int): Unit = {
    val mappings = """{
                     |  "properties": {
                     |    "id": {
                     |      "type": "integer"
                     |    }
                     |  }
                     |}""".stripMargin

    val docs = for (n <- 1 to N) yield s"""{"id": $n}""".stripMargin
    index(indexName, oneNodeSetting, mappings, docs)
  }

  def index(index: String, settings: String, mappings: String, docs: Seq[String]): Unit = {
    openSearchClient.indices.create(
      new CreateIndexRequest(index)
        .settings(settings, XContentType.JSON)
        .mapping(mappings, XContentType.JSON),
      RequestOptions.DEFAULT.toBuilder.addHeader("x-amz-content-sha256", "required").build())

    val getIndexResponse =
      openSearchClient
        .indices()
        .get(
          new GetIndexRequest(index),
          RequestOptions.DEFAULT.toBuilder.addHeader("x-amz-content-sha256", "required").build())
    assume(getIndexResponse.getIndices.contains(index), s"create index $index failed")

    /**
     *   1. Wait until refresh the index.
     */
    if (docs.nonEmpty) {
      val request = new BulkRequest().setRefreshPolicy(RefreshPolicy.NONE)
      for (doc <- docs) {
        request.add(new IndexRequest(index).source(doc, XContentType.JSON))
      }

      val response =
        openSearchClient.bulk(
          request,
          RequestOptions.DEFAULT.toBuilder.addHeader("x-amz-content-sha256", "required").build())

      assume(
        !response.hasFailures,
        s"bulk index docs to $index failed: ${response.buildFailureMessage()}")
    }
  }
}
