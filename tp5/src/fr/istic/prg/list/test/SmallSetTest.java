package fr.istic.prg.list.test;

import org.junit.Test;

import fr.istic.prg.list.SmallSet;

import static org.junit.Assert.*;

public class SmallSetTest {

    @Test
    public void testInitialState() {
        SmallSet set = new SmallSet();
        assertTrue("Set should be empty initially", set.isEmpty());
        assertEquals("Size should be 0 initially", 0, set.size());
    }

    @Test
    public void testAdd() {
        SmallSet set = new SmallSet();
        set.add(5);
        assertTrue("Set should contain 5 after adding", set.contains(5));
        assertEquals("Size should be 1 after adding one element", 1, set.size());
    }

    @Test
    public void testRemove() {
        SmallSet set = new SmallSet();
        set.add(10);
        set.remove(10);
        assertFalse("Set should not contain 10 after removal", set.contains(10));
        assertEquals("Size should be 0 after removing the only element", 0, set.size());
    }

    @Test
    public void testAddInterval() {
        SmallSet set = new SmallSet();
        set.addInterval(20, 25);
        for (int i = 20; i <= 25; i++) {
            assertTrue("Set should contain " + i + " after adding interval", set.contains(i));
        }
        assertEquals("Size should be 6 after adding interval 20-25", 6, set.size());
    }

    @Test
    public void testRemoveInterval() {
        SmallSet set = new SmallSet();
        set.addInterval(10, 15);
        set.removeInterval(12, 14);
        assertFalse("Set should not contain 12 after removal", set.contains(12));
        assertFalse("Set should not contain 13 after removal", set.contains(13));
        assertTrue("Set should still contain 10", set.contains(10));
        assertTrue("Set should still contain 15", set.contains(15));
        assertEquals("Size should be 3 after removing interval 12-14", 3, set.size());
    }

    @Test
    public void testUnion() {
        SmallSet set1 = new SmallSet();
        SmallSet set2 = new SmallSet();
        set1.add(1);
        set1.add(2);
        set2.add(2);
        set2.add(3);
        set1.union(set2);
        assertTrue("Set 1 should contain 1 after union", set1.contains(1));
        assertTrue("Set 1 should contain 2 after union", set1.contains(2));
        assertTrue("Set 1 should contain 3 after union", set1.contains(3));
        assertEquals("Size should be 3 after union", 3, set1.size());
    }

    @Test
    public void testIntersection() {
        SmallSet set1 = new SmallSet();
        SmallSet set2 = new SmallSet();
        set1.add(1);
        set1.add(2);
        set2.add(2);
        set2.add(3);
        set1.intersection(set2);
        assertFalse("Set 1 should not contain 1 after intersection", set1.contains(1));
        assertTrue("Set 1 should contain 2 after intersection", set1.contains(2));
        assertEquals("Size should be 1 after intersection", 1, set1.size());
    }

    @Test
    public void testDifference() {
        SmallSet set1 = new SmallSet();
        SmallSet set2 = new SmallSet();
        set1.add(1);
        set1.add(2);
        set2.add(2);
        set2.add(3);
        set1.difference(set2);
        assertFalse("Set 1 should not contain 2 after difference", set1.contains(2));
        assertTrue("Set 1 should still contain 1 after difference", set1.contains(1));
        assertEquals("Size should be 1 after difference", 1, set1.size());
    }

    @Test
    public void testSymmetricDifference() {
        SmallSet set1 = new SmallSet();
        SmallSet set2 = new SmallSet();
        set1.add(1);
        set1.add(2);
        set2.add(2);
        set2.add(3);
        set1.symmetricDifference(set2);
        assertTrue("Set 1 should contain 1 after symmetric difference", set1.contains(1));
        assertTrue("Set 1 should contain 3 after symmetric difference", set1.contains(3));
        assertFalse("Set 1 should not contain 2 after symmetric difference", set1.contains(2));
        assertEquals("Size should be 2 after symmetric difference", 2, set1.size());
    }

    @Test
    public void testComplement() {
        SmallSet set = new SmallSet();
        set.add(1);
        set.complement();
        assertFalse("Set should not contain 1 after complement", set.contains(1));
        assertEquals("Size should be 255 after complement (0-255 excluding 1)", 255, set.size());
    }

    @Test
    public void testClear() {
        SmallSet set = new SmallSet();
        set.add(5);
        set.clear();
        assertTrue("Set should be empty after clear", set.isEmpty());
        assertEquals("Size should be 0 after clear", 0, set.size());
    }

    @Test
    public void testIsIncludedIn() {
        SmallSet set1 = new SmallSet();
        set1.add(1);
        set1.add(2);
        SmallSet set2 = new SmallSet();
        set2.add(1);
        set2.add(2);
        set2.add(3);
        SmallSet set3 = new SmallSet();
        set3.add(1);

        assertTrue("Set 1 should be included in Set 2", set1.isIncludedIn(set2));
        assertFalse("Set 2 should not be included in Set 1", set2.isIncludedIn(set1));
        assertTrue("Set 3 should be included in Set 2", set3.isIncludedIn(set2));
        assertFalse("Set 2 should not be included in Set 3", set2.isIncludedIn(set3));
    }

    @Test
    public void testEquals() {
        SmallSet set1 = new SmallSet();
        set1.add(5);
        SmallSet set2 = new SmallSet();
        set2.add(5);
        SmallSet set3 = new SmallSet();
        set3.add(10);

        assertTrue("Set 1 should be equal to Set 2", set1.equals(set2));
        assertFalse("Set 1 should not be equal to Set 3", set1.equals(set3));
    }

    @Test
    public void testCopyOf() {
        SmallSet set1 = new SmallSet();
        set1.add(5);
        SmallSet set2 = set1.copyOf();
        assertTrue("Copied set should contain 5", set2.contains(5));
        assertEquals("Copied set size should be 1", 1, set2.size());
    }

    @Test
    public void testToString() {
        SmallSet set = new SmallSet();
        set.add(1);
        set.add(2);
        String expected = "elements presents : 1 2 ";
        assertEquals("toString should return correct representation", expected, set.toString());
    }
}
