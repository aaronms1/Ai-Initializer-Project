package org.dacss.projectinitai.system;

import org.dacss.projectinitai.system.StorageCapSettings;
import org.testng.annotations.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * <h1>{@link StorageCapSettingsTest}</h1>
 * Methods under test:
 * <ul>
 *     <li>{@link StorageCapSettings#getTotalDiskSize()}</li>
 *     <li>{@link StorageCapSettings#getAllocatedDiskSpace()}</li>
 *     <li>{@link StorageCapSettings#getDefaultTotalSpaceAllocated()}</li>
 *     <li>{@link StorageCapSettings#getMinimumSpaceAllocation()}</li>
 *     <li>{@link StorageCapSettings#getResults()}</li>
 * </ul>
 */
public class StorageCapSettingsTest {

    @Test
    public void testGetTotalDiskSize() {
        Flux<Long> result = StorageCapSettings.getTotalDiskSize();
        result.doOnNext(total -> System.out.println("Total Disk Size: " + total + " bytes")).subscribe();
        StepVerifier.create(result)
                .expectNextMatches(total -> total > 0)
                .expectNextMatches(total -> total > 0)
                .verifyComplete();
    }

    @Test
    public void testGetAllocatedDiskSpace() {
        Flux<Long> result = StorageCapSettings.getAllocatedDiskSpace();
        result.doOnNext(allocated -> System.out.println("Allocated Disk Space: " + allocated + " bytes")).subscribe();
        StepVerifier.create(result)
                .expectNextMatches(allocated -> allocated >= 0)
                .expectNextMatches(allocated -> allocated >= 0)
                .verifyComplete();
    }

    @Test
    public void testGetDefaultTotalSpaceAllocated() {
        Flux<Long> result = StorageCapSettings.getDefaultTotalSpaceAllocated();
        result.doOnNext(allowable -> System.out.println("Default Total Space Allowable: " + allowable + " bytes")).subscribe();
        StepVerifier.create(result)
                .expectNextMatches(defaultAllocated -> defaultAllocated >= 0)
                .expectNextMatches(defaultAllocated -> defaultAllocated >= 0)
                .verifyComplete();
    }

    @Test
    public void testGetMinimumSpaceAllocation() {
        Mono<Long> result = StorageCapSettings.getMinimumSpaceAllocation();
        result.doOnNext(minimum -> System.out.println("Minimum Space Required: " + minimum + " bytes")).subscribe();
        StepVerifier.create(result)
                .expectNextMatches(minimum -> minimum == 20L * 1024 * 1024 * 1024)
                .verifyComplete();
    }

//    @Test
//    public void testGetResults() {
    //todo: reimplement, this test. its purpose is to return all stats for the frontend
//        Flux<Object> result = StorageCapSettings.getResults();
//        result.doOnNext(System.out::println).subscribe();
//        StepVerifier.create(result)
//                .expectNextMatches(res -> res instanceof Long && (Long) res > 0)
//                .expectNextMatches(res -> res instanceof Long && (Long) res >= 0)
//                .expectNextMatches(res -> res instanceof Long && (Long) res >= 0)
//                .expectNextMatches(res -> res instanceof Long && (Long) res == 20L * 1024 * 1024 * 1024)
//                .verifyComplete();
//    }
}
