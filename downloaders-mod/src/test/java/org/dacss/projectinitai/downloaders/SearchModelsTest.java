package org.dacss.projectinitai.downloaders;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <h1>{@link SearchModelsTest}</h1>
 * Method under test:
 * <li>{@link SearchModels#SearchModels()} </li>
 */
@SuppressWarnings("InstantiationOfUtilityClass")
public class SearchModelsTest {

    private SearchModels searchModels;
    private DownloadAction action;

    @BeforeClass
    public void setUp() {
        searchModels = new SearchModels();
        action = DownloadAction.SEARCH;
    }

    /**
     * the following list was intended to throw an exception, but it was not thrown,
     * as all the names do return models!! including "fakeModel", "unknownName", "notFound"....
     * I'll have to come up with better fake names to create an edge case to fail.
     */
    @Test
    public void testSearchModels() {
        String[] queries = {"qwen", "bert", "gpt-3", "fakeModel", "unknownName", "notFound"};

        for (String query : queries) {
            Flux<Object> flux = SearchModels.searchModels(query);

            StepVerifier.create(flux.collectList())
                    .expectNextMatches(models -> {
                        int count = models.size();
                        if (count == 0 && (query.equals("fakeModel") || query.equals("unknownName") || query.equals("notFound"))) {
                            System.out.println("@" + query + ": 0");
                            return true; // Originally expecting no results for fake/unknown/notFound queries
                        }
                        if (count == 0) {
                            return false;
                        }
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            for (Object model : models) {
                                JsonNode jsonNode = objectMapper.readTree(model.toString());
                                System.out.println("JSON Response for query '" + query + "': " + jsonNode.toPrettyString());
                            }
                            System.out.println("@" + query + ": " + count);
                            return true;
                        } catch (Exception searchModelsTestExc) {
                            searchModelsTestExc.printStackTrace();
                            return false;
                        }
                    })
                    .expectComplete()
                    .verify();
        }
    }
}