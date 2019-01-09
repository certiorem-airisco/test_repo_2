package com.pruebasJasmine.app;


import org.junit.Before;
import org.junit.Test;

public class TestableTest {

    private Testable testable;

    @Before
    public void setUp() {
        testable = new Testable();
    }

    @Test
    public void testable1EmptyData() {
        testable.testable1(null);

    }

    @Test
    public void testable1() {
        testable.testable1("Dato de prueba");

    }
}