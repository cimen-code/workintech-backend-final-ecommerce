package com.workintech.ecommerce.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OffsetPageRequestTest {

    private final OffsetPageRequest page = new OffsetPageRequest(50, 25, Sort.by("id"));

    @Test
    void keepsExactOffset() {
        assertEquals(50, page.getOffset());
        assertEquals(25, page.getPageSize());
        assertEquals(2, page.getPageNumber());
    }

    @Test
    void offsetNeedNotBeMultipleOfLimit() {
        assertEquals(2, new OffsetPageRequest(2, 25, Sort.unsorted()).getOffset());
    }

    @Test
    void navigation() {
        assertEquals(75, page.next().getOffset());
        assertEquals(25, page.previousOrFirst().getOffset());
        assertEquals(0, page.first().getOffset());
        assertEquals(25, page.withPage(1).getOffset());
        assertTrue(page.hasPrevious());
        assertFalse(page.first().hasPrevious());
    }
}
