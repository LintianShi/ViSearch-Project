package test;

import datatype.MyHashMap;
import trace.Behaviour;
import validation.Specification;
import validation.Validation;

import java.util.Set;

public class TestVisibilityRelaxationCheck {
    public void testVisibility(String predicate) {
        /*Visibility Relaxation Check*/
        Validation vv = new Validation();
        vv.loadTrace("test1.json");
        Specification specification = new Specification();
        specification.setSpecification("put", "COMPLETE");
        specification.setSpecification("contains", predicate);
        //specification.setSpecification("contains", "MONOTONIC");
        //specification.setSpecification("contains", "PEER");
        Set<Behaviour> behaviours = vv.visibilityRelaxationCheck(specification, new MyHashMap());
        System.out.println("=======================" + predicate + "======================");
        System.out.println(behaviours);
    }

    public static void main(String[] args) {
        TestVisibilityRelaxationCheck test = new TestVisibilityRelaxationCheck();
        test.testVisibility("WEAK");
        test.testVisibility("BASIC");
        test.testVisibility("MONOTONIC");
        test.testVisibility("PEER");
        test.testVisibility("CAUSAL");
        test.testVisibility("COMPLETE");
    }
}
