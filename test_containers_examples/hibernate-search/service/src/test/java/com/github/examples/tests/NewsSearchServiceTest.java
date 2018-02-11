package com.github.examples.tests;

import com.github.examples.config.BeanConfiguration;
import com.github.examples.config.JpaConfig;
import com.github.examples.config.TestDataSource;
import com.github.examples.interfaces.NewsSearchService;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.Wait;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestDataSource.class, JpaConfig.class, BeanConfiguration.class})
public class NewsSearchServiceTest {

//    test container @see https://www.testcontainers.org/
//    it embedded elasticsearch for saving indexes in elastic for improving full text search
    @ClassRule
    public static GenericContainer elasticContainer =
            new FixedHostPortGenericContainer("docker.elastic.co/elasticsearch/elasticsearch:5.5.2")
                    .withFixedExposedPort(9201, 9200)
                    .withFixedExposedPort(9300, 9300)
                    .waitingFor(Wait.forHttp("/")) // Wait until elastic start
                    .withEnv("xpack.security.enabled", "false")
                    .withEnv("http.host", "0.0.0.0")
                    .withEnv("transport.host", "127.0.0.1");

    @Autowired
    @Qualifier("newsSearchService")
    private NewsSearchService searchService;


    @Test
    public void testElasticEmbedded() throws ClassNotFoundException {
        Map<String,String>searchValues=new HashMap<>();
        searchValues.put("urlFormat","www");
        Page result=searchService.searchByMatchingField(0,10,null,null,searchValues, "com.github.examples.model.NewsParseRule");
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getContent().size());
    }
}
